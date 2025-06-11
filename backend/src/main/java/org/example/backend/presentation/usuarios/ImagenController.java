package org.example.backend.presentation.usuarios;

import org.example.backend.data.DoctorRepository;
import org.example.backend.data.PatientRepository;
import org.example.backend.logic.Medico;
import org.example.backend.logic.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    @Autowired
    private PatientRepository pacienteRepository;

    @Autowired
    private DoctorRepository medicoRepository;

    @PostMapping("/subir")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo,
                                              @RequestParam("cedula") String cedula,
                                              @RequestParam("tipo") String tipo) {
        try {
            String nombreArchivo = archivo.getOriginalFilename();

            // Ruta del directorio donde se guardarán las imágenes
            String directorioBase = System.getProperty("user.dir") + "/fotosPerfil";
            File carpeta = new File(directorioBase);
            if (!carpeta.exists()) {
                carpeta.mkdirs(); // Crear la carpeta si no existe
            }

            // Guardar la imagen en la carpeta
            String rutaDestino = directorioBase + "/" + nombreArchivo;
            archivo.transferTo(new File(rutaDestino));

            // Buscar usuario por cédula y actualizar foto
            if (tipo.equalsIgnoreCase("paciente")) {
                Paciente paciente = pacienteRepository.findByCedula(cedula);
                if (paciente != null) {
                    paciente.setFotoUrl(nombreArchivo);
                    pacienteRepository.save(paciente);
                    return ResponseEntity.ok("Imagen del paciente subida correctamente.");
                }
            } else if (tipo.equalsIgnoreCase("medico")) {
                Medico medico = medicoRepository.findByCedula(cedula);
                if (medico != null) {
                    medico.setFotoUrl(nombreArchivo);
                    medicoRepository.save(medico);
                    return ResponseEntity.ok("Imagen del médico subida correctamente.");
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cédula no encontrada.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen.");
        }
    }

    @GetMapping("/ver/{nombreArchivo}")
    public ResponseEntity<org.springframework.core.io.Resource> verImagen(@PathVariable String nombreArchivo) throws IOException {
        String ruta = System.getProperty("user.dir") + "/fotosPerfil/" + nombreArchivo;
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        org.springframework.core.io.Resource recurso = new UrlResource(archivo.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(recurso);
    }
}

