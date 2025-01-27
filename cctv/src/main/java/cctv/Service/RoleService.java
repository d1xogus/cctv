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

    public Role make(RoleDTO roleDTO) {
        Role role = new Role();
        return null;
    }
}
