package cctv.DTO;

import cctv.Entity.Log;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogDTO {
    private Long logId; // Log ID
    private Long imageId; // 연결된 Image ID
    private String result; // 결과 메시지

    public static LogDTO toDTO(Log log) {
        return LogDTO.builder()
                .logId(log.getLogId())
                .imageId(log.getImage() != null ? log.getImage().getImageId() : null)
                .result(log.getResult())
                .build();
    }
}

