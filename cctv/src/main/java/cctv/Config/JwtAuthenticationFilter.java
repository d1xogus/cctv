package cctv.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("[JwtAuthenticationFilter] 요청 URI: {}", request.getRequestURI());
        if (requestURI.startsWith("/login") || requestURI.startsWith("/oauth2")) {
            log.info("🔹 [JwtAuthenticationFilter] /login 및 /oauth2 요청은 JWT 검증 없이 처리됨.");
            filterChain.doFilter(request, response);
            return;
        }
        String token = jwtTokenProvider.getTokenFromRequest(request);

        if (token != null && jwtTokenProvider.validateAccessToken(token)) {
            log.info("token 인증성공");
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[SecurityContext] 인증된 사용자: {}", authentication.getName());
            log.info("[SecurityContext] 권한 정보: {}", authentication.getAuthorities());
        } else {
            log.info("JWT 인증 실패 또는 Access Token이 아님");
        }
        filterChain.doFilter(request, response);
    }
}
