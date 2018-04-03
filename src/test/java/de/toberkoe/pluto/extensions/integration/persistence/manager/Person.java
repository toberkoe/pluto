package de.toberkoe.pluto.extensions.integration.persistence.manager;

import javax.persistence.*;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "fk_hobby")
    private Hobby hobby;

    public Person() {
    }

    public Person(String name, Hobby hobby) {
        this.name = name;
        this.hobby = hobby;
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

    public Hobby getHobby() {
        return hobby;
    }

    public void setHobby(Hobby hobby) {
        this.hobby = hobby;
    }
}
