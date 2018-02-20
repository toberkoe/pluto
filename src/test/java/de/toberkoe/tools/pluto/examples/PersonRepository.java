package de.toberkoe.tools.pluto.examples;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PersonRepository {

    @PersistenceContext(unitName = "pluto")
    private EntityManager manager;

    public Person create(Person person) {
        return manager.merge(person);
    }
}
