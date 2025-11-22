package com._oormthonUNIV.newnew.survey.exception;

import com._oormthonUNIV.newnew.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SurveyAnswerErrorCode  implements GlobalErrorCode {

    SURVEY_ANSWER_DUPLICATED(HttpStatus.BAD_REQUEST,2002,"같은 뉴스에 다시 설문을 할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
