package cctv.Controller;

import cctv.DTO.ImageDTO;
import cctv.DTO.ImageUploadDTO;
import cctv.Entity.Image;
import cctv.Service.ImageService;
import cctv.Service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/image")
public class ImageController {
    private final ImageService imageService;
    private final LogService logService;

    @PostMapping("/")
    public List<String> uploadImages(@ModelAttribute ImageUploadDTO imageUploadDTO) {
        if (imageUploadDTO.getImage() != null) {
            imageUploadDTO.setImages(List.of(imageUploadDTO.getImage())); // 단일 파일을 리스트로 변환
            log.info("Single file received: {}, size: {}",
                    imageUploadDTO.getImage().getOriginalFilename(),
                    imageUploadDTO.getImage().getSize());
        }

        // 다중 파일 처리 로그
        if (!imageUploadDTO.getImages().isEmpty()) {
            log.info("Received files count: {}", imageUploadDTO.getImages().size());
            for (MultipartFile file : imageUploadDTO.getImages()) {
                log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());
            }
        }
        return imageService.uploadImage(imageUploadDTO);
    }

    @GetMapping("/{roleName}")
    public List<ImageDTO> image(@PathVariable String roleName) {
        return imageService.get(roleName);
    }


    @GetMapping("/sse/{roleName}")  // 기존 경로 유지
    public SseEmitter stream(@PathVariable String roleName) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                while (true) {  //  지속적으로 데이터 전송
                    List<ImageDTO> images = imageService.get(roleName);
                    emitter.send(SseEmitter.event().data(images));
                    Thread.sleep(5000); //  5초마다 전송
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @DeleteMapping("/fail")
    public ResponseEntity<String> fail(@RequestParam List<Long> imageIds) {
        imageService.fail(imageIds);
        return ResponseEntity.ok("successfully");
    }

    @PostMapping("/success")
    public ResponseEntity<String> success(@RequestParam List<Long> imageIds) {
        imageService.success(imageIds);
        return ResponseEntity.ok("successfully");
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestParam List<Long> imageIds) {
        imageService.delete(imageIds);
        return ResponseEntity.ok("Images deleted successfully");
    }
}
