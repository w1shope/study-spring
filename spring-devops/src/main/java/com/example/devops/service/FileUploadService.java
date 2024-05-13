package com.example.devops.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.devops.dto.GetPresignedUrlRequestDto;
import com.example.devops.dto.UpdateUserProfileImageRequestDto;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AmazonS3Client amazonS3Client;
    private final UserService userService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * MultipartFile 을 AWS S3에 업로드하고 업로드된 파일의 URL을 반환한다.
     */
    public String uploadFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename(); // 실제 파일명
            String uploadFileName = UUID.randomUUID().toString() + "_" + originalFilename; // S3에 저장될 파일명

            ObjectMetadata metadata = new ObjectMetadata(); // 파일 메타데이터 생성
            metadata.setContentLength(multipartFile.getSize()); // 파일 크기
            metadata.setContentType(multipartFile.getContentType()); // 파일 타입

            // 파일 업로드
            amazonS3Client.putObject(bucket, uploadFileName, multipartFile.getInputStream(),
                metadata);

            return amazonS3Client.getResourceUrl(bucket, uploadFileName).toString(); // 업로드된 파일 URL
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file");
        }
    }

    /**
     * AWS S3에 파일을 업로드하기 위해 유효시간이 30분인 PresignedUrl 생성
     */
    public String generatePresignedUrlForUpload(GetPresignedUrlRequestDto request) {
        // 파일명을 UUID로 변경하여 중복을 방지한다.
        String uniqueFileName = UUID.randomUUID().toString() + "_" + request.getFileName();

        Date expiration = new Date();
        long expireTimeMillis = expiration.getTime() +  (1000 * 60 * 30); // 유효시간 30분
        expiration.setTime(expireTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(bucket, uniqueFileName)
                .withMethod(com.amazonaws.HttpMethod.PUT)
                .withExpiration(expiration);

        UpdateUserProfileImageRequestDto updateUserRequest = createUpdateUserProfileImageRequestDto(
            request, uniqueFileName);

        userService.updateUser(updateUserRequest); // 사용자의 프로필 이미지 업데이트

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    private static UpdateUserProfileImageRequestDto createUpdateUserProfileImageRequestDto(GetPresignedUrlRequestDto request,
        String uploadPresignedUrl) {
        return UpdateUserProfileImageRequestDto.builder()
            .profileImage(uploadPresignedUrl)
            .userId(request.getUserId())
            .build();
    }

    /**
     * AWS S3에 저장된 사진을 가져오기 위해 유효시간이 30분인 PresignedUrl 생성
     */
    public String generatePresignedUrlForShowProfileImage(String fileName) {
        Date expiration = new Date();
        long expireTimeMillis = expiration.getTime() +  (1000 * 60 * 30); // 유효시간 30분
        expiration.setTime(expireTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(com.amazonaws.HttpMethod.GET)
                .withExpiration(expiration);

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}
