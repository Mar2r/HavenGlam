package org.esfe.HavenGlam.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Genera y valida tokens de recuperación de contraseña SIN guardar nada en la base de datos.
 *
 * El token contiene: idUsuario + fecha de expiración, firmados con HMAC-SHA256.
 * Si alguien altera el contenido, la firma no coincide y el token se rechaza.
 * Si pasó el tiempo de validez, también se rechaza.
 *
 * Trade-off de esta solución (por no usar tabla en BD):
 * el token no se puede "invalidar manualmente" una vez usado — sigue siendo
 * válido hasta que expira (30 min). Es una limitación aceptable para un
 * flujo de recuperación de contraseña, pero si más adelante quieren poder
 * invalidarlo tras el primer uso, ahí sí se necesitaría guardar un registro
 * (la tabla PasswordResetToken que no se agregó).
 */
@Component
public class PasswordResetTokenUtil {

    private static final String ALGORITMO = "HmacSHA256";
    private static final long VALIDEZ_MINUTOS = 30;

    // IMPORTANTE: en application.properties definan su propio valor:
    // app.reset-token.secret=una-clave-larga-y-secreta-solo-suya
    // Si no la definen, se usa este valor por defecto (NO usar así en producción).
    @Value("${app.reset-token.secret:havenglam-dev-secret-cambiame}")
    private String secreto;

    public String generarToken(Integer idUsuario) {
        long expira = Instant.now().plusSeconds(VALIDEZ_MINUTOS * 60).getEpochSecond();
        String payload = idUsuario + ":" + expira;
        String firma = firmar(payload);
        String payloadCodificado = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return payloadCodificado + "." + firma;
    }

    public Optional<Integer> validarToken(String token) {
        try {
            String[] partes = token.split("\\.", 2);
            if (partes.length != 2) {
                return Optional.empty();
            }

            String payload = new String(
                    Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8
            );
            String firmaRecibida = partes[1];

            if (!firmar(payload).equals(firmaRecibida)) {
                return Optional.empty(); // el token fue alterado
            }

            String[] datos = payload.split(":");
            Integer idUsuario = Integer.valueOf(datos[0]);
            long expira = Long.parseLong(datos[1]);

            if (Instant.now().getEpochSecond() > expira) {
                return Optional.empty(); // el token expiró
            }

            return Optional.of(idUsuario);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String firmar(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), ALGORITMO));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Error generando la firma del token", e);
        }
    }
}