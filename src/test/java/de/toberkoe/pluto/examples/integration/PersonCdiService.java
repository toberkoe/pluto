package de.toberkoe.pluto.examples.integration;

import javax.annotation.PostConstruct;

public class PersonCdiService extends EntityCdiService<Person> {

    private boolean fullyInitialized;

    @PostConstruct
    public void init() {
        fullyInitialized = true;
    }

    public boolean isFullyInitialized() {
        return fullyInitialized;
    }
}
