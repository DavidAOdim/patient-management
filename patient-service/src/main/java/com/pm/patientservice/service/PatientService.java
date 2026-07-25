package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service //where business logic and DTO conversion happens for a given request
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) { //dependency injection (Inversion of Control)
        //PatientService class receives it's dependency the patientRepo interface via a constructor
        //instead of instantiating the patient repo itself by using the new key word (makes code more modular and easier to test)
        this.patientRepository = patientRepository;
    }

    //getting the patients. service returns DTO
    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        //iterates over each item in the patient list

        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    //recieves DTO from postmapping from our controller
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email: " +
                    patientRequestDTO.getEmail() + " already exists");
        } //if email alr exists can't create a new patient
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));//built in jpa save method

        return PatientMapper.toDTO(newPatient); //returning back to controller
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email: " +
                    patientRequestDTO.getEmail() + " already exists");
        } //checks whether there is a patient with same email but with diff id and if so throws exception

        //update Patient Object
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient); //jpa will handle the update for us
        //and assign the updated record to updatedPatient
        return PatientMapper.toDTO(updatedPatient);

    }
}
