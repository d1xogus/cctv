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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @GetMapping("/{roleId}")
    public List<ImageDTO> image(@PathVariable Long roleId) {
        return imageService.get(roleId);
    }


    @GetMapping("/sse/{roleId}")  // 기존 경로 유지
    public SseEmitter stream(@PathVariable Long roleId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 300L); // 3분(30,000ms) 타임아웃 설정
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {  //  지속적으로 데이터 전송
                    List<ImageDTO> images = imageService.get(roleId);

                    try {
                        emitter.send(SseEmitter.event().data(images));
                    } catch (IOException e) {
                        System.out.println("클라이언트 연결이 끊어졌습니다: " + e.getMessage());
                        break; // 루프 중단 후 안전하게 종료
                    }

                    Thread.sleep(5000); // 5초마다 데이터 전송
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 유지
            } finally {
                emitter.complete();
            }
        });

        emitter.onCompletion(() -> {
            System.out.println("SSE 연결 종료: " + roleId);
            executor.shutdown();
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE 타임아웃 발생: " + roleId);
            executor.shutdown();
            emitter.complete();
        });

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
