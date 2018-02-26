package de.toberkoe.pluto.examples.integration;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityCdiService<E> {

    @Inject
    private EntityManager manager;

    public E create(E person) {
        return manager.merge(person);
    }
}
