package br.dev.viniciusleonel.backend_challenge.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtDecoder {

    private static final Logger log = LoggerFactory.getLogger(JwtDecoder.class);

    public static DecodedJWT decode(String token) {
        log.info("Iniciando decodificacao do JWT");

        try {
            DecodedJWT decodedJwt = JWT.decode(token);
            log.debug("JWT decodificado com sucesso");
            return decodedJwt;
        } catch (JWTDecodeException e) {
            log.error("Falha ao decodificar JWT: {}", e.getMessage());
            throw new JWTDecodeException("Falha ao decodificar, JWT é nulo ou vazio");
        }
    }
}
