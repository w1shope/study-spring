package com.example.demo.controller;

import com.example.demo.service.QrService;
import com.google.zxing.WriterException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    @GetMapping("/qr")
    public Object createQr(@RequestParam String url) throws WriterException, IOException {
        Object qr = qrService.createQr(url);

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(qr);
    }
}
