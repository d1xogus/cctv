package cctv.DTO;

import cctv.Entity.Member;
import cctv.Entity.Role;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long memberId;
    private String name;
    private Long roleId;
    private String email;
    private String phone;
    private String provider;

    public static MemberDTO toDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .roleId(member.getRole().getRoleId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .provider(member.getProvider())
                .build();
    }
}
