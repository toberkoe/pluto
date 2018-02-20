package de.toberkoe.tools.pluto.examples;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersonRepository {

    @PersistenceContext(unitName = "pluto")
    private EntityManager manager;

    public Person create(Person person) {
        return manager.merge(person);
    }
}
