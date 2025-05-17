package cctv.Controller;

import cctv.DTO.CctvDTO;
import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;

import cctv.Service.CctvService;
import cctv.Service.ImageService;
import cctv.Service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cleanguard/cctv")
public class CctvController {
    private final CctvService cctvService;
    private final ImageService imageService;
    private final LogService logService;

    @GetMapping("/select/{roleId}")
    public List<Cctv> getSelect(@PathVariable Long roleId){
        return cctvService.getSelect(roleId);
    }

    @GetMapping("/total/{roleId}")
    public List<Cctv> getTotal(@PathVariable Long roleId){
        return cctvService.getTotal(roleId);
    }

    @GetMapping("/")
    public List<Cctv> getAll(){
        return cctvService.getAll();
    }

    @GetMapping("/sse")
    public SseEmitter streamCctv() {
        SseEmitter emitter = new SseEmitter(10 * 60 * 300L); // 약 3분 타임아웃
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    List<Cctv> cctvs = cctvService.getAll();
                    try {
                        emitter.send(SseEmitter.event().data(cctvs));
                    } catch (IOException e) {
                        System.out.println("클라이언트 연결 끊김: " + e.getMessage());
                        break;
                    }

                    Thread.sleep(6000); // 6초마다 전송
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                emitter.complete();
            }
        });

        emitter.onCompletion(() -> {
            System.out.println("SSE 연결 종료: cctv 전체");
            executor.shutdown();
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE 타임아웃 발생: cctv 전체");
            executor.shutdown();
            emitter.complete();
        });

        return emitter;
    }

    @PostMapping("/")
    public CctvDTO make(@RequestBody CctvDTO cctvDTO){
        return cctvService.make(cctvDTO);
    }

    @PatchMapping("/{stream}")
    public ResponseEntity<Cctv> update(@PathVariable String stream, @RequestBody CctvDTO cctvDTO){
        return cctvService.update(stream, cctvDTO);
    }

    @DeleteMapping("/{stream}")
    public ResponseEntity<String> delete(@PathVariable String stream) {
        imageService.deleteCctv(stream);
        logService.deleteCctv(stream);
        Cctv target = cctvService.delete(stream);
        return (target != null) ?
                ResponseEntity.status(200).body("success") :
                ResponseEntity.status(404).build();
    }
}
