package cctv.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageUploadDTO {
    private List<MultipartFile> images = new ArrayList<>(); // 다중 파일
    private MultipartFile image;                            // 단일 파일
    private String timestamp;                               // 추가 데이터(시간)
}
