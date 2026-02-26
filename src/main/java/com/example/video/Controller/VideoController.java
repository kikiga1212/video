package com.example.video.Controller;

import com.example.video.DTO.VideoDTO;
import com.example.video.Service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    //카테고리(열거형으로 정의해서)
    private static final List<String> CATEGORIES =
            List.of("전체","교육","음악","게임","여행","스포츠","엔터테인먼트","뉴스","기타");

    //CRUD를 처리를 위한 맵핑
    //조회(index 시작과 함께 사용도 가능)
    //전달하는 변수와 받는 변수명이 같으면  RequestParam 생략가능, 제약조건이 필요하면 사용
    //검색어, 카테고리, 정렬은 요청시 변수나 값이 생략되어도 상관없다.
    @GetMapping("/")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String sort,
                       Model model){
        List<VideoDTO> videos;
        String pageTitle="홈";

        //검색어와 카테고리에 따라 Service  사용할 메소드를 지정
        if(keyword != null && keyword.isBlank()){//검색어가 있으면 검색어 조회
            videos = videoService.searchVideos(keyword);
            pageTitle ="\""+keyword+"\" 검색결과";
        }else if(category !=null && !category.isBlank() && !category.equals("전체")){//카테고리로 조회
            videos = videoService.getVideosByCategory(category);
            pageTitle = category;
        }else if("popular".equals(sort)){//인기동영상 조회
            videos = videoService.getPopularVideos();
            pageTitle = "인기 동영상";
        }else {//위 조건에 해당하지 않으면 일반 조회
            videos = videoService.getPublicVideos();
        }
        //HTML에서 받은 변수, Service에서 받은 변수
        model.addAttribute("videos", videos);//조회한 결과값
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("keyword", sort);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("sort",sort);

        return "index";//index.html 시작페이지, 목록페이지
    }

    //개별조회(상세보기)
    @GetMapping("/video/{id}")
    public String watch(@PathVariable Long id, Model model){
        try{
            VideoDTO video = videoService.getVideo(id);//재생정보
            //나중에 필터를 이용해서 특정갯수만큼 출력
            List<VideoDTO> related = videoService.getAllVideos();//오른쪽에 영상 목록
            model.addAttribute("video", video);
            model.addAttribute("related", related);
            model.addAttribute("categories", CATEGORIES);
            return "video/watch";
        }catch (Exception e){
            return "redirect:/?error=동영상을 찾을 수 없습니다.";
        }
    }
    //삽입폼(동영상 올리는 폼)
    @GetMapping("/video/upload")
     public String uploadForm(Model model){
        model.addAttribute("video", new VideoDTO());
        model.addAttribute("categories", CATEGORIES);
        return "video/upload";
    }
    //삽입처리
    @PostMapping()

    //수정폼
    @GetMapping("/video/edit/{id}")
    public String editForm(Long id, Model model){
        try {
            model.addAttribute("video", videoService.getVoideForEdit(id));
            model.addAttribute("categories", CATEGORIES);
            return "video/edit";
        }catch (Exception e){
            return "redirect:/?error="+e.getMessage();
        }
    }



    //수정처리
    @PostMapping()

    //삭제(Model은 데이터를 페이지에 전달, RedirectAttributes는 데이터(값)을 다른 맵핑에 전달)
    @GetMapping("/video/delete/{id}")
    public String deleteVideo(@PathVariable Long id, RedirectAttributes re){
        try {
            videoService.deleteVideo(id);
            re.addFlashAttribute("successMsg", "동영상이 삭제되었습니다.");
        }catch (Exception e){
            re.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/"; //'/'목록요청
        //http://localhost:8080/?successMsg="내용"요청과 변수값을 전달
    }
}
