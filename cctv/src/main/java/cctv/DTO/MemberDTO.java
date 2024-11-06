package cctv.DTO;

import cctv.Entity.Member;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long memberId;
    private String passwd;
    private String name;
    private String roll;
    private String email;
    private String phone;
    private String provider;

    public static MemberDTO toDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .roll(member.getRoll())
                .email(member.getEmail())
                .phone(member.getPhone())
                .provider(member.getProvider())
                .build();
    }
}
