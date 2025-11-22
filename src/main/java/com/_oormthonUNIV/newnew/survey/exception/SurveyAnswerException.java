package com._oormthonUNIV.newnew.survey.exception;

import com._oormthonUNIV.newnew.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyAnswerException extends GlobalBaseException {
    private SurveyAnswerErrorCode errorCode;
}
