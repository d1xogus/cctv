package cctv.DTO;

import cctv.Entity.Image;
import cctv.Entity.Log;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogDTO {
    private Long logId; // Log ID
    private Image image; // 연결된 Image
    private String result; // 결과 메시지

    public static LogDTO toDTO(Log log) {
        return LogDTO.builder()
                .logId(log.getLogId())
                .image(log.getImage() != null ? log.getImage() : null)
                .result(log.getResult())
                .build();
    }
}

