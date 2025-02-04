package cctv.Entity;

import cctv.DTO.LogDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "Log")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logId")
    private Long logId;

    @OneToOne
    @JoinColumn(name = "imageId")
    private Image image;

    @Column(name = "result")
    private String result;

    public static Log toEntity(LogDTO logDTO, Image image) {
        return Log.builder()
                .logId(logDTO.getLogId())
                .image(image) // 이미지를 직접 매핑
                .result(logDTO.getResult())
                .build();
    }
}
