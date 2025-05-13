package cctv.DTO;

import cctv.Entity.Cctv;
import lombok.*;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CctvDTO {
    private String location;
    private String cctvDate;
    private String cctvName;
    private String webcamId;
    private String stream;
    private String ip;
    private String id;
    private String passwd;

    public static CctvDTO toDTO(Cctv cctv) {
        return CctvDTO.builder()
                .location(cctv.getLocation())
                .cctvDate(cctv.getCctvDate())
                .cctvName(cctv.getCctvName())
                .webcamId(cctv.getWebcamId())
                .stream(cctv.getStream())
                .ip(cctv.getIp())
                .id(cctv.getId())
                .passwd(cctv.getPasswd())
                .build();
    }
}