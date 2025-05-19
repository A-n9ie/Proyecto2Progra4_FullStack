package org.example.backend.data;

import org.example.backend.logic.Medico;
import org.example.backend.logic.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Medico, Integer> {
    public Medico findByCedula(String cedula);
    public Medico findByUsuario(Usuario usuario);
//    public Iterable<Medico> findMedicoByLugar(String lugar);
public Medico findById(int id);

    // Buscar médicos por ciudad (búsqueda parcial)
    Iterable<Medico> findByLugarAtencionContainingIgnoreCase(String ciudad);

    // Buscar médicos por especialidad (búsqueda parcial)
    Iterable<Medico> findByEspecialidadContainingIgnoreCase(String especialidad);

    // Buscar médicos por especialidad y ciudad (búsqueda parcial)
    Iterable<Medico> findByEspecialidadContainingIgnoreCaseAndLugarAtencionContainingIgnoreCase(String especialidad, String ciudad);

    //Iterable<Medico> findByNombreOrCedula(String medico);

    List<Medico> findAll();

    //Iterable<Medico> findByAprobado(boolean aprovado);

}
