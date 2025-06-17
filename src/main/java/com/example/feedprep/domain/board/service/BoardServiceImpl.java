package com.example.feedprep.domain.board.service;

import com.example.feedprep.common.security.util.SecurityUtil;
import com.example.feedprep.domain.board.dto.BoardRequestDto;
import com.example.feedprep.domain.board.dto.BoardResponseDto;
import com.example.feedprep.domain.board.dto.BoardSearchCondition;
import com.example.feedprep.domain.board.entity.Board;
import com.example.feedprep.domain.board.repository.BoardRepository;
import com.example.feedprep.domain.recommend.entity.Recommend;
import com.example.feedprep.domain.scrap.entity.Scrap;
import com.example.feedprep.domain.scrap.repository.ScrapRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.example.feedprep.domain.user.enums.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import com.example.feedprep.domain.recommend.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository; // 추천 정보 저장

    @Override
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
    public BoardResponseDto getBoard(Long boardId) {
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

        return BoardResponseDto.from(board);
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
}