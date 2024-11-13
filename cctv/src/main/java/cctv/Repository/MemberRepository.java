package cctv.Repository;

import cctv.Entity.Member;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findUserByEmailAndProvider(String email, String provider);
}
