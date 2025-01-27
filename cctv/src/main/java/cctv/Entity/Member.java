package cctv.Entity;

import cctv.DTO.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Optional;

@Table(name = "member")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberid")
    private Long memberId;

    @Column(name = "passwd")
    private String passwd;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "rollid")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column
    private String provider;

    public Member updateUser(String name, String email, String provider) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        return this;
    }

    public Member toEntity(MemberDTO memberDTO){
        return Member.builder()
                .memberId(getMemberId())
                .name(getName())
                .passwd(getPasswd())
                .email(getEmail())
                .role(getRole())
                .provider(getProvider())
                .build();
    }

    public Member update(MemberDTO memberDTO) {
        this.name = Optional.ofNullable(memberDTO.getName()).orElse(this.name);
        this.email = Optional.ofNullable(memberDTO.getEmail()).orElse(this.email);
        this.role = Optional.ofNullable(memberDTO.getRole()).orElse(this.role);
        this.provider = Optional.ofNullable(memberDTO.getProvider()).orElse(this.provider);
        this.phone = Optional.ofNullable(memberDTO.getPhone()).orElse(this.phone);
        return this;
    }

}
