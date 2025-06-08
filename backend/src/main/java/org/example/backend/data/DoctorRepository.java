package org.example.backend.data;

import org.example.backend.logic.Medico;
import org.example.backend.logic.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Medico, Integer> {
    public Medico save(Medico medico);
    public Medico findByCedula(String cedula);
    public Medico findByUsuario(Usuario usuario);
//    public Iterable<Medico> findMedicoByLugar(String lugar);
    public Medico findById(int id);

    // Buscar médicos por ciudad (búsqueda parcial)
    List<Medico> findByLugarAtencionContainingIgnoreCase(String ciudad);

    // Buscar médicos por especialidad (búsqueda parcial)
    List<Medico> findByEspecialidadContainingIgnoreCase(String especialidad);

    // Buscar médicos por especialidad y ciudad (búsqueda parcial)
    List<Medico> findByEspecialidadContainingIgnoreCaseAndLugarAtencionContainingIgnoreCase(String especialidad, String ciudad);

    //Iterable<Medico> findByNombreOrCedula(String medico);

    List<Medico> findAll();

    List<Medico> findByAprobadoFalse();

    //Iterable<Medico> findByAprobado(boolean aprovado);

}
