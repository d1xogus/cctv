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
        log.info("[JwtAuthenticationFilter] 요청 URI: {}", request.getRequestURI());
        String token = jwtTokenProvider.getTokenFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("token 인증성공");
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[SecurityContext] 인증된 사용자: {}", authentication.getName());
            log.info("[SecurityContext] 권한 정보: {}", authentication.getAuthorities());
        }
        filterChain.doFilter(request, response);
    }
}
