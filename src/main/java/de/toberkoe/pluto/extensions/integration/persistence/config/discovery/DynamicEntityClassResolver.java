package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import org.apache.log4j.Logger;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class DynamicEntityClassResolver implements EntityClassResolver {

    private static final Logger logger = Logger.getLogger("de.pluto.config");

    private final String persistenceUnit;
    private final Map<String, Set<Class<?>>> entityClasses;
    private final Map<String, Set<Class<?>>> alreadyScannedClasses;
    private Class<?> testClass;

    public DynamicEntityClassResolver(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        entityClasses = new HashMap<>();
        alreadyScannedClasses = new HashMap<>();
    }

    @Override
    public void resolve(Class<?> testClass) {
        this.testClass = testClass;
        alreadyScannedClasses.clear();
        scanHierarchyForEntities(persistenceUnit, testClass);
        entityClasses.forEach(this::scanHierarchyForEntities);
    }

    private void scanHierarchyForEntities(String persistenceUnit, Collection<Class<?>> classes) {
        classes.forEach(c -> scanHierarchyForEntities(persistenceUnit, c));
    }

    private void scanHierarchyForEntities(String persistenceUnit, Class<?> targetClass) {
        if (alreadyScannedClasses.getOrDefault(persistenceUnit, Set.of()).contains(targetClass)) {
            return;
        }
        alreadyScannedClasses.merge(persistenceUnit, Set.of(targetClass), this::merge);

        if (isTargetAssociatedToTestClass(targetClass)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Scanning " + targetClass + " for referenced entity classes");
            }

            if (targetClass.isAnnotationPresent(Entity.class)) {
                putEntityClass(persistenceUnit, targetClass);
            }

            scanFieldsOf(persistenceUnit, targetClass);
            scanGenericsOf(persistenceUnit, targetClass);
            scanSuperclassOf(persistenceUnit, targetClass);
            scanMethodResultsOf(persistenceUnit, targetClass);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring " + targetClass);
            }
        }
    }

    private boolean isTargetAssociatedToTestClass(Class<?> targetClass) {
        String prefix = Stream.of(testClass.getPackageName().split("\\.")).limit(2).collect(joining("."));
        return targetClass.getPackageName().startsWith(prefix);
    }

    private void scanMethodResultsOf(String persistenceUnit, Class<?> targetClass) {
        Stream<Method> publicMethods = Stream.of(targetClass.getMethods());
        Stream<Method> declaredMethods = Stream.of(targetClass.getDeclaredMethods());

        Stream.concat(publicMethods, declaredMethods)
                .map(Method::getReturnType)
                .forEach(t -> scanHierarchyForEntities(persistenceUnit, t));
    }

    private void scanSuperclassOf(String persistenceUnit, Class<?> targetClass) {
        if (targetClass.getSuperclass() != null) {
            Class<?> superClass = targetClass.getSuperclass();
            scanGenericsOf(persistenceUnit, superClass);
            scanHierarchyForEntities(persistenceUnit, superClass);
        }
    }

    private void scanGenericsOf(String persistenceUnit, Class<?> targetClass) {
        Optional.ofNullable(targetClass.getGenericSuperclass()).stream()
                .filter(t -> t instanceof ParameterizedType)
                .map(t -> (ParameterizedType) t)
                .map(pt -> pt.getActualTypeArguments()[0])
                .map(Type::getTypeName)
                .findAny().ifPresent(className -> putEntityClassByName(persistenceUnit, className));
    }

    private void putEntityClassByName(String persistenceUnit, String className) {
        try {
            Class<?> entityClass = Class.forName(className);
            putEntityClass(persistenceUnit, entityClass);
        } catch (ClassNotFoundException e) {
            StringBuilder builder = new StringBuilder("Unable to resolve generic entity '")
                    .append(className)
                    .append("'. Consider explicit declaration ")
                    .append(" using static method annotated with @EntityClassProvider");
            throw new EntityDiscoveryException(builder.toString());
        }
    }

    private void scanFieldsOf(String persistenceUnit, Class<?> targetClass) {
        Stream<Field> publicFields = Stream.of(targetClass.getFields());
        Stream<Field> declaredFields = Stream.of(targetClass.getDeclaredFields());
        Stream.concat(publicFields, declaredFields)
                .map(Field::getType)
                .forEach(t -> scanHierarchyForEntities(persistenceUnit, t));
    }

    public void putEntityClasses(Map<String, Set<Class<?>>> classes) {
        this.entityClasses.putAll(classes);
    }

    public void putEntityClass(String persistenceUnit, Class<?> entityClass) {
        if (logger.isDebugEnabled()) {
            logger.debug("Add entity class " + entityClass + " for persistence unit " + persistenceUnit);
        }

        entityClasses.merge(persistenceUnit, Set.of(entityClass), this::merge);
    }

    @Override
    public Set<Class<?>> getEntityClasses(String persistenceUnit) {
        return entityClasses.getOrDefault(persistenceUnit, Set.of());
    }

    @Override
    public Map<String, Set<Class<?>>> getEntityClasses() {
        return entityClasses;
    }
}
