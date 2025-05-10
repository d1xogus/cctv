package cctv.Controller;

import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;
import cctv.Entity.Image;
import cctv.Entity.Log;
import cctv.Repository.LogRepository;
import cctv.Service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/log")
public class LogController {
    private final LogService logService;

    @GetMapping("/{roleId}")
    public List<Log> log(@PathVariable Long roleId){
        return logService.get(roleId);
    }

    @PatchMapping("/{logId}")
    public ResponseEntity<Log> update(@PathVariable Long logId, @RequestBody LogDTO logDTO){
        return logService.update(logId, logDTO);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestParam List<Long> logIds) {
        logService.delete(logIds);
        return ResponseEntity.status(200).body("success");
    }
}
