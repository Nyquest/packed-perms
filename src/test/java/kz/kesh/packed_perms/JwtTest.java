package kz.kesh.packed_perms;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtTest {

    private static String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";

    /**
     * Creation of the JWT
     */
    @Test
    public void createJwt1() {
        String jwt = createJWT("111", "center", "surname", 300_000, "AAB");
        System.out.println(jwt);
        assertEquals(jwt.split("\\.").length, 3);
    }

    @Test
    public void createJwt2() {
        List<Integer> list = Arrays.asList(2, 4, 7, 8, 9, 10, 12, 14, 15, 1000, 2005, 10002, 10007);
        String pack = PackedPerms.pack(list);
        System.out.println(pack);
    }

    /**
     * Extraction from the JWT of the field with the name 'permissions'
     */
    @Test
    public void decodeJwt1() {
        String packedPermission = "AAB";
        String jwt = createJWT("111", "center", "surname", 300_000, packedPermission);
        Claims claims = decodeJwt(jwt);
        assertEquals(claims.get("permissions"), packedPermission);
    }

    private String createJWT(String id, String issuer, String subject, long ttlMillis, String packedPermissions) {
        // See https://developer.okta.com/blog/2018/10/31/jwts-with-java
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        builder.claim("permissions", packedPermissions);

        // if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    private Claims decodeJwt(String jwt) {
        // See https://developer.okta.com/blog/2018/10/31/jwts-with-java
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

}
