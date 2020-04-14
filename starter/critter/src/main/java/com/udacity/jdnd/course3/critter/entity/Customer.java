package com.udacity.jdnd.course3.critter.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Entity
@DiscriminatorValue("customer")
public class Customer extends User {

    @Column
    @Pattern(regexp = "[0-9-+]+")
    private String phoneNumber;

    @Column(length = 500)
    private String notes;

}
