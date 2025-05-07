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

    @Column(name = "imageId")
    private Long imageId;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "stream")
    private Cctv cctv;

    @Column(name = "path")  // S3 내부 이미지에 접근할 수 있는 URL
    private String path;

    @Column(name = "time")
    private String time;

    @Column(name = "result")
    private String result;

    public static Log fromImage(Image image, String result) {
        return Log.builder()
                .imageId(image.getImageId())
                .name(image.getName())
                .cctv(image.getCctv())
                .path(image.getPath())
                .time(image.getTime())
                .result(result)
                .build();
    }

}
