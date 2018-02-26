package de.toberkoe.pluto.examples.integration;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PersonLocalBean {

    @PersistenceContext
    private EntityManager manager;

    public Person create(Person person) {
        return manager.merge(person);
    }
}
