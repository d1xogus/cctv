package cctv.Service;

import cctv.DTO.ImageUploadDTO;
import cctv.Entity.Image;
import cctv.Repository.ImageRepository;
import com.amazonaws.services.s3.AmazonS3Client;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;

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

        return image.getPath();
    }
}
