package gameflix_auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long lifetimeSeconds;

    public JwtService(
            JwtEncoder encoder,
            @Value("${app.jwt.lifetime-seconds}")
            long lifetimeSeconds) {

        if (lifetimeSeconds <= 0) {
            throw new IllegalArgumentException(
                "JWT lifetime must be positive"
            );
        }

        this.encoder = encoder;
        this.lifetimeSeconds = lifetimeSeconds;
    }

    public TokenResponse issue(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("gameflix-auth")
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(lifetimeSeconds))
            .build();

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        String token = encoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new TokenResponse(
            token,
            "Bearer",
            lifetimeSeconds
        );
    }
}

