package cctv.DTO;

import cctv.Entity.Member;
import cctv.Entity.Role;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UserProfile {
    private String username; // 사용자 이름
    private String provider; // 로그인한 서비스
    private String email; // 사용자의 이메일
    private Role role;

    public void setUserName(String userName) {
        this.username = userName;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoll(Role role) {this.role = role;}

    // DTO 파일을 통하여 Entity를 생성하는 메소드
    public Member toEntity() {
        log.info("Creating Member entity with username: {}, email: {}, provider: {}", this.username, this.email, this.provider);
        return Member.builder()
                .name(this.username)
                .email(this.email)
                .provider(this.provider)
                .role(this.role)
                .build();
    }
}
