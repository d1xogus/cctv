package cctv.DTO;

import cctv.Entity.Cctv;
import cctv.Entity.Image;
import cctv.Entity.Log;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogDTO {
    private Long logId; // Log ID
    private Long imageId;
    private String name;
    private Cctv cctv;
    private String path;
    private String time;
    private String result; // 결과 메시지

    public static LogDTO toDTO(Log log) {
        return LogDTO.builder()
                .logId(log.getLogId())
                .imageId(log.getImageId())
                .name(log.getName())
                .cctv(log.getCctv())
                .path(log.getPath())
                .time(log.getTime())
                .result(log.getResult())
                .build();
    }
}

