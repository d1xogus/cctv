package cctv.Handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        log.error("🔥 인증 실패: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형태의 응답 메시지 반환
        response.getWriter().write("{ \"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\" }");
    }
}
