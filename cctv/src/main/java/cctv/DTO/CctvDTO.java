package cctv.DTO;

import cctv.Entity.Cctv;
import lombok.*;


@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CctvDTO {
    private Long cctvId;
    private Long location;
    private Long cctvDate;

    public static CctvDTO toDTO(Cctv cctv) {
        return CctvDTO.builder()
                .cctvId(cctv.getCctvId())
                .location(cctv.getLocation())
                .cctvDate(cctv.getCctvDate())
                .build();
    }
}