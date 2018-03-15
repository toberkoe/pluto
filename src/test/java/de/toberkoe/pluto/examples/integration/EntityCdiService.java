package de.toberkoe.pluto.examples.integration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityCdiService<E> {

    @Inject
    private EntityManager manager;

    private boolean initialized;

    @PostConstruct
    private void postConstruct() {
        initialized = true;
    }

    public E create(E person) {
        return manager.merge(person);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
