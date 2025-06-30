package com.example.feedprep.domain.board.service;

import com.example.feedprep.common.security.util.SecurityUtil;
import com.example.feedprep.common.util.IpUtil;
import com.example.feedprep.domain.board.dto.BoardRequestDto;
import com.example.feedprep.domain.board.dto.BoardResponseDto;
import com.example.feedprep.domain.board.dto.BoardSearchCondition;
import com.example.feedprep.domain.board.entity.Board;
import com.example.feedprep.domain.board.repository.BoardRepository;
import com.example.feedprep.domain.recommend.entity.Recommend;
import com.example.feedprep.domain.recommend.repository.RecommendRepository;
import com.example.feedprep.domain.scrap.entity.Scrap;
import com.example.feedprep.domain.scrap.repository.ScrapRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository; // 추천 정보 저장

    @Qualifier("template")
    private final RedisTemplate<String, Long> redisTemplate;

    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewcount:";
    private static final String VIEWED_MARKER_PREFIX = "post:viewed:";
    private static final long TTL_HOURS = 12L;

    @Override
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);

        Board board = Board.of(requestDto, user); // 수정된 Board.of() 사용
        boardRepository.save(board);
        return BoardResponseDto.from(board);
    }

    @Override
    public List<BoardResponseDto> getBoards(BoardSearchCondition condition) {
        return boardRepository.findAll().stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoard(Long boardId, HttpServletRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        User currentUser = userRepository.findByIdOrElseThrow(SecurityUtil.getCurrentUserId());

        // 비밀글이면 작성자 또는 튜터만 열람 가능
        if (board.isSecret()
                && !board.isOwner(currentUser)
                && !currentUser.getRole().equals(UserRole.APPROVED_TUTOR)
                && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("비밀글은 작성자, 튜터 또는 관리자만 조회할 수 있습니다.");
        }
        // 조회수 증가
        increaseViewCountIfAllowed(boardId, request);

        // Redis에서 조회수 가져와서 응답에 포함
        Long viewCount = getViewCount(boardId);
        BoardResponseDto dto = BoardResponseDto.from(board);
        dto.setViewCount(viewCount);
        return dto;
    }

    private void increaseViewCountIfAllowed(Long boardId, HttpServletRequest request) {
        String identifier;

        try {
            Long userId = SecurityUtil.getCurrentUserId();
            identifier = String.valueOf(userId);
        } catch (Exception e) {
            identifier = IpUtil.getClientIp(request);
        }

        String viewedKey = VIEWED_MARKER_PREFIX + boardId + ":" + identifier;
        Boolean alreadyViewed = redisTemplate.hasKey(viewedKey);

        if (Boolean.FALSE.equals(alreadyViewed)) {
            redisTemplate.opsForValue().increment(VIEW_COUNT_KEY_PREFIX + boardId);
            redisTemplate.opsForValue().set(viewedKey, 1L, TTL_HOURS, TimeUnit.HOURS);
        }
    }

    private Long getViewCount(Long boardId) {
        Long value = redisTemplate.opsForValue().get(VIEW_COUNT_KEY_PREFIX + boardId);
        return (value != null) ? value : 0L;
    }


    @Override
    @Transactional
    public void updateBoard(Long boardId, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        User currentUser = userRepository.findByIdOrElseThrow(SecurityUtil.getCurrentUserId());

        if (!board.isOwner(currentUser)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        board.update(requestDto);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        User currentUser = userRepository.findByIdOrElseThrow(SecurityUtil.getCurrentUserId());

        if (!board.isOwner(currentUser)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }

    @Override
    public boolean scrapBoard(Long boardId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        boolean exists = scrapRepository.existsByUserAndBoard(user, board);
        if (exists) return false;

        Scrap scrap = Scrap.of(user, board);
        scrapRepository.save(scrap);
        return true;
    }

    @Override
    public boolean unscrapBoard(Long boardId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        Scrap scrap = scrapRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new IllegalArgumentException("스크랩되지 않은 게시글입니다."));
        scrapRepository.delete(scrap);
        return true;
    }

    @Override
    public List<BoardResponseDto> getMyScrapList() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        List<Scrap> scraps = scrapRepository.findAllByUser(user);
        return scraps.stream()
                .map(scrap -> BoardResponseDto.from(scrap.getBoard()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBoardScrapped(Long boardId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        return scrapRepository.findByUserAndBoard(user, board).isPresent();
    }

    @Override
    public boolean recommendBoard(Long boardId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        boolean alreadyRecommended = recommendRepository.existsByUserAndBoard(user, board);
        if (alreadyRecommended) return false;

        Recommend recommend = Recommend.of(user, board);
        recommendRepository.save(recommend);
        board.increaseRecommendCount();

        return true;
    }

    @Override
    public List<BoardResponseDto> getPopularBoards() {
        List<Board> boards = boardRepository.findByRecommendCountGreaterThanEqualOrderByRecommendCountDesc(10);
        return boards.stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean cancelRecommendBoard(Long boardId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByIdOrElseThrow(userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        Recommend recommend = recommendRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 게시글입니다."));

        recommendRepository.delete(recommend);
        board.decreaseRecommendCount();

        return true;
    }

    // 조회수 증가 (Redis INCR)
    private void increaseViewCount(Long boardId) {
        redisTemplate.opsForValue().increment(VIEW_COUNT_KEY_PREFIX + boardId);
    }
}