package de.toberkoe.pluto.extensions.integration.persistence;

import de.toberkoe.pluto.examples.Person;
import de.toberkoe.pluto.examples.PersonRepository;
import de.toberkoe.pluto.extensions.integration.persistence.config.InjectPersistence;
import de.toberkoe.pluto.extensions.integration.persistence.config.log.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;

@ExtendWith(PersistenceExtension.class)
@Log(Log.Level.DEBUG)
class PersonRepositoryIT {

    @InjectPersistence
    private PersonRepository repository;

    @Test
    void testCreatePerson() {
        Person person = new Person("Java", "Duke");
        Person merged = repository.create(person);
        assertThat(merged).isNotNull();
        assertThat(merged.getId()).isPositive();
    }

}
