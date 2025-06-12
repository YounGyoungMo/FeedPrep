package com.example.feedprep.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReplyRequestDto {
    private Long parentId;   // 부모 댓글 ID
    private String content;  // 대댓글 내용
}