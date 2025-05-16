package cctv.Service;


import cctv.DTO.LogDTO;
import cctv.DTO.RoleDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Member;
import cctv.Entity.Role;
import cctv.Repository.MemberRepository;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;

    public List<Role> get(){
        return roleRepository.findAll();
    }

    @Transactional
    public RoleDTO make(RoleDTO roleDTO, String email) {
        //DTO를 엔터티로 변환
        Role role = Role.toEntity(roleDTO);
        // 엔터티를 데이터베이스에 저장
        Role savedRole = roleRepository.save(role);
        //저장된 엔터티를 DTO로 변환하여 반환
        Member target = memberRepository.findUserByEmail(email).orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
        target.setRole(role);
        return RoleDTO.toDTO(savedRole);
    }

    public ResponseEntity<Role> update(Long roleId, RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Log not found"));
        // DTO의 정보를 사용해 로그 업데이트
        if (roleDTO.getRoleName() != null) {
            role.setRoleName(roleDTO.getRoleName());
        }
        if (roleDTO.getSelectStream() != null){
            role.setSelectStream(roleDTO.getSelectStream());
        }
        if (roleDTO.getTotalStream() != null){
            role.setTotalStream(roleDTO.getTotalStream());
        }
        Role updatedRole = roleRepository.save(role);
        return ResponseEntity.ok(updatedRole);
    }

    @Transactional
    public ResponseEntity<Role> stream(Long roleId, RoleDTO roleDTO) {
        // 기존 Role 조회
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 기존 스트림 목록 가져오기
        List<String> currentStreams = new ArrayList<>(role.getTotalStream());

        // 새로운 스트림 추가 (중복 방지)
        if (roleDTO.getTotalStream() != null) {
            Set<String> updatedStreams = new HashSet<>(currentStreams);
            updatedStreams.addAll(roleDTO.getTotalStream());
            role.setTotalStream(new ArrayList<>(updatedStreams));
        }

        // 변경된 Role 저장
        Role updatedRole = roleRepository.save(role);
        return ResponseEntity.ok(updatedRole);
    }

    public Role delete(Long roleId) {
        Role deleted = roleRepository.findByRoleId(roleId);
        roleRepository.delete(deleted);
        return deleted;
    }
}
