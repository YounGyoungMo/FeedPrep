package com.example.feedprep.domain.comment.service;

import com.example.feedprep.domain.board.entity.Board;
import com.example.feedprep.domain.board.repository.BoardRepository;
import com.example.feedprep.domain.comment.dto.CommentReplyRequestDto;
import com.example.feedprep.domain.comment.dto.CommentRequestDto;
import com.example.feedprep.domain.comment.dto.CommentResponseDto;
import com.example.feedprep.domain.comment.entity.Comment;
import com.example.feedprep.domain.comment.repository.CommentRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createComment(Long boardId, Long userId, CommentRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .content(requestDto.getContent())
                .isDeleted(false)
                .build();

        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void replyComment(Long boardId, Long parentCommentId, Long userId, CommentReplyRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Comment reply = Comment.builder()
                .board(board)
                .user(user)
                .parent(parent)
                .content(requestDto.getContent())
                .isDeleted(false)
                .build();

        commentRepository.save(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardId, Long currentUserId) {
        List<Comment> comments = commentRepository.findByBoardIdAndParentIsNull(boardId);
        return comments.stream()
                .map(comment -> CommentResponseDto.fromWithReplies(comment, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, Long userId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(requestDto.getContent());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        comment.softDelete();
    }
}
