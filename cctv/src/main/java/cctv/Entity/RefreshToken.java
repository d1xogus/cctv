package cctv.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refreshToken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memberId", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 512, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken(Long memberId, String token, long expirationMillis) {
        this.memberId = memberId;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plus(Duration.ofMillis(expirationMillis));
    }

}
