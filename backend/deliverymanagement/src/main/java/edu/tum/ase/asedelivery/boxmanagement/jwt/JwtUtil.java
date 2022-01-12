package edu.tum.ase.asedelivery.boxmanagement.jwt;

//import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Autowired



    private PublicKey loadPubKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Read Public Key.
        File filePublicKey = ResourceUtils.getFile("classpath:public.key");
        FileInputStream fis = new FileInputStream(filePublicKey);

        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();
        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        return keyFactory.generatePublic(publicKeySpec);

    }
    // Create a Parser to read info inside a JWT. This parser use the public key
// to verify the signature of incoming JWT tokens
    private JwtParser loadJwtParser() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return Jwts.parserBuilder()
                .setSigningKey(loadPubKey())
                .build();
    }
    public String extractUsername(String token) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return loadJwtParser()
                .parseClaimsJws(token)
                .getBody();
    }
    private boolean isTokenExpired(String token) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return extractExpiration(token).before(new Date());
    }

    // Check if the JWT is signed by us, and is not expired
    public boolean verifyJwtSignature(String token) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        PublicKey publicKey = loadPubKey();

        String[] chunks = token.split("\\.");
        JwtParser parser = loadJwtParser();

        parser.parseClaimsJws(token);
        return !isTokenExpired(token);
    }
}