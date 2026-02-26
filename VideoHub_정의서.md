# 📺 VideoHub 프로젝트 정의서

---

## 📘 동영상 테이블 (VideoEntity)

| 필드명          | 타입            | 설명                                                              |
| ------------ | ------------- | --------------------------------------------------------------- |
| `id`         | Long          | 동영상 고유 ID (PK, 자동 생성)                                          |
| `title`      | String        | 동영상 제목 (최대 200자, 필수)                                           |
| `description`| String        | 동영상 설명 (TEXT 타입, 선택 입력)                                        |
| `uploader`   | String        | 업로더 채널명 (최대 100자, 필수)                                          |
| `category`   | String        | 카테고리 (최대 50자, 예: 교육·음악·게임 등, 선택)                              |
| `tags`       | String        | 태그 목록 (최대 200자, 쉼표 구분, 선택)                                     |
| `viewCount`  | Long          | 조회수 (기본값 0, 필수)                                                |
| `likeCount`  | Long          | 좋아요 수 (기본값 0, 필수)                                              |
| `thumbnailUrl`| String       | 썸네일 이미지 URL 또는 업로드 파일 경로 (최대 500자, 선택)                        |
| `videoUrl`   | String        | 동영상 URL 또는 업로드 파일 경로 (최대 500자, 선택)                            |
| `status`     | String        | 공개 상태 (PUBLIC: 공개 / PRIVATE: 비공개 / UNLISTED: 일부공개, 기본값 PUBLIC, 최대 20자) |
| `createdAt`  | LocalDateTime | 업로드 일시 (자동 생성, 수정 불가)                                          |
| `updatedAt`  | LocalDateTime | 수정 일시 (자동 갱신)                                                  |

---

## 📘 동영상 DTO (VideoDTO)

| 필드명            | 타입            | 설명                                                 |
| -------------- | ------------- | -------------------------------------------------- |
| `id`           | Long          | 동영상 고유 ID                                          |
| `title`        | String        | 동영상 제목                                             |
| `description`  | String        | 동영상 설명                                             |
| `uploader`     | String        | 업로더 채널명                                            |
| `category`     | String        | 카테고리                                               |
| `tags`         | String        | 태그 (쉼표 구분)                                         |
| `viewCount`    | Long          | 조회수                                                |
| `likeCount`    | Long          | 좋아요 수                                              |
| `thumbnailUrl` | String        | 썸네일 URL                                            |
| `videoUrl`     | String        | 동영상 URL                                            |
| `status`       | String        | 공개 상태                                              |
| `createdAt`    | LocalDateTime | 업로드 일시                                             |
| `updatedAt`    | LocalDateTime | 수정 일시                                              |
| `thumbnailFile`| MultipartFile | 썸네일 파일 업로드용 (폼 바인딩 전용, ModelMapper 변환 대상 제외)       |
| `videoFile`    | MultipartFile | 동영상 파일 업로드용 (폼 바인딩 전용, ModelMapper 변환 대상 제외)       |

**편의 메서드**

| 메서드명                    | 반환형    | 설명                                               |
| ----------------------- | ------ | ------------------------------------------------ |
| `getFormattedViewCount()` | String | 조회수 포맷 변환 (1,000 → 1.0K / 1,000,000 → 1.0M)      |
| `getTimeAgo()`          | String | 업로드 후 경과 시간 표시 (오늘 / N일 전 / N주 전 / N개월 전 / N년 전) |

---

## 📘 홈 / 관리 API 정의서 (VideoController — GET)

| 기능           | Method | 경로        | 설명                       | 요청 파라미터                          | 반환 뷰 / 모델 속성                                                                        |
| ------------ | ------ | --------- | ------------------------ | --------------------------------- | ------------------------------------------------------------------------------------- |
| **홈 목록**     | GET    | `/`       | 공개 동영상 목록 메인 페이지         | `keyword`, `category`, `sort` (선택) | `index.html` / `videos`, `keyword`, `selectedCategory`, `sort`, `categories`           |
| **동영상 재생**   | GET    | `/video/{id}` | 동영상 상세 재생 페이지 (조회수 +1)  | `id` (PathVariable)               | `video/watch.html` / `video`, `related`(최대 8개), `categories`                         |
| **업로드 폼**    | GET    | `/video/upload` | 동영상 업로드 입력 폼            | 없음                                | `video/upload.html` / 빈 `VideoDTO`, `categories`                                     |
| **수정 폼**     | GET    | `/video/edit/{id}` | 동영상 수정 입력 폼          | `id` (PathVariable)               | `video/edit.html` / 해당 `VideoDTO`, `categories`                                      |
| **동영상 삭제**   | GET    | `/video/delete/{id}` | 동영상 및 파일 삭제 후 홈 리다이렉트 | `id` (PathVariable)               | `redirect:/` (성공 flashMsg / 실패 errorMsg)                                             |
| **관리 페이지**   | GET    | `/manage` | 전체 동영상 관리 목록 페이지        | 없음                                | `video/manage.html` / `videos`, `publicCount`, `totalViews`, `totalLikes`, `categories` |

---

## 📘 동영상 처리 API 정의서 (VideoController — POST)

| 기능          | Method | 경로                    | 설명                      | 요청 바디                   | 처리 결과                                             |
| ----------- | ------ | --------------------- | ----------------------- | ----------------------- | ------------------------------------------------- |
| **업로드 처리**  | POST   | `/video/upload`       | 동영상 정보 저장 후 재생 페이지 이동  | `VideoDTO` (multipart)  | `redirect:/video/{id}` (성공) / `redirect:/video/upload` (실패) |
| **수정 처리**   | POST   | `/video/edit/{id}`    | 동영상 정보 수정 후 재생 페이지 이동  | `VideoDTO` (multipart)  | `redirect:/video/{id}` (성공) / `redirect:/video/edit/{id}` (실패) |
| **좋아요 처리**  | POST   | `/video/like/{id}`    | 좋아요 수 1 증가 후 재생 페이지 이동 | 없음                      | `redirect:/video/{id}`                            |

---

## 📘 동영상 로직 정의서 (VideoService) — ModelMapper 적용

| 함수 이름              | 매개변수                       | 반환형              | 설명                            | ModelMapper 적용 방식                                                                                  |
| ------------------ | -------------------------- | ---------------- | ----------------------------- | --------------------------------------------------------------------------------------------------- |
| `createVideo`      | `VideoDTO dto`             | `VideoDTO`       | 신규 동영상 등록 후 DTO 반환            | 빌더로 Entity 직접 생성 (파일 경로·기본값 수동 세팅 필요); 저장 후 `toDTO()`로 반환                                         |
| `getPublicVideos`  | 없음                         | `List<VideoDTO>` | PUBLIC 상태 동영상 목록 (최신순) 조회     | `toDTO()` 스트림 적용                                                                                  |
| `getAllVideos`      | 없음                         | `List<VideoDTO>` | 전체 동영상 목록 (관리용, 최신순) 조회      | `toDTO()` 스트림 적용                                                                                  |
| `getPopularVideos` | 없음                         | `List<VideoDTO>` | PUBLIC 동영상 조회수 내림차순 목록 조회    | `toDTO()` 스트림 적용                                                                                  |
| `searchVideos`     | `String keyword`           | `List<VideoDTO>` | 제목·설명·태그 키워드 검색 (PUBLIC만)     | `toDTO()` 스트림 적용; keyword 공백 시 `getPublicVideos()` 위임                                            |
| `getVideosByCategory` | `String category`       | `List<VideoDTO>` | 카테고리별 PUBLIC 동영상 목록 (최신순) 조회 | `toDTO()` 스트림 적용                                                                                  |
| `getVideo`         | `Long id`                  | `VideoDTO`       | 단건 조회 (조회수 +1 포함)             | `toDTO()` 단건 적용; `videoRepository.incrementViewCount(id)` 호출                                     |
| `getVideoForEdit`  | `Long id`                  | `VideoDTO`       | 단건 조회 (조회수 증가 없음, 수정 폼용)     | `toDTO()` 단건 적용 (readOnly 트랜잭션)                                                                  |
| `updateVideo`      | `Long id, VideoDTO dto`    | `VideoDTO`       | 동영상 정보 수정 후 DTO 반환            | 보호 필드(`id`, `viewCount`, `likeCount`, `createdAt`) 유지를 위해 필드별 직접 수정; 파일 교체 시 기존 파일 삭제 후 재저장; `toDTO()`로 반환 |
| `deleteVideo`      | `Long id`                  | `void`           | 동영상 및 연관 파일 삭제               | ModelMapper 미사용; 파일 삭제 후 `deleteById()` 호출                                                      |
| `likeVideo`        | `Long id`                  | `VideoDTO`       | 좋아요 수 +1 후 최신 DTO 반환          | `videoRepository.incrementLikeCount(id)` 후 재조회; `toDTO()` 단건 적용                                 |
| `toDTO` (private)  | `VideoEntity entity`       | `VideoDTO`       | Entity → DTO 자동 변환             | `modelMapper.map(entity, VideoDTO.class)` 단일 호출; `MultipartFile` 타입 필드는 타입 불일치로 자동 제외            |

---

## 📘 동영상 쿼리 정의서 (VideoRepository)

| 메서드 이름                                          | JPQL / 동작 설명                                         | 입력 파라미터                   | 반환 타입                  | 특징 / 비고                           |
| ------------------------------------------------ | ------------------------------------------------------ | ------------------------- | ---------------------- | ---------------------------------- |
| `findByStatusOrderByCreatedAtDesc`               | 특정 상태의 동영상 목록을 업로드 일시 최신순으로 조회                        | `String status`           | `List<VideoEntity>`    | PUBLIC 목록 기본 조회에 사용               |
| `findAllByOrderByCreatedAtDesc`                  | 전체 동영상 목록을 업로드 일시 최신순으로 조회                            | 없음                        | `List<VideoEntity>`    | 관리 페이지 전체 목록에 사용                  |
| `searchByKeyword`                                | PUBLIC 동영상 중 제목·설명·태그에 키워드가 포함된 목록 최신순 조회 (JPQL)     | `String keyword`          | `List<VideoEntity>`    | LIKE 검색, 세 필드 OR 조건 결합            |
| `findByStatusAndCategoryOrderByCreatedAtDesc`    | 특정 상태 + 카테고리 조건의 동영상 목록 최신순 조회                        | `String status, String category` | `List<VideoEntity>` | AND 조건 결합, 카테고리 필터에 사용          |
| `findByUploaderOrderByCreatedAtDesc`             | 특정 업로더(채널명)의 동영상 목록 최신순 조회                            | `String uploader`         | `List<VideoEntity>`    | 채널별 영상 목록 조회용                     |
| `findByStatusOrderByViewCountDesc`               | 특정 상태의 동영상 목록을 조회수 내림차순으로 조회                          | `String status`           | `List<VideoEntity>`    | 인기 동영상 목록에 사용                     |
| `incrementViewCount`                             | 특정 동영상의 조회수를 1 증가 (JPQL UPDATE, `@Modifying`)         | `Long id`                 | `void`                 | 트랜잭션 내 실행; 낙관적 락 없이 단순 카운터 증가     |
| `incrementLikeCount`                             | 특정 동영상의 좋아요 수를 1 증가 (JPQL UPDATE, `@Modifying`)       | `Long id`                 | `void`                 | 트랜잭션 내 실행; 단순 카운터 증가              |

---

## 📘 파일 저장 로직 정의서 (FileStorageService)

| 함수 이름    | 매개변수                              | 반환형    | 설명                            | 처리 방식                                                                              |
| -------- | --------------------------------- | ------ | ----------------------------- | ---------------------------------------------------------------------------------- |
| `store`  | `MultipartFile file, String subDir` | String | 파일을 지정 서브 디렉토리에 저장 후 웹 접근 경로 반환 | UUID 기반 랜덤 파일명 생성; `uploads/{subDir}/` 경로에 저장; 반환값: `/uploads/{subDir}/{uuid}.{ext}` |
| `delete` | `String webPath`                  | `void` | 웹 경로 기준으로 실제 파일 삭제            | `/uploads/...` → 상대 경로로 변환 후 `Files.deleteIfExists()` 호출; 예외는 무시(ignore)          |

**설정값**

| 프로퍼티                | 기본값       | 설명                  |
| ------------------- | --------- | ------------------- |
| `app.upload.dir`    | `uploads` | 파일 저장 루트 디렉토리       |

---

## 📘 설정 정의서 (Config)

### AppConfig — ModelMapper 빈 등록

| 빈 이름          | 타입           | 설정                                                                           |
| ------------- | ------------ | ---------------------------------------------------------------------------- |
| `modelMapper` | `ModelMapper` | `MatchingStrategy.STRICT` (필드명 완전 일치 시에만 매핑) / `skipNullEnabled = true` (null 필드 건너뜀) |

### WebConfig — 정적 파일 리소스 핸들러

| URL 패턴        | 실제 경로                              | 설명                         |
| ------------- | ---------------------------------- | -------------------------- |
| `/uploads/**` | `{app.upload.dir}` 의 절대 경로 | 업로드된 동영상·썸네일 파일을 웹에서 접근 가능하게 매핑 |

---

## 📘 화면 정의서 (Thymeleaf Templates)

| 템플릿 경로                        | 설명          | 주요 모델 속성                                                  |
| ------------------------------ | ----------- | --------------------------------------------------------- |
| `fragments/layout.html`        | 공통 레이아웃 (네비바·사이드바·스크립트 프래그먼트) | `categories`, `keyword`           |
| `index.html`                   | 홈 — 동영상 그리드 목록 | `videos`, `keyword`, `selectedCategory`, `sort`, `categories` |
| `video/watch.html`             | 동영상 재생 페이지  | `video`, `related`, `categories`                          |
| `video/upload.html`            | 동영상 업로드 폼   | `video`(빈 DTO), `categories`                              |
| `video/edit.html`              | 동영상 수정 폼    | `video`, `categories`                                     |
| `video/manage.html`            | 동영상 관리 목록   | `videos`, `publicCount`, `totalViews`, `totalLikes`, `categories` |

**공통 레이아웃 프래그먼트**

| 프래그먼트명      | 설명                                 |
| ----------- | ---------------------------------- |
| `head(title)` | `<head>` 공통 영역 (Bootstrap 5 CDN 포함) |
| `navbar`    | 상단 검색 네비게이션 바                      |
| `sidebar(active)` | 좌측 메뉴 사이드바 (활성 항목 하이라이트)       |
| `scripts`   | Bootstrap 5 JS + 토스트 자동 표시 스크립트    |

---

## 📘 오류 수정 이력

| 일자         | 대상 파일                       | 오류 내용                                                                              | 수정 방법                                                                                               |
| ---------- | --------------------------- | ---------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- |
| 2026-02-24 | `video/manage.html`         | SpEL이 Java 람다식(`->`) 미지원으로 `videos.stream().filter(v -> v.status == 'PUBLIC').count()` 등 3개 표현식에서 500 오류 발생 | `VideoController.manage()`에서 `publicCount`, `totalViews`, `totalLikes`를 Java 스트림으로 미리 계산해 모델에 추가; 템플릿에서는 단순 변수(`${publicCount}` 등)로 교체 |
