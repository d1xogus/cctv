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
    @Column(name = "stream", nullable = false, unique = true)
    private String stream;

    @Column(name = "location")
    private String location;

    @Column(name = "cctvDate")
    private String cctvDate;

    @Column(name = "cctvName")
    private String cctvName;

    @Column(name = "webcamId")
    private String webcamId;

    @Column(name ="ip")
    private String ip;

    @Column(name = "id")
    private String id;

    @Column(name = "passwd")
    private String passwd;


    public static Cctv toEntity(CctvDTO cctvDTO) {
        return Cctv.builder()
                .location(cctvDTO.getLocation())
                .cctvDate(cctvDTO.getCctvDate())
                .cctvName(cctvDTO.getCctvName())
                .webcamId(cctvDTO.getWebcamId())
                .stream(cctvDTO.getStream())
                .ip(cctvDTO.getIp())
                .id(cctvDTO.getId())
                .passwd(cctvDTO.getPasswd())
                .build();
    }
}
