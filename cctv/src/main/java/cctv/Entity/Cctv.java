package cctv.Entity;


import cctv.DTO.CctvDTO;
import cctv.DTO.RoleDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "Cctv")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Cctv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cctvId")
    private Long cctvId;

    @Column(name = "location")
    private String location;

    @Column(name = "cctvDate")
    private String cctvDate;

    @Column(name = "cctvName")
    private String cctvName;

    @Column(name = "webcamId")
    private String webcamId;

    @Column(name = "stream")
    private String stream;

    public static Cctv toEntity(CctvDTO cctvDTO) {
        return Cctv.builder()
                .cctvId(cctvDTO.getCctvId())
                .location(cctvDTO.getLocation())
                .cctvDate(cctvDTO.getCctvDate())
                .cctvName(cctvDTO.getCctvName())
                .webcamId(cctvDTO.getWebcamId())
                .stream(cctvDTO.getStream())
                .build();
    }
}
