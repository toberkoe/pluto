package de.toberkoe.pluto.extensions.integration.persistence;

import de.toberkoe.pluto.examples.integration.Person;
import de.toberkoe.pluto.examples.integration.PersonLocalBean;
import de.toberkoe.pluto.extensions.integration.persistence.config.InjectPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;

@ExtendWith(PersistenceExtension.class)
class PersonLocalBeanIT {

    @InjectPersistence
    private PersonLocalBean repository;

    @Test
    void testCreatePerson() {
        Person merged = repository.create(new Person("Luke", "Skywalker"));
        assertThat(merged).isNotNull();
        assertThat(merged.getId()).isPositive();
    }
}
