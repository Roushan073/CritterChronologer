package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Nationalized
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Pet> pets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
