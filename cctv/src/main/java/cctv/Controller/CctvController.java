package cctv.Controller;

import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Log;

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

    @GetMapping("/")
    public List<Cctv> get(){
        return cctvService.get();
    }

    @PatchMapping("/{cctvId}")
    public ResponseEntity<Cctv> update(@PathVariable Long cctvId, @RequestBody LogDTO logDTO){
        return cctvService.update(cctvId, logDTO);
    }
}
