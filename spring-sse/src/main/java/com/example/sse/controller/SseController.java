package com.example.sse.controller;

import com.example.sse.dto.EventPayload;
import com.example.sse.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterService sseEmitterService;

    /**
     * 클라이언트의 이벤트 구독을 수락한다. text/event-stream은 SSE를 위한 Mime Type이다. 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
     */
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long userId) {
        return sseEmitterService.subscribe(userId);
    }

    /**
     * 이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.
     */
    @PostMapping("/broadcast/{userId}")
    public void broadcast(@PathVariable Long userId, @RequestBody EventPayload eventPayload) {
        sseEmitterService.broadcast(userId, eventPayload);
    }
}
