package de.toberkoe.pluto.extensions.integration.persistence;

import de.toberkoe.pluto.examples.integration.Person;
import de.toberkoe.pluto.examples.integration.PersonCdiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;

@ExtendWith(PersistenceExtension.class)
class DynamicPersonCdiServiceTest {

    @Inject
    PersonCdiService service;

    @Test
    void testCdiService() {
        Person merged = service.create(new Person("Luke", "Skywalker"));
        assertThat(merged).isNotNull();
        assertThat(merged.getId()).isPositive();
    }

}