package edu.tum.ase.asedelivery.usermngmt.jwt;

import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Autowired
    private KeyStoreManager keyStoreManager;
    public String generateToken(AseUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getRole());
        return createToken(claims, userDetails.getName());
    }
    // Create JWS with both custom and registered claims, signed by
    // a private key.
    private String createToken(Map<String, Object> claims, String subject) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("aseProject")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Expires after 5 hours
                .signWith(keyStoreManager.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
        return jwt;
    }
    // Create a Parser to read info inside a JWT. This parser use the public key
    // to verify the signature of incoming JWT tokens
    private JwtParser loadJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(keyStoreManager.getPublicKey())
                .build();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return loadJwtParser()
                .parseClaimsJws(token)
                .getBody();
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Check if the JWT is signed by us, and is not expired
    public boolean verifyJwtSignature(String token) {
        PublicKey publicKey = keyStoreManager.getPublicKey();
        String[] chunks = token.split("\\.");
        JwtParser parser = loadJwtParser();

        parser.parseClaimsJws(token);

        if (isTokenExpired(token)){
            return false;
        }

        return true;


    }
    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String tmpname = extractUsername(token);

        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return tmpname;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        JwtParser jwtParser = loadJwtParser();
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        Collection<SimpleGrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}