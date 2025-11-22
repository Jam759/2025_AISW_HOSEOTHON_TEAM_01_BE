package com._oormthonUNIV.newnew.news.entity;

import com._oormthonUNIV.newnew.global.util.LongIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends LongIdEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String author;

    @Column(nullable = false)
    private LocalDateTime news_created_at;

}
