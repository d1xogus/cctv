package cctv.Service;


import cctv.DTO.CctvDTO;
import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Log;
import cctv.Repository.CctvRepository;
import cctv.Repository.LogRepository;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvService {
    private final CctvRepository cctvRepository;
    private final RoleRepository roleRepository;

    public List<Cctv> get(String roleName){
        List<Long> cctvIds = roleRepository.findByRoleName(roleName);
        return cctvRepository.findByCctvIdIn(cctvIds);
    }

    public CctvDTO make(CctvDTO cctvDTO){
        Cctv cctv = Cctv.toEntity(cctvDTO);
        Cctv savedCctv = cctvRepository.save(cctv);
        return CctvDTO.toDTO(savedCctv);
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

    public Cctv delete(Long cctvId) {
        Cctv deleted = cctvRepository.findByCctvId(cctvId);
        cctvRepository.delete(deleted);
        return deleted;
    }
}
