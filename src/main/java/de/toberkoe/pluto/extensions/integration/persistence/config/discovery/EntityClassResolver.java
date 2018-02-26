package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public interface EntityClassResolver {

    void resolve(Class<?> entryClass);

    default Set<Class<?>> merge(Set<Class<?>> first, Set<Class<?>> second) {
        return Stream.concat(first.stream(), second.stream()).collect(toSet());
    }

    Set<Class<?>> getEntityClasses(String persistenceUnit);

    Map<String, Set<Class<?>>> getEntityClasses();
}
