package de.toberkoe.pluto.examples.integration;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PERSON")
public class Person {

    @Id
    @GeneratedValue
    private long id;

    private String firstName;

    private String lastName;

    @ManyToOne
    @JoinColumn(name = "fk_job")
    private Job job;

    @OneToMany
    private List<Hobby> hobbies;

    public Person() {}

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
