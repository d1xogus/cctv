package cctv.Controller;

import cctv.DTO.ImageUploadDTO;
import cctv.DTO.MemberDTO;
import cctv.Entity.Image;
import cctv.Entity.Member;
import cctv.Service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/")
    public List<String> uploadImages(@ModelAttribute ImageUploadDTO imageUploadDTO) {
        log.info("Received files count: {}", imageUploadDTO.getImages().size());
        for (MultipartFile file : imageUploadDTO.getImages()) {
            log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());
        }
        return imageService.uploadImage(imageUploadDTO);
    }

    @GetMapping("/")
    public List<Image> image() {
        return imageService.get();
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestParam List<Long> imageIds) {
        log.info("Deleting images with IDs: {}", imageIds);
        imageService.delete(imageIds);
        return ResponseEntity.ok("Images deleted successfully");
    }
}
