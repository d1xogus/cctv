package cctv.Repository;

import cctv.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);

    Role findByRoleId(Long roleId);
}