package cctv.Controller;

import cctv.DTO.LogDTO;
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

    @GetMapping("/{roleName}")
    public List<Log> log(@PathVariable String roleName){
        return logService.get(roleName);
    }

    @PatchMapping("/{logId}")
    public ResponseEntity<Log> update(@PathVariable Long logId, @RequestBody LogDTO logDTO){
        return logService.update(logId, logDTO);
    }
}
