package com.example.devops.controller;

import com.example.devops.dto.GetPresignedUrlRequestDto;
import com.example.devops.service.FileUploadService;
import com.example.devops.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final FileUploadService fileUploadService;
    private final S3Service s3Service;

    /**
     * 클라이언트 -> 서버 -> S3 업로드
     */
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String fileUrl = fileUploadService.uploadFile(multipartFile);
        return ResponseEntity.ok(fileUrl);
    }

    /**
     * GET : 화면에 사용자의 프로필 이미지를 보여주기 위한 PresignedUrl을 발급한다.
     * PUT : 클라이언트가 요청한 이미지의 명으로 된 PresignedUrl을 받아 이미지를 업로드한다.
     */
    @GetMapping("/presigned-url")
    public ResponseEntity<String> generatePresignedUrl(@ModelAttribute GetPresignedUrlRequestDto request) {
        log.info("filename : {}", request.getFileName());
        String presignedUrl = s3Service.getPresignedUrl(request);
        return ResponseEntity.ok(presignedUrl);
    }
}
