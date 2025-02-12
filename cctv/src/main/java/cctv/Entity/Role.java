package cctv.Entity;

import cctv.DTO.LogDTO;
import cctv.DTO.RoleDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;


@Table(name = "role")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private Long roleId;

    @Column(name = "roleName")
    private String roleName;

    @ElementCollection
    @CollectionTable(name = "role_cctvids", joinColumns = @JoinColumn(name = "roleId"))
    @Column(name = "cctvId")
    private List<Long> cctvId;

    public static Role toEntity(RoleDTO roleDTO) {
        return Role.builder()
                .roleId(roleDTO.getRoleId())
                .roleName(roleDTO.getRoleName())
                .cctvId(roleDTO.getCctvId())
                .build();
    }

    public String getName() {
        return this.roleName;
    }
}
