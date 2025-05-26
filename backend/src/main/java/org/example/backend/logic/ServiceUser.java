package org.example.backend.logic;

import org.example.backend.data.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
        return usuarioRepository.findByUsuario(username);
    }

    public void addUser(Usuario user) {
        if (getUser(user.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username Already Exist");
        }
       /* BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setClave(passwordEncoder.encode(user.getClave()));*/
        usuarioRepository.save(user);
    }

    public String getUserAuthenticated (){
       // return SecurityContextHolder.getContext().getAuthentication().getName();
        return "user";
    }
}
