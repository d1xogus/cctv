package cctv.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

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
    @Column(name = "memberId")
    private Long memberId;

    @Column(name = "passwd")
    private String passwd;

    @Column(name = "name")
    private String name;

    @Column(name = "roll")
    private String roll;

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
}
