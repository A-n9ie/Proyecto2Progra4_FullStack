package org.example.backend.presentation.security;

import org.example.backend.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private TokenService tokenService;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Usuario user) {
//        try {
//            var authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            user.getUsuario(), user.getPassword()
//                    )
//            );
//
//            String token = tokenService.generateToken(authentication);
//            return ResponseEntity.ok().body(Map.of("token", token));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Credenciales inválidas"));
//        }
//    }
//}
