package cctv.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Table(name = "image")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageId")
    private Long imageId;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "cctvId")
    private Cctv cctv;

    @Column(name = "path")  // S3 내부 이미지에 접근할 수 있는 URL
    private String path;

    @Column(name = "time")
    private String time;

    public Image(String name) {
        this.name = name;
        this.path = "";
    }

    // 이미지 파일의 확장자를 추출하는 메소드
    public String extractExtension(String name) {
        int index = name.lastIndexOf('.');
        return name.substring(index, name.length());
    }

    // 이미지 파일의 이름을 저장하기 위한 이름으로 변환하는 메소드
    public String getFileName(String name) {
        return UUID.randomUUID() + "." + extractExtension(name);
    }
}
