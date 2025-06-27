package com.example.feedprep.domain.user.entity;

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String email;

    private String password;

    private String address;

    private String introduction;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long point;

    private Double rating;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserTechStack> userTechStacks = new ArrayList<>();

    // 소셜 로그인 관련 필드
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;  // KAKAO, NAVER, GOOGLE 등

    private String providerId; // OAuth 제공자가 주는 사용자 ID

    public User(String name, String email, String password, String address, String introduction,
        UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.introduction = introduction;
        this.role = role;
        this.point = 0L;
    }

    public User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.point = 0L;
    }

    // 소셜 회원 가입
    public User(String nickname, String email,String profileImageUrl, UserRole role, OAuthProvider provider, String providerId) {
        this.name = nickname;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.role = role;
        this.point = 0L;

        this.provider = provider;
        this.providerId = providerId;
    }

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }
}
