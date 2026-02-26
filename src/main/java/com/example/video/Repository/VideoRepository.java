package com.example.video.Repository;

import com.example.video.Entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    //CRUD(삽입, 전체조회, 개별조회, 수정, 삭제)-기본제공
    //id를 이용하지 않는 조회관련
    //findBy(Entity 변수명)(조건)(관계연산식, And, Or)(Entity 변수명)(조건)....
    //변수명 갯수만큼 인수값이 필요함
    //많은 변수를 사용할때는 Query로 직접작성

    //전체목록조회(입력한 상태에 맞는 레코드를 내림차순으로 정렬)
    List<VideoEntity> findByStatusOrderByCreatedAtDesc(String status);

    //관리자 페이지에서 사용할 전체목록
    List<VideoEntity>findByAllOrderByCreatedAtDesc();

    //제목 또는 설명으로 검색
    //List<VideoEntity> findByStatusAndTitleContainingAndDescriotuinContaining("PUBLIC", String title, String description);
    //테이블 이름에 별칭을 지정해서 사용
    //select 필드명 ... from 테이블명 별칭 where 조건 order by 정렬
    //숫자는 = 비교, 문자는 like 비교
    //Entity를 이용한 Query는 JPQL, 데이터베이스 테이블을 이용한 Query 는 SQL
    @Query("SELECT v FROM VideoEntity v " + //뒤에 빈공백 유지
            "WHERE v.status = 'PUBLIC' AND " +
            "(v.title LIKE %:keyword% OR v.description LIKE %:keyword%" +
            "OR v.tags LIKE %:keyword%) ORDER BY v.createdAt DESC")
    List<VideoEntity> searchKeyword(@Param("keyword")String keyword);

    //카테고리별 조회(일치하는 자료만 조회), Containing(포함된 자료 조회-비슷)
    List<VideoEntity> findByStatusAndCategoryOrderByCreatedAtDesc(String status, String category);

    //업로드별 조회
    List<VideoEntity> findByUploaderOrderByCreatedAtDesc(String uploader);

    //인기순 조회
    List<VideoEntity> findByViewCountGreaterThanOrderByViewCountDesc(Long viewCount);

    //부분수정 관련(findBy~, save, saves, delete, deletes 제공)
    //조회수 증가(동영상 선택시 증가)
    //UPDATE 테이블명 별칭 SET 필드명 = 변경값 WHERE 조건
    @Modifying
    @Query("UPDATE VideoEntity v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
    void incrementViewCount(@Param("id")Long id);

    //좋아요 증가(좋아요 버튼 클릭시 증가)
    @Modifying
    @Query("UPDATE VideoEntity v SET v.likeCount = v.likeCount + 1 WHERE v.id = :id")
    void incrementLikeCount(@Param("id")Long id);

}
