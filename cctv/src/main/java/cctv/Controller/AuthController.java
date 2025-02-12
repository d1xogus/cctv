package cctv.Controller;

import cctv.Config.JwtTokenProvider;
import cctv.Entity.Member;
import cctv.Entity.RefreshToken;
import cctv.Repository.MemberRepository;
import cctv.Service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");

        // Refresh Token 검증
        RefreshToken storedToken = tokenService.getRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        // memberId 가져오기
        Long memberId = storedToken.getMemberId();

        // 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(member);

        return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    }
}
