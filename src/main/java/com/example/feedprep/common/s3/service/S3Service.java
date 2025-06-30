package com.example.feedprep.common.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    // S3에 파일 업로드 기능
    String uploadFile(MultipartFile file, String directory);

    // S3 파일을 조회 하는 기능, URL 반환한다.
    String createFileUrl(String key);

    // S3에 파일 삭제 기능, 비동기 처리
    void deleteFile(String fileKey);

    // 업로드 할 파일 크기 제한
    void limitedFileSize(MultipartFile file, Long size);

    // 제한할 파일 크기 타입 정의
    Long convertFileSizeType(Long size, String type);
}
