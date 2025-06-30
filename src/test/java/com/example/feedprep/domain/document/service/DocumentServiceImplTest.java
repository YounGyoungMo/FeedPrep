package com.example.feedprep.domain.document.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.s3.service.S3Service;
import com.example.feedprep.domain.document.dto.responseDto.DocumentListResponseDto;
import com.example.feedprep.domain.document.dto.responseDto.DocumentResponseDto;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private DocumentServiceImpl documentServiceImpl;

    @Test
    @DisplayName("이력서 등록 성공")
    void createDocument_success() {
        // given
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "content".getBytes());
        String resume = "resume.pdf";
        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        // when
        when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
        when(documentRepository.countByUser(user)).thenReturn(3L);
        when(s3Service.uploadFile(file,resume)).thenReturn("https://s3.aws.com/resume.pdf");

        Document document = Document.builder()
            .documentId(1L)
            .user(user)
            .fileUrl("https://s3.aws.com/resume.pdf")
            .build();

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentResponseDto responseDto = documentServiceImpl.createDocument(file,resume,userId);

        // then
        assertThat(responseDto)
            .extracting(
                DocumentResponseDto::getDocumentId,
                DocumentResponseDto::getName,
                DocumentResponseDto::getRole,
                DocumentResponseDto::getFileUrl
            )
            .containsExactly(
                1L,
                "name",
                UserRole.STUDENT,
                "https://s3.aws.com/resume.pdf"
            );
    }

    @Test
    @DisplayName("등록 할려는 파일 없음")
    void createDocument_not_found_file() {
        // given
        MultipartFile emptyFile = new MockMultipartFile("file", "", "application/pdf", new byte[0]);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
            documentServiceImpl.createDocument(emptyFile, "resume", 1L)
        );

        // then
        assertEquals(ErrorCode.NOT_FOUND_FILE, exception.getErrorCode());
    }

    @Test
    @DisplayName("이력서 등록은 최대 5개까지")
    void createDocument_dont_create_more() {
        // given
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "content".getBytes());
        String resume = "resume.pdf";
        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        // when
        when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
        when(documentRepository.countByUser(user)).thenReturn(5L);

        CustomException exception = assertThrows(CustomException.class, () ->
            documentServiceImpl.createDocument(file, resume, userId)
        );

        // then
        assertEquals(ErrorCode.DONT_CREATE_MORE, exception.getErrorCode());
    }

    @Test
    @DisplayName("문서 리스트 조회 성공")
    void getMyDocumentList() {
        // given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().userId(1L).name("name").role(UserRole.STUDENT).build();

        Document doc1 = Document.builder()
            .documentId(1L)
            .user(user)
            .build();

        Document doc2 = Document.builder()
            .documentId(2L)
            .user(user)
            .build();

        // when
        when(documentRepository.findAllByUserUserId(userId)).thenReturn(List.of(doc1, doc2));

        List<DocumentListResponseDto> result = documentServiceImpl.getMyDocumentList(userId);

        // then
        assertThat(result)
            .hasSize(2)
            .extracting(DocumentListResponseDto::getDocumentId)
            .containsExactly(1L,2L);
    }

    @Test
    @DisplayName("문서 단건 조회 성공")
    void getMyDocument_success() {
        // given
        Long documentId = 1L;
        Long userId = 1L;
        String presignedUrl ="https://s3.aws.com/presignedUrl/file.pdf";

        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        Document document = Document.builder()
            .documentId(documentId)
            .user(user)
            .fileUrl("https://s3.aws.com/file.pdf")
            .build();

        // when
        when(documentRepository.findByIdOrElseThrow(documentId)).thenReturn(document);
        when(s3Service.createFileUrl(document.getFileUrl())).thenReturn(presignedUrl);

        String response = documentServiceImpl.getMyDocument(documentId, userId);

        // then
        assertEquals(presignedUrl, response);
    }

    @Test
    @DisplayName("문서 단건 조회 실패 - 접근 권한 없음")
    void getMyDocument_foreign_document_access() {
        // given
        Long documentId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;

        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        Document document = Document.builder()
            .documentId(documentId)
            .user(user)
            .fileUrl("https://s3.aws.com/file.pdf")
            .build();

        // when
        when(documentRepository.findByIdOrElseThrow(documentId)).thenReturn(document);

        CustomException exception = assertThrows(CustomException.class, () ->
            documentServiceImpl.getMyDocument(documentId,otherUserId)
        );

        // then
        assertEquals(ErrorCode.FOREIGN_DOCUMENT_ACCESS, exception.getErrorCode());
    }

    @Test
    @DisplayName("문서 삭제 성공")
    void deleteDocument_success() {
        // given
        Long userId = 1L;
        Long documentId = 1L;

        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        Document document = Document.builder()
            .documentId(documentId)
            .user(user)
            .fileUrl("https://s3.aws.com/file.pdf")
            .build();

        // when
        when(documentRepository.findByIdOrElseThrow(documentId)).thenReturn(document);

        documentServiceImpl.deleteDocument(documentId, userId);

        // then
        verify(documentRepository).delete(document);
        verify(s3Service).deleteFile(document.getFileUrl());
    }

    @Test
    @DisplayName("문서 삭제 실패 - 접근 권한 없음")
    void deleteDocument_foreign_document_access() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long documentId = 1L;

        User user = User.builder().userId(userId).name("name").role(UserRole.STUDENT).build();

        Document document = Document.builder()
            .documentId(documentId)
            .user(user)
            .fileUrl("https://s3.aws.com/file.pdf")
            .build();

        // when
        when(documentRepository.findByIdOrElseThrow(documentId)).thenReturn(document);

        CustomException exception = assertThrows(CustomException.class, () ->
            documentServiceImpl.deleteDocument(documentId,otherUserId)
        );

        // then
        assertEquals(ErrorCode.FOREIGN_DOCUMENT_ACCESS, exception.getErrorCode());
    }
}