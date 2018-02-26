package de.toberkoe.pluto.extensions.integration.persistence.config;

import de.toberkoe.pluto.extensions.integration.persistence.config.database.Database;
import de.toberkoe.pluto.extensions.integration.persistence.config.database.UseDatabase;
import de.toberkoe.pluto.extensions.integration.persistence.config.database.UseDatabases;
import de.toberkoe.pluto.extensions.integration.persistence.config.discovery.*;
import de.toberkoe.pluto.extensions.integration.persistence.config.log.Log;
import de.toberkoe.pluto.extensions.integration.persistence.config.log.LogConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersistenceConfig {


    private final Class<?> testClass;
    private final Map<String, Database> databases = new HashMap<>();
    private EntityClassResolver entityResolver;

    private PersistenceConfig(Class<?> testClass) {
        this.testClass = testClass;
    }

    public static PersistenceConfig build(Class<?> testClass) {
        PersistenceConfig config = new PersistenceConfig(testClass);
        config.load();
        return config;
    }

    private void load() {
        resolveLogLevel();
        resolveDatabases();
        resolveEntityDiscoveryMode();
    }

    private void resolveLogLevel() {
        LogConfig.configure(testClass.getAnnotation(Log.class));
    }

    private void resolveDatabases() {
        UseDatabases dbDefinitions = testClass.getAnnotation(UseDatabases.class);
        if (dbDefinitions != null) {
            Stream.of(dbDefinitions.values()).forEach(this::resolveDatabase);
        } else {
            resolveDatabase(testClass.getAnnotation(UseDatabase.class));
        }
    }

    private void resolveDatabase(UseDatabase db) {
        if (db == null) {
            databases.put("", Database.HSQLDB);
        } else {
            databases.put(db.forPersistenceUnit(), db.value());
        }
    }

    private void resolveEntityDiscoveryMode() {
        EntityDiscoveryMode mode = testClass.getAnnotation(EntityDiscoveryMode.class);
        Strategy discoveryMode = Strategy.DYNAMIC;
        if (mode != null) {
            discoveryMode = mode.value();
        }

        switch (discoveryMode) {
            case DYNAMIC:
                entityResolver = new DynamicEntityClassResolver(getDefaultPersistenceUnitName());
                EntityClassResolver resolver = new StaticEntityClassResolver(false);
                resolver.resolve(testClass);
                ((DynamicEntityClassResolver) entityResolver).putEntityClasses(resolver.getEntityClasses());
                break;
            case STATIC:
                entityResolver = new StaticEntityClassResolver(true);
                break;
        }
        entityResolver.resolve(testClass);
    }

    private String getDefaultPersistenceUnitName() {
        return databases.keySet().stream().findFirst().orElse("");
    }

    public void putEntityClass(String persistenceUnit, Class<?> entityClass) {
        if (isDynamicDiscoveryMode()) {
            ((DynamicEntityClassResolver) entityResolver).putEntityClass(persistenceUnit, entityClass);
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("Adding entities in static discovery mode is not allowed.\n")
                    .append("Consider explicit declaration of entity ")
                    .append(entityClass.getName())
                    .append(" using static method annotated with @EntityClassProvider ")
                    .append("or switching to dynamic discovery mode by using @EntityDiscoveryMode(Strategy.DYNAMIC) on test class");
            throw new EntityDiscoveryException(builder.toString());
        }
    }

    public List<String> getEntityClassNames(String persistenceUnit) {
        return getEntityClasses(persistenceUnit).stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    public Set<Class<?>> getEntityClasses(String persistenceUnit) {
        return entityResolver.getEntityClasses(persistenceUnit);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    private boolean isDynamicDiscoveryMode() {
        return entityResolver instanceof DynamicEntityClassResolver;
    }
}
