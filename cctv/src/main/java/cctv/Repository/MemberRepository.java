package cctv.Repository;

import cctv.Entity.Member;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findBySub(Long sub);
    Optional<Member> findByMemberId(Long memberId);
    Optional<Member> findUserByEmail(String email);
}
