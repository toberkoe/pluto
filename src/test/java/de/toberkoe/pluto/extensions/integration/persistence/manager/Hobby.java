package de.toberkoe.pluto.extensions.integration.persistence.manager;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Hobby {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    public Hobby() {
    }

    public Hobby(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
