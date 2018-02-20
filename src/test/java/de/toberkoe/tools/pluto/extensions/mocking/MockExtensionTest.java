package de.toberkoe.tools.pluto.extensions.mocking;

import de.toberkoe.tools.pluto.examples.Person;
import de.toberkoe.tools.pluto.examples.PersonRepository;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.fest.assertions.api.Assertions.assertThat;
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