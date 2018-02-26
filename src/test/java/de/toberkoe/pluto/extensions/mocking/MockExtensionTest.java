package de.toberkoe.pluto.extensions.mocking;

import de.toberkoe.pluto.examples.mock.Person;
import de.toberkoe.pluto.examples.mock.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockExtension.class)
class MockExtensionTest {

    @Mock
    private EntityManager manager;

    @InjectMocks
    private PersonRepository repository;

    @Test
    void testCreatePerson() {
        doReturn(new Person()).when(manager).merge(any(Person.class));
        assertThat(repository.create(new Person())).isNotNull();
    }
}