package cctv.Controller;

import cctv.DTO.ImageUploadDTO;
import cctv.Service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public List<String> uploadImages(@ModelAttribute ImageUploadDTO imageUploadDTO) {
        log.info("Received files count: {}", imageUploadDTO.getImages().size());
        for (MultipartFile file : imageUploadDTO.getImages()) {
            log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());
        }
        return imageService.uploadImage(imageUploadDTO);
    }

}
