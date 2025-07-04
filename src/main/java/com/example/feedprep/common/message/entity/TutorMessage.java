package com.example.feedprep.common.message.entity;


import com.example.feedprep.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class TutorMessage extends BaseTimeEntity {

    // 메세지의 기록 목적을 위해 연관 관계 설정 x, 사용 되는 다른 서비스 로직에서 연관관계를 확인할 것.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long userId;

    private Long documentId;

    private String fileName;
}
