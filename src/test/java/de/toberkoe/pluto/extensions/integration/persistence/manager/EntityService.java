package de.toberkoe.pluto.extensions.integration.persistence.manager;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityService {

    @Inject
    EntityManager manager;

    public Person mergePerson(Person person) {
        return manager.merge(person);
    }
}
