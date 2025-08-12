package br.dev.viniciusleonel.backend_challenge.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtGenerator {

    private static final String SECRET = "your-secret-key"; // Substitua por uma chave segura
//    private static final long EXPIRATION_TIME = 86400000; // 1 dia em milissegundos

    public static String generateJwtToken(String name, String role, String seed) {
        return JWT.create()
                .withClaim("Name", name)
                .withClaim("Role", role)
                .withClaim("Seed", seed)
                //                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }
}
