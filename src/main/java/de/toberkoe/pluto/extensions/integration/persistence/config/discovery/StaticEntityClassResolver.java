package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import javafx.util.Pair;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StaticEntityClassResolver implements EntityClassResolver {

    private final Map<String, Set<Class<?>>> entityClasses;
    private final boolean throwExceptionOnMissingProvider;

    public StaticEntityClassResolver(boolean throwExceptionOnMissingProvider) {
        this.entityClasses = new HashMap<>();
        this.throwExceptionOnMissingProvider = throwExceptionOnMissingProvider;
    }

    @Override
    public void resolve(Class<?> entryClass) {
        List<Method> providers = Stream.of(entryClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(EntityClassProvider.class))
                .collect(toList());

        if (providers.isEmpty() && throwExceptionOnMissingProvider) {
            StringBuilder builder = new StringBuilder()
                    .append("No static methods annotated with @EntityClassProvider found.\n")
                    .append("Consider explicit declaration of entity classes using static method annotated with @EntityClassProvider ")
                    .append("or switching to dynamic discovery mode by using @EntityDiscoveryMode(Strategy.DYNAMIC) on test class");
            throw new EntityDiscoveryException(builder.toString());
        }

        providers.stream()
                .map(this::getEntityClassesFromMethod)
                .forEach(p -> entityClasses.merge(p.getKey(), p.getValue(), this::merge));
    }

    private Pair<String, Set<Class<?>>> getEntityClassesFromMethod(Method method) {
        EntityClassProvider annotation = method.getAnnotation(EntityClassProvider.class);
        String persistenceUnit = annotation.forPersistenceUnit();

        try {
            method.setAccessible(true);
            Object result = method.invoke(null);

            Set<Class<?>> classes = new HashSet<>();
            if (result instanceof Collection) {
                classes.addAll((Collection<? extends Class<?>>) result);
            } else if (result instanceof Class<?>[]) {
                Class<?>[] array = (Class<?>[]) result;
                classes.addAll(Arrays.asList(array));
            }

            return new Pair<>(persistenceUnit, classes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
