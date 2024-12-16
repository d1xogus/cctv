package cctv.Service;

import cctv.Entity.Image;
import cctv.Entity.Log;
import cctv.Repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    public List<Log> get(){
        return logRepository.findAll();
    }

    public void make(Image image) {
        Log log = new Log();
        log.setImage(image);
        log.setResult("Image uploaded successfully"); // 로그 결과 메시지
        logRepository.save(log);
    }

    public void delete(List<Long> imageIds) {
        List<Log> target = logRepository.findByImage_ImageIdIn(imageIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        logRepository.deleteAll(target);
    }
}
