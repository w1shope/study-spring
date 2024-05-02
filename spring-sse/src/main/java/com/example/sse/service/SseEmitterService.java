package com.example.sse.service;

import com.example.sse.dto.EventPayload;
import com.example.sse.repository.EmitterRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    // SSE 이벤트 타임아웃 시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    /**
     * 클라이언트의 이벤트 구독을 허용하는 메서드
     */
    public SseEmitter subscribe(Long userId) {
        // sse의 유효 시간이 만료되면, 클라이언트에서 다시 서버로 이벤트 구독을 시도한다.
        SseEmitter sseEmitter = emitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // Emitter의 유효 시간이 만료되면 emitter 삭제
        // 유효 시간이 만료되었다는 것은 클라이언트와 서버가 연결된 시간동안 아무런 이벤트가 발생하지 않은 것을 의미한다.
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(userId));

        // 첫 구독시에 이벤트를 발생시킨다.
        // sse 연결이 이루어진 후, 하나의 데이터로 전송되지 않는다면 sse의 유효 시간이 만료되고 503 에러가 발생한다.
        sendToClient(userId, "subscribe event, userId : " + userId);

        return sseEmitter;
    }

    /**
     * 이벤트가 구독되어 있는 클라이언트에게 데이터를 전송
     */
    public void broadcast(Long userId, EventPayload eventPayload) {
        sendToClient(userId, eventPayload);
    }

    private void sendToClient(Long userId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);
        try {
            sseEmitter.send(
                SseEmitter.event()
                    .id(userId.toString())
                    .name("sse")
                    .data(data)
            );
        } catch (IOException ex) {
            emitterRepository.deleteById(userId);
            throw new RuntimeException("연결 오류 발생");
        }
    }

}
