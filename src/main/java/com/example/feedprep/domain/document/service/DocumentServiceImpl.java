package com.example.feedprep.domain.document.service;


import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.s3.service.S3Service;
import com.example.feedprep.domain.document.dto.responseDto.DocumentListResponseDto;
import com.example.feedprep.domain.document.dto.responseDto.DocumentResponseDto;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService{

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public DocumentResponseDto createDocument(MultipartFile file, String resume, Long userId) {

        if(file.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_FILE);
        }

        Long megabyte = s3Service.convertFileSizeType(5L, "mb");

        s3Service.limitedFileSize(file, megabyte);

        User user = userRepository.findByIdOrElseThrow(userId);

        // user의 이력서는 최대 5개까지 생성 제한
        long cntDocument = documentRepository.countByUser(user);
        if(cntDocument >= 5) {
            throw new CustomException(ErrorCode.DONT_CREATE_MORE);
        }

        String fileUrl = s3Service.uploadFile(file,resume);

        // s3 파일 업로드
        Document document = Document.builder().user(user).fileUrl(fileUrl).build();

        Document saveDocument = documentRepository.save(document);

        return new DocumentResponseDto(
            saveDocument.getDocumentId(),
            saveDocument.getUser().getName(),
            saveDocument.getUser().getRole(),
            saveDocument.getFileUrl(),
            saveDocument.getCreatedAt(),
            saveDocument.getModifiedAt()
        );
    }

    @Override
    public List<DocumentListResponseDto> getMyDocumentList(Long userId) {

        List<Document> documentList = documentRepository.findAllByUserUserId(userId);

        List<DocumentListResponseDto> documentListResponseDtos =
            documentList.stream().map(document -> new DocumentListResponseDto(
                document.getDocumentId(),
                document.getCreatedAt(),
                document.getModifiedAt()
            )).collect(Collectors.toList());

        return documentListResponseDtos;
    }

    @Override
    public String getMyDocument(Long documentId, Long userId) {

        Document document = documentRepository.findByIdOrElseThrow(documentId);

        // 본인 문서가 아니면 접근 권한 없음
        if(!document.getUser().getUserId().equals(userId)){
            throw new CustomException(ErrorCode.FOREIGN_DOCUMENT_ACCESS);
        }

        return s3Service.createFileUrl(document.getFileUrl());
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId, Long userId) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);

        // 본인 문서가 아니면 접근 권한 없음
        if(!document.getUser().getUserId().equals(userId)){
            throw new CustomException(ErrorCode.FOREIGN_DOCUMENT_ACCESS);
        }

        // DB에서 삭제 (DB에서 먼저 삭제 해야 아래 AWS S3 버킷에서 삭제 실패해도 Transactional로 복구됨)
        documentRepository.delete(document);

        // AWS S3 버킷에서 삭제 - 비동기
        s3Service.deleteFile(document.getFileUrl());
    }
}
