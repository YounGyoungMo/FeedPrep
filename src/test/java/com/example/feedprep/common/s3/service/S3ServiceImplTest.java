package com.example.feedprep.common.s3.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private Environment env;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private S3Presigner s3Presigner;

    @Test
    @DisplayName("업로드 성공")
    void uploadFile_success() {
        // given
        String bucket = "test-bucket";
        String directory = "test-dir";
        String originalFileName = "test.pdf";
        String contentType = "application/pdf";
        byte[] fileContent = "dummy".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            originalFileName,
            contentType,
            fileContent
        );

        // when
        when(env.getProperty("aws.s3.bucket")).thenReturn(bucket);

        String result = s3Service.uploadFile(mockFile, directory);

        // then
        assertThat(result).contains(directory);
        assertThat(result).contains(originalFileName);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("업로드 할 파일이 없음")
    void uploadFile_NOT_FOUND_FILE() {
        // given
        String directory = "test-dir";
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.pdf",
            "application/pdf",
            new byte[0]
        );

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
            s3Service.uploadFile(emptyFile,directory)
        );

        // then
        assertEquals(ErrorCode.NOT_FOUND_FILE, exception.getErrorCode());
    }

    @Test
    @DisplayName("업로드 중에 문제 발생")
    void uploadFile_S3_UPLOAD_FAILED() {
        // given
        String bucket = "test-bucket";
        String directory = "test-dir";
        String originalFileName = "test.pdf";
        byte[] fileContent = "dummy".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            originalFileName,
            "application/pdf",
            fileContent
        );

        // when
        when(env.getProperty("aws.s3.bucket")).thenReturn(bucket);

        doThrow(new RuntimeException("S3 업로드 실패"))
            .when(s3Client)
            .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        CustomException exception = assertThrows(CustomException.class, () ->
            s3Service.uploadFile(mockFile,directory)
        );

        // then
        assertEquals(ErrorCode.S3_UPLOAD_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("S3 파일 접속 링크 생성 완료")
    void createFileUrl_success() throws MalformedURLException {
        // given
        String key = "fileName";
        String presignedUrl ="https://s3.aws.com/presignedUrl/file.pdf";

        // when
        URL testUrl = new URL(presignedUrl);
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(testUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        String result = s3Service.createFileUrl(key);

        // then
        assertEquals(presignedUrl, result);
    }

    @Test
    @DisplayName("S3 파일 접속 링크 생성 실패")
    void createFileUrl_NOT_CREATED_S3FILE_URL() {
        // given
        String key = "fileName";

        // when
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
            .thenThrow(new RuntimeException("접속 링크 생성 실패"));

        CustomException exception = assertThrows(CustomException.class, () -> {
            s3Service.createFileUrl(key);
        });

        // then
        assertEquals(ErrorCode.NOT_CREATED_S3FILE_URL, exception.getErrorCode());
    }

    @Test
    @DisplayName("파일 삭제 완료")
    void deleteFile_success() {
        // given
        String bucket = "test-bucket";
        String fileKey = "some-file-key";

        // when
        when(env.getProperty("aws.s3.bucket")).thenReturn(bucket);

        s3Service.deleteFile(fileKey);

        // then
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("파일 삭제 실패")
    void deleteFile_DONT_DELETE_S3FILE() {
        // given
        String bucket = "test-bucket";
        String fileKey = "some-file-key";

        // when
        when(env.getProperty("aws.s3.bucket")).thenReturn(bucket);

        doThrow(new RuntimeException("삭제 실패"))
            .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        CustomException exception = assertThrows(CustomException.class, () -> {
            s3Service.deleteFile(fileKey);
        });

        // then
        assertEquals(ErrorCode.DONT_DELETE_S3FILE, exception.getErrorCode());
    }

    @Test
    @DisplayName("업로드 할 파일 크기 제한")
    void limitedFileSize() {
        // given
        Long limitSize = 5L * 1024 * 1024;

        byte[] fileSize = new byte[7*1024*1024];

        MultipartFile file = new MockMultipartFile(
            "file",
            "testFile.txt",
            "text/plain",
            fileSize
        );

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            s3Service.limitedFileSize(file, limitSize);
        });

        // then
        assertEquals(ErrorCode.OVER_LIMIT_FILESIZE, exception.getErrorCode());
    }

    @DisplayName("요구 타입별 값 반환 확인")
    @ParameterizedTest
    @CsvSource({
        ", 5",
        "kb, 5120",
        "KB, 5120",
        "kB, 5120",
        "Kb, 5120",
        "mb, 5242880",
        "MB, 5242880",
        "Mb, 5242880",
        "mB, 5242880"
    })
    void convertFileSizeType(String type, Long expected) {
        // given
        Long Size = 5L;

        // when
        Long result = s3Service.convertFileSizeType(Size, type);

        // then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("")
    void convertFileSizeType_DONT_CONVERT_FILESIZE_TYPE() {
        // given
        Long Size = 5L;
        String type = "other";

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            s3Service.convertFileSizeType(Size,type);
        });

        // then
        assertEquals(ErrorCode.DONT_CONVERT_FILESIZE_TYPE, exception.getErrorCode());
    }
}