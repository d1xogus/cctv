package cctv.DTO;

import cctv.Entity.Role;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private List<Long> cctvId;

    public static RoleDTO toDTO(Role role) {
        return RoleDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .cctvId(role.getCctvId())
                .build();
    }
}