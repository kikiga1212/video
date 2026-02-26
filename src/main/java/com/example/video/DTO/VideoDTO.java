package com.example.video.DTO;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

//화면디지안을 참고해서
//목록,삽입,수정,삭제 등 공통으로 사용
@Getter @Setter
@Builder @ToString
@NoArgsConstructor @AllArgsConstructor
public class VideoDTO {
    private Long id;//일련번호
    private String title;//동영상제목
    private String description;//동영상 설명
    private String uploader;//업로더(채널명)
    private String category;//카테고리
    private String tags;//태그
    private Long viewCount = 0L;//조회수  Long 형  초기값 지정시   L 뒤에 붙인다.
    private Long likeCount = 0L;//좋아요
    private String thumbnailUrl;//동영상에 해당하는 썸네일 이미지 주소
    private String videoUrl;//동영상 또는 파일 주소
    private String status ="PUBLIC";//동영상상태(공개(PUBLIC), 비공개(PRIVATE),일부공개(UNLISTED))
    private LocalDateTime createdAt;//업로드일시
    private LocalDateTime updatedAt;//수정일시

    //동영상 관리, 삽입, 수정=>input type="file"
    private MultipartFile videoFile;
    private MultipartFile thumbnailFile;

    //조회수 단위변경(1000->1k, 1000000->1M)\
    public String getFormattedViewCount() {
        if(viewCount == null) return "0";
        if(viewCount >= 1_000_000) return String.format("%.1fM", viewCount / 1_000_000.0);
        if(viewCount >= 1_000) return String.format("%.1fK", viewCount / 1_000);
        return String.valueOf(viewCount);//숫자를 문자열로 변환
    }
    //업로드 후 경과 시간표시
    public String getTimeAgo(){
        if(createdAt == null) return "";

        //생성일과 현재시간의 사이값(범위)=>날수로
        long days = java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
        //날수에 따라 단위를 변경
        if(days == 0) return "오늘";
        if(days<7) return days+"일 전";//1~6
        if(days<30) return (days/7) + "주 전";//7~29
        if(days<365) return (days/30) + "개월 전";//30~364
        return (days/365) + "년 전"; //365~
    }

}
