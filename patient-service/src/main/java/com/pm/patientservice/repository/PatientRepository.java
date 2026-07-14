package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository //tells spring that this interface is a jpa repository
public interface PatientRepository extends JpaRepository<Patient, UUID> { //pass in entity we want it to control and it's primary id type
    //with JpaRepo we get access to CRUD functionality


}
