package de.toberkoe.pluto.extensions.integration.persistence.config;

import de.toberkoe.pluto.extensions.integration.persistence.config.database.DataSourceProvider;
import de.toberkoe.pluto.extensions.integration.persistence.config.database.Database;
import de.toberkoe.pluto.extensions.integration.persistence.config.discovery.FieldInjector;
import de.toberkoe.pluto.extensions.internal.Throwables;
import org.apache.log4j.Logger;
import org.hibernate.AnnotationException;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceManager {

    private static final Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger("de.pluto.config");
    private static final Map<String, EntityManager> managers = new ConcurrentHashMap<>();
    public static PersistenceManager INSTANCE = new PersistenceManager();
    private PersistenceConfig config;

    public void injectAll(Optional<Object> object) {
        object.stream()
                .map(FieldInjector::of)
                .forEach(FieldInjector::inject);
    }

    public EntityManager getInstanceOfEntityManager(Optional<String> persistenceUnit) {
        String unit = persistenceUnit.orElse("");
        if (unit.trim().isEmpty()) {
            unit = config.getDefaultUnitName();
        }

        return managers.computeIfAbsent(unit, u -> getEntityManagerFactory(u).createEntityManager());
    }

    private EntityManagerFactory getEntityManagerFactory(String unit) {
        if (!factories.containsKey(unit)) {
            factories.put(unit, initFactory(unit, Database.getDefault()));
        }
        return factories.get(unit);
        //FIXME configurable behaviour on missing factory for given unit
        //default behaviour: create if missing
    }

    public void init(PersistenceConfig config) {
        this.config = config;
        config.getDatabases().forEach(this::initFactory);
    }

    private synchronized EntityManagerFactory initFactory(String persistenceUnit, Database database) {
        EntityManagerFactory factory = tryToCreateFactory(persistenceUnit, database, new HashMap<>());
        factories.put(persistenceUnit, factory);

        if (logger.isDebugEnabled()) {
            logger.debug("Successfully initialized EntityManagerFactory for unit " + persistenceUnit);
        }
        return factories.get(persistenceUnit);
    }

    private EntityManagerFactory tryToCreateFactory(String persistenceUnit, Database database, HashMap<Object, Object> map) {
        try {
            Properties properties = getProperties(database);
            PersistenceUnitSettings settings = new PersistenceUnitSettings(persistenceUnit, config.getEntityClassNames(persistenceUnit), properties);
            EntityManagerFactory factory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(settings, new HashMap<>());
            validateEntities(persistenceUnit, factory.createEntityManager());
            return factory;
        } catch (Exception e) {
            Optional<AnnotationException> exception = Throwables.extractCause(e, AnnotationException.class);
            if (exception.isPresent()) {
                AnnotationException ex = exception.get();
                resolveMissingEntityClass(persistenceUnit, ex);
                return tryToCreateFactory(persistenceUnit, database, map);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateEntities(String persistenceUnit, EntityManager manager) {
        long count = config.getEntityClasses(persistenceUnit).stream()
                .map(cl -> manager.createQuery("SELECT e FROM " + cl.getName() + " e", cl))
                .map(q -> q.setMaxResults(1))
                .map(TypedQuery::getResultList)
                .count();

        if (logger.isDebugEnabled()) {
            logger.debug("Validated " + count + " entities");
        }
    }

    private void resolveMissingEntityClass(String persistenceUnit, AnnotationException ex) {
        String className = ex.getMessage().substring(ex.getMessage().lastIndexOf(":") + 1).trim();
        addEntityClass(persistenceUnit, className);
    }

    private void addEntityClass(String persistenceUnit, String className) {
        try {
            config.putEntityClass(persistenceUnit, Class.forName(className));
        } catch (ClassNotFoundException e) {
            if (className.endsWith("]")) {
                className = className.substring(className.lastIndexOf("[") + 1);
                className = className.substring(0, className.lastIndexOf("]"));
                className = className.replaceAll("/", ".");
                addEntityClass(persistenceUnit, className);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    //FIXME create more flexible data source
    private Properties getProperties(Database database) {
        DataSourceProvider provider = database.dataSourceProvider();
        Properties properties = new Properties();
        properties.put("hibernate.dialect", provider.hibernateDialect());
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.connection.datasource", provider.dataSource());
        properties.put("hibernate.generate_statistics", "true");
        return properties;
    }

    public void close() {
        if (logger.isDebugEnabled()) {
            factories.keySet().forEach(unit -> logger.debug("Closing entity manager factory " + unit));
        }

        factories.values().forEach(EntityManagerFactory::close);
        factories.clear();
    }

}
