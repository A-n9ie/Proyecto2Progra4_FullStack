package org.example.backend.presentation.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.example.backend.logic.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.example.backend.presentation.security.JwtConfig;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {
    private final JwtConfig jwtConfig;

    public TokenService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Authentication authentication) {
        var header = new JWSHeader.Builder(jwtConfig.getAlgorithm()).type(JOSEObjectType.JWT).build();
        Instant now = Instant.now();
        var builder = new JWTClaimsSet.Builder().issuer("TotalSoft").issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(1, java.time.temporal.ChronoUnit.HOURS)));
        var scopes = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        builder.claim("scope", scopes);
        var user = (Usuario) authentication.getPrincipal();
        builder.claim("id", user.getId());
        builder.claim("name", user.getUsername());
        var claims = builder.build();

        var key = jwtConfig.getSecretKey();
        var jwt = new SignedJWT(header, claims);
        try {
            var signer = new MACSigner(key);
            jwt.sign(signer);
        } catch (JOSEException e) { throw new RuntimeException("Error generating JWT", e); }
        return jwt.serialize();
    }
}


