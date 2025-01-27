package cctv.Repository;

import cctv.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Long> findByRoleName(String roleName);
}