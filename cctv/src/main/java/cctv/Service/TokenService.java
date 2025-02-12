package cctv.Service;

import cctv.Entity.RefreshToken;
import cctv.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(Long memberId, String refreshToken, long expirationMillis) {
        refreshTokenRepository.findByMemberId(memberId)
                .ifPresent(refreshTokenRepository::delete);
        RefreshToken newToken = new RefreshToken(memberId, refreshToken, expirationMillis);
        refreshTokenRepository.save(newToken);

//        String key = "refresh:" + memberId;
//        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS); // 7일 만료
    }

    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
