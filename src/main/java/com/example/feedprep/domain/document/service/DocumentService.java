package com.example.feedprep.domain.document.service;

import com.example.feedprep.domain.document.dto.responseDto.DocumentListResponseDto;
import com.example.feedprep.domain.document.dto.responseDto.DocumentResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    DocumentResponseDto createDocument(MultipartFile file, String resume, Long userId);

    List<DocumentListResponseDto> getMyDocumentList(Long userId);

    String getMyDocument(Long documentId, Long userId);

    void deleteDocument(Long documentId, Long userId);
}
