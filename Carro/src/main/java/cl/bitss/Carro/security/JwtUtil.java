package cl.bitss.Carro.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class JwtUtil {
    private static final String SECRET = System.getenv().getOrDefault("BITSS_JWT_SECRET", "BITSS-Cambiar-Esta-Clave-Secreta-Para-Produccion-2026");
    private static final long EXPIRATION_SECONDS = 3600;

    public static String generate(String subject) {
        long expiration = Instant.now().getEpochSecond() + EXPIRATION_SECONDS;
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":\"" + subject.replace("\"", "") + "\",\"exp\":" + expiration + "}");
        String content = header + "." + payload;
        return content + "." + sign(content);
    }

    public static boolean valid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) return false;
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"exp\":(\\d+)").matcher(payload);
            return matcher.find() && Long.parseLong(matcher.group(1)) > Instant.now().getEpochSecond();
        } catch (Exception e) { return false; }
    }

    private static String encode(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }

    private static String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
