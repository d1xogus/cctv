package cctv.Service;

import cctv.Config.OAuthAttributes;
import cctv.DTO.UserProfile;
import cctv.Entity.Member;
import cctv.Entity.Role;
import cctv.Repository.MemberRepository;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2UserService 실행됨 - 요청 클라이언트: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("OAuth2 로그인 시도 - 사용자 정보: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인을 수행한 서비스의 이름

        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // PK가 되는 정보

        Map<String, Object> attributes = oAuth2User.getAttributes(); // 사용자가 가지고 있는 정보

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        userProfile.setProvider(registrationId);

        updateOrSaveUser(userProfile);

        Map<String, Object> customAttribute =
                getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                customAttribute,
                userNameAttributeName);
    }

    public Map getCustomAttribute(String registrationId,
                                  String userNameAttributeName,
                                  Map<String, Object> attributes,
                                  UserProfile userProfile) {
        Map<String, Object> customAttribute = new ConcurrentHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userProfile.getUsername());
        customAttribute.put("email", userProfile.getEmail());

        return customAttribute;
    }

    public Member updateOrSaveUser(UserProfile userProfile) {
        log.info("Attempting to find user by email: {} and provider: {}", userProfile.getEmail(), userProfile.getProvider());
        Role defaultRole = roleRepository.findById(1L)  //ID가 1인 Role 엔티티를 데이터베이스에서 조회
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Member member = memberRepository
                .findUserByEmailAndProvider(userProfile.getEmail(), userProfile.getProvider())
                .map(value -> value.updateUser(userProfile.getUsername(), userProfile.getSub(), userProfile.getEmail(), userProfile.getProvider()))
                .orElseGet(() -> {
                    Member newMember = userProfile.toEntity();
                    newMember.setRole(defaultRole); // ✅ 기본 Role 설정
                    return newMember;
                });

        return memberRepository.save(member);

    }
}