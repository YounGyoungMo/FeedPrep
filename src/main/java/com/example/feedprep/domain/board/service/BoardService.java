package com.example.feedprep.domain.board.service;

import com.example.feedprep.domain.board.dto.BoardRequestDto;
import com.example.feedprep.domain.board.dto.BoardResponseDto;
import com.example.feedprep.domain.board.dto.BoardSearchCondition;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BoardService {

    BoardResponseDto createBoard(BoardRequestDto requestDto);

    List<BoardResponseDto> getBoards(BoardSearchCondition condition);

    BoardResponseDto getBoard(Long boardId, HttpServletRequest request);

    void updateBoard(Long boardId, BoardRequestDto requestDto);

    void deleteBoard(Long boardId);

    //스크랩 관련
    boolean scrapBoard(Long boardId);
    boolean unscrapBoard(Long boardId);
    List<BoardResponseDto> getMyScrapList();
    boolean isBoardScrapped(Long boardId);

    // 인기 게시글
    List<BoardResponseDto> getPopularBoards();

    // 추천 기능 관련
    boolean cancelRecommendBoard(Long boardId);
    boolean recommendBoard(Long boardId);
}