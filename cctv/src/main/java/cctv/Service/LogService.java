package cctv.Service;

import cctv.DTO.LogDTO;
import cctv.Entity.Image;
import cctv.Entity.Log;
import cctv.Entity.Role;
import cctv.Repository.LogRepository;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final RoleRepository roleRepository;

    public List<Log> get(String roleName){
        Role role = roleRepository.findByRoleName(roleName);
        List<Long> cctvIds = role.getCctvId();
        return logRepository.findByImage_Cctv_CctvIdIn(cctvIds);
    }

    public void successMake(Image image) {
        Log log = new Log();
        log.setImage(image);
        log.setResult("1");
        log.setResult("Image uploaded successfully"); // 로그 결과 메시지
        logRepository.save(log);
    }

    public void failMake(Image image) {
        Log log = new Log();
        log.setImage(image);
        log.setResult("2");
        log.setResult("Image uploaded successfully"); // 로그 결과 메시지
        logRepository.save(log);
    }

    public void delete(List<Long> logIds) {
        List<Log> target = logRepository.findByLogIdIn(logIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        logRepository.deleteAll(target);
    }

    public ResponseEntity<Log> update(Long logId, LogDTO logDTO) {
        Log log = logRepository.findById(logId).orElseThrow(() -> new RuntimeException("Log not found"));
        // DTO의 정보를 사용해 로그 업데이트
        if (logDTO.getResult() != null) {
            log.setResult(logDTO.getResult());
        }
        Log updatedLog = logRepository.save(log);
        return ResponseEntity.ok(updatedLog);
    }
}
