package cctv.Service;

import cctv.DTO.ImageUploadDTO;
import cctv.Entity.Image;
import cctv.Entity.Log;
import cctv.Entity.Member;
import cctv.Repository.ImageRepository;
import cctv.Repository.LogRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    private final LogRepository logRepository;
    private final LogService logService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<String> uploadImage(ImageUploadDTO imageUploadDTO) {
        List<String> resultList = new ArrayList<>();

        for(MultipartFile multipartFile : imageUploadDTO.getImages()) {
            String value = upload(multipartFile);
            resultList.add(value);
        }
        log.info("resultList:",resultList);
        return resultList;
    }

    @Transactional
    public String upload(MultipartFile multipartFile){
        String name = multipartFile.getOriginalFilename();
        Image image = new Image(name);

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            amazonS3Client.putObject(bucket, name, multipartFile.getInputStream(), objectMetadata);

            String path = amazonS3Client.getUrl(bucket, name).toString();
            image.setPath(path);
        } catch(IOException e) {

        }

        imageRepository.save(image);
        logService.make(image);

        return image.getPath();
    }

    public List<Image> get(){
        return imageRepository.findAll();
    }

    public void delete(List<Long> imageIds) {
        List<Image> target = imageRepository.findAllById(imageIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        for (Image image : target) {
            String s3Path = image.getPath(); // S3 경로
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, s3Path);
            amazonS3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted S3 object with key: {}", s3Path);
        }
        logService.delete(imageIds);
        // 데이터베이스에서 삭제
        imageRepository.deleteAll(target);
        log.info("Deleted images: {}", imageIds);
    }
}
