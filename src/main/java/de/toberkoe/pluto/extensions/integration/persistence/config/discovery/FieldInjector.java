package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import de.toberkoe.pluto.extensions.integration.persistence.config.InjectPersistence;
import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class FieldInjector {

    private static final Logger logger = Logger.getLogger("de.pluto.config");
    private static final List<Class<? extends Annotation>> injectableAnnotations = List.of(Inject.class, InjectPersistence.class, EJB.class);

    private Object target;
    private List<Field> fields;

    private FieldInjector() {
    }

    public static FieldInjector of(Object target) {
        FieldInjector injector = new FieldInjector();
        injector.setTarget(target);
        injector.setFields(collectFields(target.getClass()));
        return injector;
    }

    private static List<Field> collectFields(Class<?> targetClass) {
        if (targetClass == null) {
            return new ArrayList<>();
        }

        Stream<Field> publicFields = Stream.of(targetClass.getFields());
        Stream<Field> declaredFields = Stream.of(targetClass.getDeclaredFields());

        List<Field> fields = Stream.concat(publicFields, declaredFields).collect(toList());
        fields.addAll(collectFields(targetClass.getSuperclass()));
        return fields;
    }

    public void inject() {
        if (logger.isDebugEnabled()) {
            logger.debug("Injecting fields of " + target.getClass().getName());
        }
        injectEntityManager();
        injectableAnnotations.forEach(this::injectFieldsAnnotatedWith);
    }

    private void injectEntityManager() {
        List<Field> managerFields = fields.stream()
                .filter(f -> f.getType().equals(EntityManager.class))
                .filter(f -> getValue(f) == null)
                .collect(toList());

        for (Field field : managerFields) {
            Optional<String> unitName = getUnitNameFromAnnotations(field);
            setValue(field, requireNonNull(PersistenceManager.INSTANCE.getInstanceOfEntityManager(unitName)));
        }
    }

    private Optional<String> getUnitNameFromAnnotations(Field field) {
        PersistenceContext context = field.getAnnotation(PersistenceContext.class);
        if (context != null) {
            return Optional.of(context.unitName());
        }

        return Stream.of(field.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .filter(a -> a.isAnnotationPresent(Qualifier.class))
                .map(Class::getName)
                .findAny();
    }

    private void injectFieldsAnnotatedWith(Class<? extends Annotation> annotationClass) {
        fields.stream()
                .filter(f -> f.isAnnotationPresent(annotationClass))
                .filter(f -> !f.getType().equals(EntityManager.class))
                .filter(f -> getValue(f) == null)
                .forEach(this::injectField);
    }

    private void injectField(Field field) {
        field.setAccessible(true);
        Object value = createValueInstance(field.getType());
        invokePostConstructs(value);
        setValue(field, value);
        //FIXME optimize -> check IntegrationTestConfig if subsequent init is demanded
        //FIXME optimize -> check IntegrationTestConfig for specific demanded subsequent init fields
        FieldInjector.of(value).inject();
    }

    private void invokePostConstructs(Object value) {
        Map<Integer, Method> methods = collectPostConstructs(value.getClass());

        List<Integer> layers = new ArrayList<>(methods.keySet());
        Collections.reverse(layers);

        layers.stream()
                .map(methods::get)
                .forEach(m -> invokePostConstruct(value, m));
    }

    private void invokePostConstruct(Object value, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to call PostConstruct method " + value.getClass().getName() + "." + method.getName(), e);
        }
    }

    private Map<Integer, Method> collectPostConstructs(Class<?> targetClass) {
        return collectPostConstructs(targetClass, 0);
    }

    private Map<Integer, Method> collectPostConstructs(Class<?> targetClass, int layer) {
        Map<Integer, Method> methods = new TreeMap<>();
        if (targetClass.getSuperclass() != null) {
            methods.putAll(collectPostConstructs(targetClass.getSuperclass(), layer + 1));
        }

        Stream<Method> declaredMethods = Stream.of(targetClass.getDeclaredMethods());
        Stream<Method> publicMethods = Stream.of(targetClass.getMethods());

        Stream.concat(declaredMethods, publicMethods)
                .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                .forEach(m -> methods.put(layer, m));
        return methods;
    }

    private <E> E createValueInstance(Class<E> valueClass) {
        //FIXME support for interceptors?
        //FIXME use producer / provider / suppliers first, try direct creation at last
        try {
            Constructor<E> constructor = valueClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create instance of " + valueClass + ". No matching constructor found", e);
        }
    }

    private void setValue(Field field, Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("Set Value " + value + " to field " + field.getDeclaringClass().getSimpleName() + "." + field.getName());
        }

        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field " + target.getClass().getName() + "." + field.getName());
        }
    }

    private <E> E getValue(Field field) {
        try {
            field.setAccessible(true);
            return (E) field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field " + target.getClass().getName() + "." + field.getName());
        }
    }

    void setTarget(Object target) {
        this.target = target;
    }

    void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
