package com._oormthonUNIV.newnew.global.entity;

import com._oormthonUNIV.newnew.global.util.TimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeRecordEntity {

    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = TimeUtil.getNowSeoulLocalDateTime();
        onPrePersist();
    }

    //여기서 재정의시 prepersist에서 적용됨
    protected void onPrePersist() {}

}

