package com.example.feedprep.common.s3.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{

    private final S3Client s3Client;
    private final Environment env;

    // 파일 저장소 명시
    private String getBucketName() {
        return env.getProperty("aws.s3.bucket");
    }

    // S3에 파일 업로드 기능
    @Override
    public String uploadFile(MultipartFile file, String directory) {

        if(file.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_FILE);
        }

        String bucket = getBucketName();
        String fileName = directory + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .contentType(file.getContentType())
            .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL).build();

        try {
            s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    // S3에 파일 삭제 기능
    @Override
    @Async // 비동기 어노테이션
    public void deleteFile(String fileKey) {
        String bucket = getBucketName();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 (비동기): {}", fileKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 (비동기): {} - {}", fileKey, e.getMessage(), e);
            throw new CustomException(ErrorCode.DONT_DELETE_S3FILE);
        }
    }
}
