package com.example.feedprep.common.s3.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{

    private static final Logger slackLogger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final S3Client s3Client;
    private final Environment env;
    private final S3Presigner s3Presigner;

    // 파일 저장소 명시
    private String getBucketName() {
        return env.getProperty("aws.s3.bucket");
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public String createFileUrl(String key) {

        String bucket = getBucketName();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .getObjectRequest(getObjectRequest)
            .build();

        try {
            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOT_CREATED_S3FILE_URL);
        }
    }

    @Override
    @Transactional
    @Async // 비동기 어노테이션
    public void deleteFile(String fileKey) {
        String bucket = getBucketName();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            slackLogger.info("S3 파일 삭제 성공 (비동기): {}", fileKey);
        } catch (Exception e) {
            slackLogger.warn("S3 파일 삭제 실패 (비동기): {} - {}", fileKey, e.getMessage(), e);
            throw new CustomException(ErrorCode.DONT_DELETE_S3FILE);
        }
    }

    @Override
    @Transactional
    public void limitedFileSize(MultipartFile file, Long size) {

        if(file.getSize() > size) {
            throw new CustomException(ErrorCode.OVER_LIMIT_FILESIZE);
        }
    }

    @Override
    @Transactional
    public Long convertFileSizeType(Long size, String type) {

        Long convertedSize;

        if(type == null || type.isBlank()) {
            convertedSize = size;
        } else if(type.equals("kb") || type.equals("KB") || type.equals("Kb") || type.equals("kB")) {
            convertedSize = size * 1024;
        } else if (type.equals("mb") || type.equals("MB") || type.equals("Mb") || type.equals("mB")) {
            convertedSize = size * 1024 * 1024;
        } else {
            throw new CustomException(ErrorCode.DONT_CONVERT_FILESIZE_TYPE);
        }

        return convertedSize;
    }
}
