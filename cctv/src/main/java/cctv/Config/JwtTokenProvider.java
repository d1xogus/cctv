package cctv.Config;

import cctv.Entity.Member;
import cctv.Entity.Role;
import cctv.Handler.JwtAuthenticationException;
import cctv.Repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 1L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final String KEY_ROLE = "roles";
    private final TokenService tokenService;

    public String generateAccessToken(Member member) {
        return generateAccess(member.getMemberId(), member.getEmail(), ACCESS_TOKEN_EXPIRE_TIME);
    }
    // 1. refresh token 발급
    public String generateRefreshToken(Member member) {
        String refreshToken = generateRefresh(member.getMemberId(), member.getEmail(), REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME); // Redis에 저장
        return refreshToken;
    }

    private String generateRefresh(Long memberId, String email, long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // role이 null일 가능성이 있으므로 예외 처리 추가
        List<String> roleName = Optional.ofNullable(member.getRole())
                .map(role -> List.of(role.getName()))
                .orElse(List.of("ROLE_USER")); // 기본값 설정

        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) //  sub에 memberId 사용
                .claim("email", email)
                .claim("roles", roleName)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    private String generateAccess(Long memberId, String email, long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // role이 null일 가능성이 있으므로 예외 처리 추가
        List<String> roleName = Optional.ofNullable(member.getRole())
                .map(role -> List.of(role.getName()))
                .orElse(List.of("ROLE_USER")); // 기본값 설정

        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) //  sub에 memberId 사용
                .claim("email", email)
                .claim("roles", roleName)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateAccessToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtAuthenticationException("Access Token이 없음.");
        }

        try {
            Claims claims = Jwts.parser()       // 0.12.x 버젼
                    .verifyWith(secretKey)      //SecretKey 객체 사용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                log.warn("Access Token이 아닌 토큰으로 인증을 시도함.");
                throw new JwtAuthenticationException("Access Token이 아님");
            }

            if (claims.getExpiration().before(new Date())) {
                log.warn("Access Token이 만료됨.");
                throw new JwtAuthenticationException("Access Token이 만료되었습니다.");
            }
            return true;
            //만료 시간 체크
        } catch (Exception e) {
            log.error("[validateToken] 유효하지 않은 Access 토큰.");
            throw new JwtAuthenticationException("유효하지 않은 Access Token.");
        }
    }

    public boolean validateRefreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parser()       // 0.12.x 버젼
                    .verifyWith(secretKey)      //SecretKey 객체 사용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                log.warn("Refresh Token이 아닌 토큰으로 Access Token을 재발급하려 함.");
                throw new JwtAuthenticationException("Refresh Token이 아닌 토큰으로 Access Token");
            }

            return claims.getExpiration().after(new Date());        //만료 시간 체크
        } catch (Exception e) {
            log.error("[validateToken] 유효하지 않은 Refresh 토큰입니다.");
            throw new JwtAuthenticationException("유효하지 않은 Refresh Token.");
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
        attributes.put("email", claims.get("email"));

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

