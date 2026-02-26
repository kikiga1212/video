package com.example.video.Service;

import com.example.video.DTO.VideoDTO;
import com.example.video.Entity.VideoEntity;
import com.example.video.Repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

//삽입 : DTO->ENTITY->저장->ENTITY->DTO 전달
//수정 : DTO->ENTITY(부분수정)->저장->ENTITY->DTO 전달
//조회 : 검색단어->Entity조회->DTO 전달
//좋아요/싫어요 : ->Entity조회->변경->DTO 전달
@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {
    private final VideoRepository videoRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    //CRUD 기존
    //삽입
    public VideoDTO createVideo(VideoDTO dto) {
        //이미지와 동영상 파일이 존재하면 저장
        String thumbnailUrl = fileStorageService.store(dto.getThumbnailFile(), "thumbnails");
        String videoUrl = fileStorageService.store(dto.getVideoFile(), "videos");
        //UUID로 만들어진 이미지와 동영상 파일명을 dto(또는 entity)에 저장
        //entity로 변환전이면 dto로 작업
        if(thumbnailUrl != null) {
            dto.setThumbnailUrl(thumbnailUrl);
        }
        if(videoUrl != null) {
            dto.setVideoUrl(videoUrl);
        }
        //Entity에 Default선언된 변수->오류->수동초기값
        if(dto.getStatus() != null) {
            dto.setStatus("PUBLIC");
        }
        dto.setLikeCount(0L);
        dto.setViewCount(0L);

        VideoEntity entity = modelMapper.map(dto, VideoEntity.class); //주작업
        /* entity 변환이 되었으면 entity로 작업
        if(thumbnailUrl != null) {
            entity.setThumbnailUrl(thumbnailUrl);
        }
        if(videoUrl != null) {
            entity.setVideoUrl(videoUrl);
        }*/

        VideoEntity result = videoRepository.save(entity); //최종
        return toDTO(result);
    }
    //수정(삽입과 비슷) 삽입은 DTO만 존재, 수정은 대상인 id와 DTO가 존재
    public VideoDTO updateVideo(Long id, VideoDTO dto) {
        //1.조회(변경할 내용만 지정)
        VideoEntity read = videoRepository.findById(id).
                orElseThrow(()->new IllegalStateException("오류"));

        //이미지와 동영상 파일이 존재하면 저장
        String thumbnailUrl = fileStorageService.store(dto.getThumbnailFile(), "thumbnails");
        String videoUrl = fileStorageService.store(dto.getVideoFile(), "videos");

        //읽어온 데이터에 변경된 내용만 지정
        if(thumbnailUrl != null) {
            read.setThumbnailUrl(thumbnailUrl);
        }
        if(videoUrl != null) {
            read.setVideoUrl(videoUrl);
        }
        //HTML <input type="hidden" name="id"> 가 존재하면 삽입처럼 modelMapper변경해서 저장
        //input이 없으면 작업할 id가 없어서 추가(삽입)->수동으로 수정작업
        read.setTitle(dto.getTitle());
        read.setDescription(dto.getDescription());
        read.setUploader(dto.getUploader());
        read.setCategory(dto.getCategory());
        read.setTags(dto.getTags());
        read.setStatus(dto.getStatus());

        VideoEntity result = videoRepository.save(read); //최종
        return toDTO(result);
    }

    //전체조회(각 메소드별로 구현)
    //공개 동영상 목록
    public List<VideoDTO> getPublicVideos() {
        return videoRepository.findByStatusOrderByCreatedAtDesc("PUBLIC")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    //전체 목록(관리자용)
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findByAllOrderByCreatedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    //인기 동영상목록
    public List<VideoDTO> getPopularVideos() {
        return videoRepository.findByStatusOrderByCreatedAtDesc("PUBLIC")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    //키워드 검색목록
    public List<VideoDTO> searchVideos(String keyword) {
        //키워드가 없으면
        if(keyword==null || keyword.isBlank()) return getPublicVideos();

        return videoRepository.searchKeyword(keyword.trim())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    //카테고리 조회
    public List<VideoDTO> getVideosByCategory(String category) {
        return videoRepository.findByStatusAndCategoryOrderByCreatedAtDesc("PUBLIC", category)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    //개별조회(VideoDTO로 전달)-사용자가 동영상을 감상하기 위해 조회
    public VideoDTO getVideo(Long id) {
        if (videoRepository.existsById(id)) { //존재하면
            //조회수를 증가
            videoRepository.incrementViewCount(id);
        }
        //조회(조회수 증가 적용)
        VideoEntity entity = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("오류"));

        return toDTO(entity);
    }

    //관리자가 수정을 위해 조회하는 경우
    public VideoDTO getVoideForEdit(Long id) {
        VideoEntity entity = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("오류"));

        return toDTO(entity);
    }

    //삭제(데이터삭제, 삭제할 데이터와 관련된 파일도 삭제)
    public void deleteVideo(Long id) {
        //삭제 대상
        //Optional<VideoEntity> entity = videoRepository.findById(id);
        //if(entity.isPresent())
        //Optional은 변수호출시 get()를 사용 entity.get().getCategory()

        VideoEntity videoEntity = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("오류"));
        //이미지와 동영상을 삭제
        fileStorageService.delete(videoEntity.getThumbnailUrl());
        fileStorageService.delete(videoEntity.getVideoUrl());
        //원본 데이터 삭제
        videoRepository.deleteById(id);

    }

    //Entity를 DTO변환(모든 메소드사용 빈도가 높다)
    private VideoDTO toDTO(VideoEntity entity) {
        return modelMapper.map(entity, VideoDTO.class);
    }

    //DTO를 Entity변환(1번 빈도가 낮다.)
    private VideoEntity toEntity(VideoDTO dto) {
        return modelMapper.map(dto, VideoEntity.class);
    }

    //CRUD를 제외하고 추가로 필요한 메소드(좋아요, 부분수정, 존재여부 확인)-페이지 변경 없이 동작
    //좋아요
    public VideoDTO likeVideo(Long id) {
        VideoEntity entity = videoRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("오류"));
        videoRepository.incrementLikeCount(id);

        VideoEntity result = videoRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("오류"));
        return toDTO(result);
    }
}
