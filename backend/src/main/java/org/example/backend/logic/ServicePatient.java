package org.example.backend.logic;

import org.example.backend.data.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service("servicePatient")
public class ServicePatient {
    @Autowired
    private PatientRepository patientRepository;

    public Iterable<Paciente> pacientesFindAll() {
        return patientRepository.findAll();
    }

    public void addPatient(Paciente paciente) {
        patientRepository.save(paciente);
    }

    public Paciente findPatient(String cedula) {return patientRepository.findByCedula(cedula);}

    public Paciente getPatientByUser(Usuario user){return patientRepository.findByUsuario(user);}

    public void editPatient(Usuario user, Paciente patient){
        Paciente u = getPatientByUser(user);
        if (u != null){
            u.setNombre(patient.getNombre());
            u.setDireccion(patient.getDireccion());
            u.setTelefono(patient.getTelefono());

            patientRepository.save(u);
        }
    }
}