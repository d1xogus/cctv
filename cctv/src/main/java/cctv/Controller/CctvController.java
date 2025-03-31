package cctv.Controller;

import cctv.DTO.CctvDTO;
import cctv.DTO.LogDTO;
import cctv.Entity.Cctv;

import cctv.Service.CctvService;
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

    @GetMapping("/{roleName}")
    public List<Cctv> get(@PathVariable String roleName){
        return cctvService.get(roleName);
    }

    @GetMapping("/")
    public List<Cctv> getAll(){
        return cctvService.getAll();
    }

    @GetMapping("/sse/cctv")
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

                    Thread.sleep(5000); // 5초마다 전송
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

    @PatchMapping("/{cctvId}")
    public ResponseEntity<Cctv> update(@PathVariable Long cctvId, @RequestBody CctvDTO cctvDTO){
        return cctvService.update(cctvId, cctvDTO);
    }

    @DeleteMapping("/{cctvId}")
    public ResponseEntity<String> delete(@PathVariable Long cctvId) {
        Cctv target = cctvService.delete(cctvId);
        return (target != null) ?
                ResponseEntity.status(200).body("success") :
                ResponseEntity.status(404).build();
    }
}
