package cctv.Controller;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class MemberController {
    @GetMapping("/code/kakao")
    public String getJson(Authentication authentication) {
        log.info("sdfsdf");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return attributes.toString();
    }
}

