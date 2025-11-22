package com._oormthonUNIV.newnew.ai.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerationAspectDTO {
    private String generation;
    private String firstAspect;
    private String firstAspectReason;
    private String secondAspect;
    private String secondAspectReason;
    private String thirdAspect;
    private String thirdAspectReason;
}

