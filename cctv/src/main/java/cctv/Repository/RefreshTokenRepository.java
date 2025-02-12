package cctv.Repository;

import cctv.Entity.Member;
import cctv.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long memberId); //  특정 사용자 토큰 찾기
    Optional<RefreshToken> findByToken(String token); //  토큰 값으로 조회
    void deleteByMemberId(Long memberId); //  로그아웃 시 삭제
}
