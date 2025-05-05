package cctv.Service;


import cctv.DTO.CctvDTO;
import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Log;
import cctv.Entity.Role;
import cctv.Repository.CctvRepository;
import cctv.Repository.LogRepository;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvService {
    private final CctvRepository cctvRepository;
    private final RoleRepository roleRepository;

    public List<Cctv> get(String roleName){
        Role role = roleRepository.findByRoleName(roleName);
        List<Long> cctvIds = role.getCctvId();

        return cctvRepository.findByCctvIdIn(cctvIds);
    }

    public CctvDTO make(CctvDTO cctvDTO){
        Cctv cctv = Cctv.toEntity(cctvDTO);
        Cctv savedCctv = cctvRepository.save(cctv);
        return CctvDTO.toDTO(savedCctv);
    }

    public ResponseEntity<Cctv> update(Long cctvId, CctvDTO cctvDTO) {
        Cctv cctv = cctvRepository.findById(cctvId).orElseThrow(() -> new RuntimeException("Log not found"));
        // DTO의 정보를 사용해 로그 업데이트
        if (cctvDTO.getCctvName() != null) {
            cctv.setCctvName(cctvDTO.getCctvName());
        }
        if (cctvDTO.getCctvDate() != null){
            cctv.setCctvDate(cctvDTO.getCctvDate());
        }
        if (cctvDTO.getLocation() != null){
            cctv.setLocation(cctvDTO.getLocation());
        }
        if (cctvDTO.getWebcamId() != null){
            cctv.setWebcamId(cctvDTO.getWebcamId());
        }
        if (cctvDTO.getStream() != null){
            cctv.setStream(cctvDTO.getStream());
        }
        Cctv updatedCctv = cctvRepository.save(cctv);
        return ResponseEntity.ok(updatedCctv);
    }

    public Cctv delete(Long cctvId) {
        Cctv deleted = cctvRepository.findByCctvId(cctvId);
        cctvRepository.delete(deleted);
        return deleted;
    }

    public List<Cctv> getAll() {
        return cctvRepository.findAll();
    }
}
