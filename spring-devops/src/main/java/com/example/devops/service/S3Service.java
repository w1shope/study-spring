package com.example.devops.service;

import static com.example.devops.dto.PresignedUrlStatus.GET;

import com.example.devops.dto.GetPresignedUrlRequestDto;
import com.example.devops.dto.PresignedUrlStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final FileUploadService fileUploadService;

    /**
     * GET : 화면에 사용자의 프로필 이미지를 보여주기 위한 PresignedUrl을 발급한다.
     * PUT : S3에 이미지를 업로드하기 위한 PresignedUrl을 발급한다.
     */
    public String getPresignedUrl(GetPresignedUrlRequestDto request) {

        PresignedUrlStatus httpMethod = request.getHttpMethod();
        if (httpMethod == GET) {
            return fileUploadService.generatePresignedUrlForShowProfileImage(request.getFileName());
        } else {
            return fileUploadService.generatePresignedUrlForUpload(request);
        }
    }
}
