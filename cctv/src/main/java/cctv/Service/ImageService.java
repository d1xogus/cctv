package cctv.Service;

import cctv.DTO.ImageUploadDTO;
import cctv.DTO.ImageDTO;
import cctv.Entity.*;
import cctv.Repository.CctvRepository;
import cctv.Repository.ImageRepository;
import cctv.Repository.RoleRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    private final RoleRepository roleRepository;
    private final CctvRepository cctvRepository;
    private final LogService logService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<String> uploadImage(ImageUploadDTO imageUploadDTO) {
        List<String> resultList = new ArrayList<>();
        log.info("DTO: {}", imageUploadDTO);
        log.info("업로드할 파일 수: {}", imageUploadDTO.getImages().size());
        for(MultipartFile multipartFile : imageUploadDTO.getImages()) {
            log.info("파일이름: {}", multipartFile.getOriginalFilename());
            String value = upload(multipartFile, imageUploadDTO.getTimestamp(), imageUploadDTO.getStream());
            resultList.add(value);
        }
        log.info("resultList: {}",resultList);
        return resultList;
    }

    @Transactional
    public String upload(MultipartFile multipartFile, String timestamp, String cctv){
        String name = multipartFile.getOriginalFilename();
        Image image = new Image(name);

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            String contentType = multipartFile.getContentType();
            if (contentType == null || contentType.equals("application/octet-stream")) {
                if (name != null && name.endsWith(".mp4")) {
                    contentType = "video/mp4";
                } else if (name != null && name.endsWith(".jpg")) {
                    contentType = "image/jpeg";
                } else if (name != null && name.endsWith(".png")) {
                    contentType = "image/png";
                }
            }
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            amazonS3Client.putObject(bucket, name, multipartFile.getInputStream(), objectMetadata);

            String path = amazonS3Client.getUrl(bucket, name).toString();
            Cctv stream = cctvRepository.findByStream(cctv);

            image.setPath(path);
            image.setTime(timestamp);
            image.setCctv(stream);
        } catch(IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }

        imageRepository.save(image);


        return image.getPath();
    }

    @Transactional(readOnly = true)     //단순한 조회(READ) 작업이 수행될 때 사용, Lazy Loading이 필요한 경우
    public List<ImageDTO> get(Long roleId){
        Role role = roleRepository.findByRoleId(roleId);
        //List<Long> cctvIds = role.getCctvId();
        List<String> streams = new ArrayList<>(role.getSelectStream());
        return imageRepository.findByCctv_StreamIn(streams)
                .stream()
                .map(ImageDTO::new) // DTO 변환
                .collect(Collectors.toList());
    }

    @Transactional
    public void fail(List<Long> imageIds) {
        List<Image> target = imageRepository.findAllById(imageIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        for (Image image : target) {
//            String s3Path = image.getPath(); // S3 경로
//            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, s3Path);
//            amazonS3Client.deleteObject(deleteObjectRequest);
            logService.failMake(image);
        }

        imageRepository.deleteAll(target);
    }

    @Transactional
    public void success(List<Long> imageIds) {
        List<Image> target = imageRepository.findAllById(imageIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        for (Image image : target) {
            logService.successMake(image);
        }
//        imageRepository.deleteAll(target);
    }

    @Transactional
    public void delete(List<Long> imageIds) {
        List<Image> target = imageRepository.findAllById(imageIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        for (Image image : target) {
            String s3Path = image.getPath(); // S3 경로
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, s3Path);
            amazonS3Client.deleteObject(deleteObjectRequest);
        }
        imageRepository.deleteAll(target);
    }

    @Transactional
    public void deleteCctv(String stream) {
        List<Image> target = imageRepository.findByCctv_StreamIn(Collections.singletonList(stream));
        for (Image image : target) {
            String s3Path = image.getPath(); // S3 경로
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, s3Path);
            amazonS3Client.deleteObject(deleteObjectRequest);
        }
        imageRepository.deleteAll(target);
    }
}
