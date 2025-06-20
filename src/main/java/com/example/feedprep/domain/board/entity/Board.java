package com.example.feedprep.domain.board.entity;

import com.example.feedprep.domain.board.dto.BoardRequestDto;
import com.example.feedprep.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String tag; // 예: "질문", "후기", "정보"

    @Column(nullable = false)
    private boolean secret; // 비밀글 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 (User와 연관관계 설정)

    @Column(nullable = false)
    private int recommendCount = 0;

    private long viewCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Board of(BoardRequestDto dto, User user) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .tag(dto.getTag())
                .secret(dto.isSecret())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .viewCount(0L)
                .build();
    }

    public void update(BoardRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.tag = dto.getTag();
        this.secret = dto.isSecret();
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseRecommendCount() {
        this.recommendCount++;
    }

    public int getRecommendCount() {
        return this.recommendCount;
    }

    public boolean isOwner(User user) {
        return this.user.getUserId().equals(user.getUserId());
    }

    public void decreaseRecommendCount() {
        if (this.recommendCount > 0) {
            this.recommendCount--;
        }
    }

    //조회수 설정 메서드 (스케줄러에서 사용)
    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}