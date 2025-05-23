package cctv.Service;

import cctv.DTO.LogDTO;
import cctv.Entity.Image;
import cctv.Entity.Log;
import cctv.Entity.Role;
import cctv.Repository.LogRepository;
import cctv.Repository.RoleRepository;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final RoleRepository roleRepository;

    public List<Log> getSelect(Long roleId){
        Role role = roleRepository.findByRoleId(roleId);
        List<String> streams = role.getTotalStream();
        return logRepository.findByCctv_StreamIn(streams);
    }

    public List<Log> getTotal(Long roleId) {
        Role role = roleRepository.findByRoleId(roleId);
        List<String> streams = role.getSelectStream();
        return logRepository.findByCctv_StreamIn(streams);
    }


        public void successMake(Image image) {
        Long imageId = image.getImageId();

        // 로그가 이미 존재하면 저장하지 않음
        if (logRepository.existsByImageId(imageId)) {
            log.info("이미 로그가 존재하는 imageId입니다: {}", imageId);
            return;
        }

        Log log = Log.fromImage(image, "1");
        logRepository.save(log);
    }

    public void failMake(Image image) {
        log.info("삭제실행");
        Log log = Log.fromImage(image, "0");
        logRepository.save(log);
    }

    public void delete(List<Long> logIds) {
        List<Log> target = logRepository.findByLogIdIn(logIds);
        if (target.isEmpty()) {
            throw new RuntimeException("No images found to delete");
        }
        logRepository.deleteAll(target);
    }

    @Transactional
    public void deleteCctv(String stream) {
        List<Log> target = logRepository.findByCctv_StreamIn(Collections.singletonList(stream));
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
