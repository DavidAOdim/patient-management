package com.pm.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity //used to persist data in the db
public class Patient {
    //our entity properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull //when we try to persist (save /update) it's going to fail if name does not have a value
    private String name;

    @NotNull
    @Email
    @Column(unique = true) //additional validation to make sure when we save user email it's unique
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth; //saves a timestamp to the db > handled by jpa persistence layer and db

    @NotNull
    private LocalDate registeredDate;

    //add getter and setter for our properties

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }
}
