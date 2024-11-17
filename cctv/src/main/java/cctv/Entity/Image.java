package cctv.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Table(name = "image")
@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageid")
    private Long imageId;

    @Column(name = "name")
    private String name;

    @Column(name = "cctvid")
    private Long cctvId;

    @Column(name = "path")  // S3 내부 이미지에 접근할 수 있는 URL
    private String path;

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
