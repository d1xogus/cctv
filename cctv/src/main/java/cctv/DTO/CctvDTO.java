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
    private Long cctvId;
    private String location;
    private String cctvDate;
    private String cctvName;
    private String webcamId;

    public static CctvDTO toDTO(Cctv cctv) {
        return CctvDTO.builder()
                .cctvId(cctv.getCctvId())
                .location(cctv.getLocation())
                .cctvDate(cctv.getCctvDate())
                .cctvName(cctv.getCctvName())
                .webcamId(cctv.getWebcamId())
                .build();
    }
}