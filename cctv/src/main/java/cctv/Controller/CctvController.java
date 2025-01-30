package cctv.Controller;

import cctv.DTO.CctvDTO;
import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;

import cctv.Service.CctvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/cctv")
public class CctvController {
    private final CctvService cctvService;

    @GetMapping("/{roleName}")
    public List<Cctv> get(@PathVariable String roleName){
        return cctvService.get(roleName);
    }

    @PostMapping("/")
    public CctvDTO make(@RequestBody CctvDTO cctvDTO){
        return cctvService.make(cctvDTO);
    }

    @PatchMapping("/{cctvId}")
    public ResponseEntity<Cctv> update(@PathVariable Long cctvId, @RequestBody LogDTO logDTO){
        return cctvService.update(cctvId, logDTO);
    }

    @DeleteMapping("/{cctvId}")
    public ResponseEntity<String> delete(@PathVariable Long cctvId) {
        Cctv target = cctvService.delete(cctvId);
        return (target != null) ?
                ResponseEntity.status(200).body("success") :
                ResponseEntity.status(404).build();
    }
}
