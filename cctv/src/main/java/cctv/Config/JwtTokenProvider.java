package cctv.Config;

import cctv.Service.OAuth2Service;
import cctv.Service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String SECRET_KEY = "your-very-secure-and-longer-secret-key-your-very-secure-and-longer-secret-key"; // 64바이트 이상
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final String KEY_ROLE = "roles";
    private final TokenService tokenService;

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 1. refresh token 발급
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveRefreshToken(authentication.getName(), refreshToken, accessToken); // redis에 저장
    }

    public String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .subject(authentication.getName())      // 사용자 정보 저장
                .claim(KEY_ROLE, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)  // ✅ 문자열(String) 리스트로 변환
                        .collect(Collectors.toList()))  // ✅ JWT Claims에 List<String>으로 저장    //권한정보저장
                .issuedAt(now)  // 발급 시간
                .expiration(expiredDate)    // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS512)    // 서명 알고리즘
                .compact();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parser()       // 0.12.x 버젼
                    .verifyWith(secretKey)      //SecretKey 객체 사용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().after(new Date());        //만료 시간 체크
        } catch (Exception e) {
            log.error("[validateToken] 유효하지 않은 토큰입니다.");
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);     // JWT에서 Claims 추출
        log.info("[JWT Claims] {}", claims);

        List<String> roles = claims.get("roles", List.class);   // 권한 정보 추출
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        // JWT 안의 정보(Claims)에서 sub와 roles를 attributes 맵에 저장
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", claims.getSubject());
        attributes.put("roles", claims.get("roles"));

        // 2. security의 User 객체 생성
        DefaultOAuth2User principal = new DefaultOAuth2User(authorities, attributes, "sub");
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid Token");
        } catch (SecurityException e) {
            throw new JwtException("Invalid JWT Signature");
        }
    }

}

