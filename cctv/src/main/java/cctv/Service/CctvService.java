package cctv.Service;


import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Log;
import cctv.Repository.CctvRepository;
import cctv.Repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvService {
    private final CctvRepository cctvRepository;

    public List<Cctv> get(){
        return cctvRepository.findAll();
    }

    public ResponseEntity<Cctv> update(Long cctvId, LogDTO logDTO) {
        Cctv cctv = cctvRepository.findById(cctvId).orElseThrow(() -> new RuntimeException("Log not found"));
        // DTO의 정보를 사용해 로그 업데이트
        if (logDTO.getResult() != null) {
            logDTO.setResult(logDTO.getResult());
        }
        Cctv updatedCctv = cctvRepository.save(cctv);
        return ResponseEntity.ok(updatedCctv);
    }
}
