package com.example.video.Service;

import com.example.video.DTO.VideoDTO;
import com.example.video.Entity.VideoEntity;
import com.example.video.Repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //Entity를 DTO변환(모든 메소드사용 빈도가 높다)
    private VideoDTO toDTO(VideoEntity videoEntity) {
        return modelMapper.map(videoEntity, VideoDTO.class);
    }

    //DTO를 Entity 변환(1번 빈도가 낮다)
    private VideoEntity toEntity(VideoDTO dto) {
        return modelMapper.map(dto, VideoEntity.class);
    }


}
