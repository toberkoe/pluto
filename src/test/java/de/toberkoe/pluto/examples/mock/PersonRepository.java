package de.toberkoe.pluto.examples.mock;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersonRepository {

    @PersistenceContext(unitName = "pluto")
    private EntityManager manager;

    public Person create(Person person) {
        return manager.merge(person);
    }
}
