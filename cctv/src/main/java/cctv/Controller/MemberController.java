package cctv.Controller;



import cctv.DTO.MemberDTO;
import cctv.Entity.Member;
import cctv.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/code/kakao")
    public String getJson(Authentication authentication) {
        log.info("sdfsdf");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return attributes.toString();
    }

    @GetMapping("/{memberId}")
    public Member member(@PathVariable Long memberId){
        return memberService.get(memberId);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<Member> update(@PathVariable Long memberId, @RequestBody MemberDTO memberDTO){
        Member updated = memberService.update(memberId, memberDTO);
        return (updated != null) ?
                ResponseEntity.status(200).body(updated) :
                ResponseEntity.status(400).build();
    }
}

