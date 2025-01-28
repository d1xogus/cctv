package cctv.Service;


import cctv.DTO.RoleDTO;
import cctv.Entity.Role;
import cctv.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> get(){
        return roleRepository.findAll();
    }

    public RoleDTO make(RoleDTO roleDTO) {
        //DTO를 엔터티로 변환
        Role role = Role.toEntity(roleDTO);
        // 엔터티를 데이터베이스에 저장
        Role savedRole = roleRepository.save(role);
        //저장된 엔터티를 DTO로 변환하여 반환
        return RoleDTO.toDTO(savedRole);
    }
}
