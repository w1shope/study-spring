package com.example.sse.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventPayload {

    private String userId;
    private String username;
}
