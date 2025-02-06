package cctv.Controller;



import cctv.DTO.MemberDTO;
import cctv.Entity.Member;
import cctv.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @GetMapping("/main")
    public String getJson(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "사용자가 인증되지 않았습니다.";
        }
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return attributes.toString();
    }

    @GetMapping("/cleanguard/logout")
    public String logoutKakao() {
        String logoutRedirectUri = "/";

        return "redirect:https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri;
    }

    @GetMapping("/cleanguard")
    public Member get(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        return memberService.get(email, provider);
    }

    @PatchMapping("/cleanguard")
    public ResponseEntity<Member> update(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestBody MemberDTO memberDTO){
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        Member updated = memberService.update(email, provider, memberDTO);
        return (updated != null) ?
                ResponseEntity.status(200).body(updated) :
                ResponseEntity.status(400).build();
    }
}

