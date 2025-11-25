NewNew 백엔드 (Spring Boot)

프로젝트 개요
- AI 기반 뉴스 요약/설문/리포트와 인증을 제공하는 Spring Boot 3 애플리케이션입니다.
- 주요 도메인: 인증(Security, JWT), 뉴스(News), 설문(Survey), AI 통계/리포트(AI), 전역 공통(Global)
- API 문서: springdoc-openapi 기반 Swagger UI 제공(/swagger-ui/index.html)

기술 스택
- Java 21, Spring Boot 3.5.8
- Spring Data JPA, Spring Security, Validation
- DB: MySQL 8.x (Docker-Compose 제공)
- 문서: springdoc-openapi-starter-webmvc-ui
- JWT: jjwt 0.11.5
- HTML 파싱: jsoup 1.21.2
- OpenAI SDK: com.openai:openai-java:4.8.0
- 빌드: Gradle (gradlew)

패키지 구조와 역할
- com._oormthonUNIV.newnew.global
  - config.OpenApiConfig: OpenAPI 메타·보안 스키마(JWT Bearer) 정의
  - exception: 전역 예외 인터페이스/핸들러(GlobalBaseException, GlobalErrorCode, GlobalExceptionHandler, GlobalExceptionResponse)
  - util: 시간 유틸(TimeUtil), 공통 ID(LongIdEntity)
  - aop, cache, messageQueue 등 공통 보조 모듈 존재
- com._oormthonUNIV.newnew.security
  - config.SecurityConfig: JWT 인증 필터, 허용 경로 설정
    - 허용 경로: /api/v1/auth/**, /public/**, /swagger-ui/**, /swagger-ui.html, /v3/api-docs/**
    - ADMIN 전용: /admin/**
    - 그 외 모든 요청: 인증 필요
  - filter.JwtAuthenticationFilter: 요청의 JWT 파싱/검증/인증 컨텍스트 주입
  - controller.SecurityController: 로그인/로그아웃/회원가입/토큰 재발급 API
  - facade/service/repository: RefreshToken, 액세스토큰 블랙리스트 캐시 등 관리
  - exception: Security/RefreshToken/JWT 관련 도메인 예외 정의
- com._oormthonUNIV.newnew.news
  - entity.News: 뉴스(제목, 본문, 작성시각 news_created_at, 썸네일, 카테고리, 조회수)
  - repository.NewsRepository: 조회수 증가, 커서 기반 조회(id 기준), 인기 Top2 조회
  - service.NewsService/Impl: 페이징/커서 조회, 상세 조회(조회수 증가 포함), 랭킹 제공
  - facade.NewsFacade: 뉴스 + 설문 서비스 결합 응답 생성
  - controller.NewsController: 뉴스 목록/상세/인기/사용자 설문 참여 뉴스 목록 API
  - dto.response: 카드/리스트/상세 응답 DTO
  - factory.NewsFactory: 엔티티 → DTO 변환 및 시간 표현 변환(toLatestTime/toRelativeTime)
  - scheduler.NaverNewsScheduler/NaverNewsCrawler: 외부 뉴스 수집(크롤링 관련 클래스 존재)
  - exception: NewsErrorCode/NewsException
- com._oormthonUNIV.newnew.survey
  - controller.SurveyController: 설문 문항 조회, 답변 저장, 통계 조회 API
  - service/impl: 설문 저장/통계, 사용자 설문 참여 내역을 통한 뉴스 목록 조회
  - repository.SurveyAnswerRepository: 설문 답변 접근
  - dto(request/response), entity.SurveyAnswer, factory
  - exception: SurveyAnswerErrorCode/SurveyAnswerException
- com._oormthonUNIV.newnew.ai
  - service.impl.AiGenerationSurveyStatisticsServiceImpl, AiNewsReportServiceImpl: 통계/리포트 생성 서비스
  - repository: AiGenerationSurveyStatisticsRepository, AiNewsReportRepository
  - worker.ApiWorker, PromptProvider: 외부(OpenAI) 호출을 보조하는 워커/프롬프트 제공자
  - entity: AiGenerationSurveyStatistics, AiNewsReport

보안과 인증 흐름(JWT)
- 로그인(/api/v1/auth/login) 시 Access/Refresh 토큰을 발급
- 요청은 JwtAuthenticationFilter가 Authorization: Bearer <토큰>을 파싱/검증하여 SecurityContext에 사용자 주입
- 로그아웃(/api/v1/auth/logout): 액세스 토큰 블랙리스트 처리 + 리프레시 토큰 무효화
- 토큰 재발급(/api/v1/auth/access/reissue): 리프레시 토큰으로 새로운 액세스 토큰 발급
- SecurityConfig에서 인증 면제 경로 외 모든 API는 인증 필요(요구사항에 맞춰 SecurityController 제외 대부분 JWT 필요)

예외 처리 표준화
- 모든 도메인 예외는 GlobalBaseException을 상속하고 GlobalErrorCode(enum)를 통해
  - httpStatus, errorCode, message를 노출
- GlobalExceptionHandler가 GlobalBaseException을 받아 GlobalExceptionResponse로 응답
- 예: 뉴스 상세 조회 시 존재하지 않으면 NewsException(NewsErrorCode.NEWS_NOT_FOUND) → 404/메시지/코드 일관 반환

뉴스 조회 및 페이징 전략
- 목록 조회: 두 가지 방식을 지원
  1) 페이지네이션(page, size, 기본 정렬: id DESC)
  2) 커서 기반(nextNewsId 지정 시 해당 id 미만을 id DESC로 size만큼 조회)
- 상세 조회: 존재 여부 확인 후 조회수 증가 처리 후 응답
- 인기 뉴스: 조회수 기준 상위 2개 반환

주요 엔드포인트 요약
- 인증
  - POST /api/v1/auth/login: 로그인(토큰 반환)
  - POST /api/v1/auth/logout: 로그아웃(블랙리스트/리프레시 무효화) [JWT 필요]
  - POST /api/v1/auth/sign-up: 회원가입(토큰 반환)
  - POST /api/v1/auth/access/reissue: 액세스 토큰 재발급
- 뉴스 [JWT 필요]
  - GET /api/v1/News?page=&size=&nextNewsId=: 뉴스 목록(페이지/커서)
  - GET /api/v1/News/{newsId}: 뉴스 상세(설문 참여 여부에 따라 reportBlur 반영)
  - GET /api/v1/News/viewCount: 인기 뉴스 Top2
  - GET /api/v1/News/survey?page=&size=: 사용자가 설문에 참여한 뉴스 목록
- 설문 [JWT 필요]
  - GET /api/v1/survey: 설문 문항 조회
  - POST /api/v1/survey: 설문 답변 저장
  - GET /api/v1/survey/statistics/{newsId}: 설문 통계 조회

데이터 모델 개요(발췌)
- Users: 사용자(권한 UserRole, 세대 UserGeneration 등)
- RefreshToken: 사용자별 리프레시 토큰 저장
- News: title, content, author, category(Enum), news_created_at(LocalDateTime), viewCount, thumbnailUrl
- SurveyAnswer: 사용자-뉴스별 설문 응답
- AiGenerationSurveyStatistics: 설문 통계 집계 결과
- AiNewsReport: AI가 생성한 리포트 결과

메시지 큐/비동기 작업
- global.messageQueue.MessageQueueConfig, task.SurveyStatisticsTask가 존재하며 설문 통계 생성 작업을 분리/스케줄링하기 위한 구조를 포함합니다. 실제 메시지 브로커 종류에 대한 추가 설정은 소스에 포함된 범위 내에서만 사용됩니다.

환경 설정(application.yaml) 핵심
- DB 접속: jdbc:mysql://127.0.0.1:3306/newnew, username=root, password=root
  - JPA: ddl-auto=create(개발용), show-sql=true
- JWT
  - issuer, access-key, refresh-key, TTL(access 분 단위, refresh 일 단위)
- OpenAI API 키
  - spring 환경 속성: openai.api.key
  - application.yaml에는 ${OPENAI-API-KEY:}로도 참조하고 있어, 운영 환경에서는 다음 중 하나로 설정할 수 있습니다.
    1) 환경변수 OPENAI-API-KEY 설정
    2) 애플리케이션 프로퍼티 openai.api.key 설정
  - OpenApiProperties가 초기화 시키에 키를 로깅(System.out.println)하므로 실제 운영에서는 제거를 권장합니다.

로컬 실행 가이드
1) 의존성 설치 및 빌드
   - Windows: gradlew.bat build
   - macOS/Linux: ./gradlew build
2) DB 실행
   - Docker Desktop 실행 후, 프로젝트 루트의 Docker-Compose.yaml 사용
   - 명령: docker compose up -d
   - 기본 MySQL 접속: 127.0.0.1:3306, DB=newnew, root/root
3) 애플리케이션 실행
   - Windows: gradlew.bat bootRun 또는 빌드 후 java -jar build/libs/*.jar
   - 기본 포트: 8080
4) API 문서 접속
   - http://localhost:8080/swagger-ui/index.html
   - JWT가 필요한 엔드포인트는 Authorize 버튼으로 Bearer <액세스토큰> 입력

개발 시 유의 사항
- Security 정책: SecurityController의 인증 API를 제외한 대부분의 엔드포인트는 JWT가 필요합니다.
- 뉴스 정렬: 구현상 기본 정렬은 id DESC이며, 엔티티의 생성시각 필드는 news_created_at입니다.
- 예외 응답: 도메인 예외는 GlobalExceptionResponse 포맷으로 반환되며, 클라이언트는 errorCode/httpStatus/message를 기준으로 처리할 수 있습니다.
- 데이터 시드: src/main/resources/data.sql이 존재하면 ddl-auto=create 시 초기 데이터가 반영될 수 있습니다.

라이선스/저작권
- 본 저장소의 라이선스 정보는 명시되어 있지 않습니다. 외부 배포 시 별도 라이선스 명시를 권장합니다.
