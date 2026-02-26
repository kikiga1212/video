package com.example.video.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//ERD를 참고해서 테이블생성
@Entity
@Table(name = "video")
@Getter @Setter
@ToString @Builder
@AllArgsConstructor @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)//날짜 자동생성
public class VideoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//일련번호
    @Column(length = 200, nullable = false)
    private String title;//동영상제목
    @Column(columnDefinition = "TEXT")
    private String description;//동영상 설명
    @Column(length = 100, nullable = false)
    private String uploader;//업로더(채널명)
    @Column(length = 50)
    private String category;//카테고리
    @Column(length = 200)
    private String tags;//태그
    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;//조회수  Long 형  초기값 지정시   L 뒤에 붙인다.
    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;//좋아요
    @Column(length = 500)
    private String thumbnailUrl;//동영상에 해당하는 썸네일 이미지 주소
    @Column(length = 500)
    private String videoUrl;//동영상 또는 파일 주소
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status ="PUBLIC";//동영상상태(공개(PUBLIC), 비공개(PRIVATE),일부공개(UNLISTED))
    @Column(nullable = false, updatable = false)
    @CreatedDate//@EntityListeners선언되어야 함
    private LocalDateTime createdAt;//업로드일시
    @LastModifiedDate
    private LocalDateTime updatedAt;//수정일시

}
