package de.toberkoe.pluto.extensions.integration.persistence.manager;

import de.toberkoe.pluto.extensions.integration.persistence.PersistenceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;

@ExtendWith(PersistenceExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerCreationTest {

    @Inject
    private EntityManager manager;

    @Inject
    private EntityService service;

    @Test
    void testMerge() {
        assertThat(manager).isSameAs(service.manager);
        Hobby hobby = new Hobby("Programming");
        hobby = manager.merge(hobby);

        Person person = service.mergePerson(new Person("Person", hobby));

        assertThat(hobby.getId()).isPositive();
        assertThat(person.getId()).isPositive();
    }

    @Test
        //FIXME make this a scenario test
    void isDatabaseClean() {
        int size = manager.createQuery("SELECT h FROM Hobby h")
                .getResultList().size();
        assertThat(size).isZero();
    }
}
