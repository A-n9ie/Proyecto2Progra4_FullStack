package org.example.backend.logic;

import org.example.backend.data.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
/*import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;*/

@org.springframework.stereotype.Service("serviceUser")
public class ServiceUser {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Iterable<Usuario> usuariosFindAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getLastUser() {
        return usuarioRepository.findTopByOrderByIdDesc();
    }

    public Usuario getUser(String username) {
        return usuarioRepository.findByUsuario(username).orElse(null);
    }

    public void addUser(Usuario user) {
        if (getUser(user.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username Already Exist");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setClave(passwordEncoder.encode(user.getClave()));
        usuarioRepository.save(user);
    }

    public String getUserAuthenticated (){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void guardarFoto(MultipartFile archivo, String nombreArchivo) throws IOException {
        Path ruta = Paths.get("fotosPerfil");
        if (!Files.exists(ruta)) {
            Files.createDirectories(ruta);
        }
        Path rutaCompleta = ruta.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
    }

    public String cargarFoto(String fotoUrl) {
        return fotoUrl != null
                ? "http://localhost:8080/imagenes/ver/" + fotoUrl
                : null;
    }


    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsuario(username).orElse(null);
    }

}
