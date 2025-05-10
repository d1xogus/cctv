package cctv.Controller;


import cctv.DTO.LogDTO;
import cctv.DTO.RoleDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Role;
import cctv.Service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/role")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<Role> get(){
        return roleService.get();
    }

    @PostMapping
    public RoleDTO make(@RequestBody RoleDTO roleDTO){ // modelattribute로 받은 데이터와 roleDTO를 매핑
        return roleService.make(roleDTO);
    }

    @PatchMapping("/{roleId}")
    public ResponseEntity<Role> update(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO){
        return roleService.update(roleId, roleDTO);
    }

    @PostMapping("/{roleId}")
    public ResponseEntity<Role> stream(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO){
        return roleService.update(roleId, roleDTO);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long roleId) {
        Role target = roleService.delete(roleId);
        return (target != null) ?
                ResponseEntity.status(200).body("success") :
                ResponseEntity.status(404).build();
    }
}
