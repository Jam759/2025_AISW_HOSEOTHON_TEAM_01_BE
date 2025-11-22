package com._oormthonUNIV.newnew.ai.worker;

import com._oormthonUNIV.newnew.ai.entity.AiGenerationSurveyStatistics;
import com._oormthonUNIV.newnew.news.entity.News;
import com._oormthonUNIV.newnew.survey.entity.SurveyAnswer;
import com._oormthonUNIV.newnew.user.entity.enums.UserGeneration;
import lombok.Getter;

import java.util.List;

@Getter
public class PromptProvider {
    private final String statisticGeneration =
            """
            당신의 역할은 “세대별 뉴스 설문 분석기”입니다.
            
            아래 입력으로 제공되는 뉴스의 본문과 특정 세대의 설문 응답 데이터를 기반으로 \s
            해당 세대가 공통적으로 가지는 ‘주요 관점 TOP 3’와 \s
            각 관점의 이유를 하나의 문장으로 요약하여 도출하세요.
            
            ### 매우 중요한 규칙
            1. 절대 설명, 분석 과정, 불필요한 문장을 출력하지 마십시오.
            2. 반드시 **아래 JSON만** 출력하십시오. JSON 외의 텍스트가 포함되면 시스템 오류가 발생합니다.
            3. JSON 앞뒤로 코드블록, 마크다운, 해설, 접두사, 접미사를 절대 붙이지 마십시오.
            4. 값이 비어있더라도 key는 모두 포함해야 합니다.
            5. generation 값은 입력된 세대 그대로 출력해야 합니다.
    
            ### 절대 규칙
            - 출력은 반드시 JSON 객체만 포함해야 하며, 어떤 설명, 해설, 추가 문장도 포함해서는 안 됩니다.
            - JSON 앞뒤에 ```json, ``` 같은 코드블록 표시는 포함하지 않습니다.
            - Optional, Some(), Result<> 등 어떤 래핑 형태도 출력하지 않습니다.
            - 문자열 외의 타입(JSON 객체, 리스트 등)은 출력하지 않습니다.
            - JSON은 최상위에 하나만 존재해야 합니다.
            
            ### 출력 JSON 형식 예시
            {
              "generation": "GEN_Z",
              "firstAspect": "string",
              "firstAspectReason": "string",
              "secondAspect": "string",
              "secondAspectReason": "string",
              "thirdAspect": "string",
              "thirdAspectReason": "string"
            }
            
            ### 입력 데이터
            - generation: 분석할 세대
            - news : 뉴스 본문
            - answers : Q1 ~ Q5 설문 목록
            
            ### 제공 데이터
            
            """;

    private final String statisticAllGenerations =
            """
            당신의 역할은 “세대 간 공통 관점 분석기”입니다.
    
            아래 입력으로 제공되는 모든 세대별 설문 분석 결과를 기반으로
            “모든 세대가 공통적으로 동의하거나 공통적으로 문제의식을 가진 관점”을 한 개 도출하고,
            그 이유를 하나의 문장으로로 요약하세요.
    
            ### 분석 방식 규칙
            1. 각 세대의 top 3 관점들을 비교하여 의미적으로 가장 강한 중복을 찾습니다.
            2. 공통 관점은 중복도가 가장 높은 하나의 문장으로 구성합니다.
            3. 관점의 이유는 핵심 키워드를 콤마로 구분하여 간결하게 작성합니다.
            4. 결과는 반드시 아래 JSON 형식으로 출력합니다.
            5. JSON 구조는 AiNewsReport 엔티티에 저장 가능한 형태여야 합니다.
    
            ### 매우 중요한 규칙
            1. 절대 JSON 외의 다른 문장을 출력하지 마십시오.
            2. 마크다운, 코드블록, 설명을 출력하면 안 됩니다.
            3. key는 반드시 두 개 모두 포함해야 합니다.
            4. 이유(aspectReason)는 핵심 이유들을 콤마로만 구분하여 작성하십시오.
            
            ### 절대 규칙
            - 출력은 반드시 JSON 객체만 포함해야 하며, 어떤 설명, 해설, 추가 문장도 포함해서는 안 됩니다.
            - JSON 앞뒤에 ```json, ``` 같은 코드블록 표시는 포함하지 않습니다.
            - Optional, Some(), Result<> 등 어떤 래핑 형태도 출력하지 않습니다.
            - 문자열 외의 타입(JSON 객체, 리스트 등)은 출력하지 않습니다.
            - JSON은 최상위에 하나만 존재해야 합니다.
            
            ### AiNewsReport 구조(java)
            public AiNewsReport{
                private String commonAspect;
                private String aspectReason;
            }
    
            ### 출력 JSON 포맷
            {
              "commonAspect": "string",
              "aspectReason": "string"
            }
    
            ### 입력 데이터 설명
            - news : 뉴스의 본문
            - generation : 세대
                - aspect1 : 해당세대 관점 1번
                - aspect1Reason : 관점 1번 이유
                - aspect2 : 해당세대 관점 2번
                - aspect2Reason : 관점 2번 이유
                - aspect3 : 해당세대 관점 3번
                - aspect3Reason : 관점 3번 이유
            
            ### 데이터
            
            """;

    public String statisticAllGenerationAddData(
        News news,List<AiGenerationSurveyStatistics> aiGenerationSurveyStatisticList
    ){
        StringBuilder common = new StringBuilder();
        common.append("news : ").append(news.getContent()).append("\n");
        for (AiGenerationSurveyStatistics ai : aiGenerationSurveyStatisticList) {
            common.append("- generation : ").append(ai.getGeneration()).append("\n");
            common.append("\t").append("- aspect1 : ").append(ai.getFirstAspect()).append("\n");
            common.append("\t").append("- aspect1Reason : ").append(ai.getFirstAspectReason()).append("\n");
            common.append("\t").append("- aspect2 : ").append(ai.getSecondAspect()).append("\n");
            common.append("\t").append("- aspect2Reason : ").append(ai.getSecondAspectReason()).append("\n");
            common.append("\t").append("- aspect3 : ").append(ai.getThirdAspect()).append("\n");
            common.append("\t").append("- aspect3Reason : ").append(ai.getThirdAspectReason()).append("\n");
        }
        return statisticAllGenerations + common.toString();
    }

    public String statisticGenerationAddData(
            List<SurveyAnswer> answerList,
            UserGeneration generation,
            News news
    ) {
        StringBuilder common = new StringBuilder();

        common.append("- generation: ").append(generation).append("\n");
        common.append("- news: ").append(news.getContent()).append("\n");
        common.append("- answers:\n");

        for (SurveyAnswer answer : answerList) {
            short q = getQuestionNum(answer.getQuestion_id());
            common.append("  Q").append(q).append(": ").append(answer.getAnswer()).append("\n");
        }

        // AI 프롬프트와 설문 데이터를 연결
        return statisticGeneration + "\n" + common.toString();
    }


    private short getQuestionNum(long a) {
        if (a >= 1 && a <= 5) return (short) a;
        throw new IllegalArgumentException("Unknown question_id: " + a);
    }



}
