package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //where business logic and DTO conversion happens for a given request
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) { //dependency injection (Inversion of Control)
        //PatientService class recieves it's dependency the patientRepo interface via a constructor
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
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));//built in jpa save method

        return PatientMapper.toDTO(newPatient); //returning back to controller
    }
}
