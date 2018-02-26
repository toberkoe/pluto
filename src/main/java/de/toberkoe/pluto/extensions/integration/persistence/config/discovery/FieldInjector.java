package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import de.toberkoe.pluto.extensions.integration.persistence.config.InjectPersistence;
import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceManager;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FieldInjector {

    private static final Logger logger = Logger.getLogger("de.pluto.config");
    private static final List<Class<? extends Annotation>> injectableAnnotations = List.of(Inject.class, InjectPersistence.class, EJB.class);

    private Object target;
    private List<Field> fields;

    FieldInjector() {
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
                .filter(f -> f.getType() == EntityManager.class)
                .filter(f -> getValue(f) == null)
                .collect(toList());

        for (Field field : managerFields) {
            PersistenceContext context = field.getAnnotation(PersistenceContext.class);
            Optional<String> unitName = Optional.ofNullable(context).map(PersistenceContext::unitName);
            setValue(field, PersistenceManager.getInstanceOfEntityManager(unitName));
        }
    }

    private void injectFieldsAnnotatedWith(Class<? extends Annotation> annotationClass) {
        fields.stream()
                .filter(f -> f.isAnnotationPresent(annotationClass))
                .filter(f -> getValue(f) == null)
                .forEach(this::injectField);
    }

    private void injectField(Field field) {
        field.setAccessible(true);
        Object value = createValueInstance(field.getType());
        setValue(field, value);
        //FIXME optimize -> check IntegrationTestConfig if subsequent init is demanded
        //FIXME optimize -> check IntegrationTestConfig for specific demanded subsequent init fields
        FieldInjector.of(value).inject();
    }

    private <E> E createValueInstance(Class<E> valueClass) {
        //FIXME support for interceptors?
        //FIXME use producer / provider / suppliers first, try direct creation at last
        try {
            Constructor<E> constructor = valueClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create instance of " + valueClass + ". No matching constructor found");
        }
    }

    private void setValue(Field field, Object value) {
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
