package gameflix_auth;

import java.time.LocalDateTime;

public record UserProfile(
    Integer id,
    String username,
    String email,
    LocalDateTime createdAt
) {

    public static UserProfile from(User user) {
        return new UserProfile(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
