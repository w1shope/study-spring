package com.example.devops.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * MultipartFile을 AWS S3에 업로드하고 업로드된 파일의 URL을 반환한다.
     *
     */
    public String uploadFile(MultipartFile multipartFile) {
        try {
            String uploadFileName = multipartFile.getOriginalFilename();
            String uploadFileUrl = "https://" + bucket + "/test" + uploadFileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            amazonS3Client.putObject(bucket, uploadFileName, multipartFile.getInputStream(),
                metadata);

            return uploadFileUrl;
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file");
        }
    }

    /**
     * AWS S3에 파일을 업로드하기 위해 유효시간이 30분인 PresignedUrl 생성
     */
    public String generatePresignedUrlForUpload(String fileName) {
        // 파일명을 UUID로 변경하여 중복을 방지한다.
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        Date expiration = new Date();
        long expireTimeMillis = expiration.getTime() +  (1000 * 60 * 30); // 유효시간 30분
        expiration.setTime(expireTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(bucket, uniqueFileName)
                .withMethod(com.amazonaws.HttpMethod.PUT)
                .withExpiration(expiration);

        log.info("PUT 요청");
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
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

        log.info("GET 요청");
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}
