package de.toberkoe.pluto.extensions.integration.persistence;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import de.toberkoe.pluto.examples.integration.Hobby;
import de.toberkoe.pluto.examples.integration.Job;
import de.toberkoe.pluto.examples.integration.Person;
import de.toberkoe.pluto.examples.integration.PersonCdiService;
import de.toberkoe.pluto.extensions.integration.persistence.config.discovery.EntityClassProvider;
import de.toberkoe.pluto.extensions.integration.persistence.config.discovery.EntityDiscoveryMode;
import de.toberkoe.pluto.extensions.integration.persistence.config.discovery.Strategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;


@ExtendWith(PersistenceExtension.class)
@EntityDiscoveryMode(value = Strategy.STATIC)
class StaticPersonCdiServiceTest {

    @Inject
    PersonCdiService service;

    @EntityClassProvider
    static List<Class<?>> provideEntityClasses() {
        return List.of(Person.class);
    }

    @EntityClassProvider
    static Set<Class<?>> provideEntityClassesSet() {
        return Set.of(Hobby.class);
    }

    @EntityClassProvider
    static Class<?>[] provideEntityClassesArray() {
        return new Class<?>[] { Job.class };
    }

    @Test
    void testCdiService() {
        Person merged = service.create(new Person("Luke", "Skywalker"));
        assertThat(merged).isNotNull();
        assertThat(merged.getId()).isPositive();
    }

}