package src.nerius.com.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String deviceAuthKey;

    public JwtService(@Value("${jwt.deviceAuthKey}") String deviceAuthKey) {
        this.deviceAuthKey = deviceAuthKey;
    }

    public String generateToken(@Nullable String authKeyForDevices, String type, String name) {
        Date now = new Date();

        if (type.equals("Device")) {
            if (deviceAuthKey.equals(authKeyForDevices)){
                return "";
            }
        }


        return Jwts.builder()
                .setSubject(name)
                .claim("type", type)
                .setIssuedAt(now)
                .signWith(Keys.hmacShaKeyFor(deviceAuthKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(deviceAuthKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
