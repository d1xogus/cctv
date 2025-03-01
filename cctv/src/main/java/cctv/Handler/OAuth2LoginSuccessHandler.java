package cctv.Handler;

import cctv.Config.JwtTokenProvider;
import cctv.Entity.Member;
import cctv.Entity.RefreshToken;
import cctv.Repository.MemberRepository;
import cctv.Repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.net.URI;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("1번");
        Member member = getAuthenticatedMember(authentication);
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        redirectToken(request, response, member);
        log.info("2번");
    }

    private Member getAuthenticatedMember(Authentication authentication) {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Long kakaoId = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");

        if (kakaoId == null || email == null) {
            throw new RuntimeException("OAuth2User에서 id 또는 email을 가져오지 못함.");
        }
        log.info("🔹 [OAuth2LoginSuccessHandler] 인증된 사용자 - Kakao ID: {}, Email: {}", kakaoId, email);


        //  DB에서 기존 회원 조회
        Optional<Member> existingMember = memberRepository.findBySub(kakaoId);

        if (existingMember.isPresent()) {
            //  기존 회원이 있으면 DB의 memberId 사용
            log.info(" [기존 회원] DB에서 조회된 회원 ID: {}", existingMember.get().getMemberId());
            return existingMember.get();
        } else {
            //  신규 회원이면 DB에 저장 후 반환
            Member newMember = new Member();
            newMember.setSub(kakaoId);
            newMember.setEmail(email);
            newMember = memberRepository.save(newMember); // DB 저장 후 memberId 자동 생성

            return newMember;
        }
    }

    private void redirectToken(HttpServletRequest request, HttpServletResponse response, Member member) throws IOException {
        String accessToken = jwtTokenProvider.generateAccessToken(member);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member); //  Refresh Token은 Redis에 저장됨

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(member.getMemberId());

        if (existingToken.isPresent()) {
            RefreshToken refreshTokenEntity = existingToken.get();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenRepository.save(refreshTokenEntity); // 기존 토큰 업데이트
        } else {
            refreshTokenRepository.save(new RefreshToken(member.getMemberId(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME)); //  새 토큰 저장
        }

        String uri = createURI(accessToken, refreshToken, member.getMemberId(), request).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    //  프론트엔드 리다이렉트 URL 생성 (Refresh Token 제거)
    private URI createURI(String accessToken, String refreshToken, Long memberId, HttpServletRequest request) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("member_id", String.valueOf(memberId));
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);


        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri == null || redirectUri.isBlank()) {
            redirectUri = "http://localhost:3000/main"; // 기본값 설정
        }
//        String redirectUri = "http://3.36.174.53:8080/logininfo";
        log.info("url생성");
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

}
