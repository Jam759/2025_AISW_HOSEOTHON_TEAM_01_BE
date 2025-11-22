package com._oormthonUNIV.newnew.news.repository;

import com._oormthonUNIV.newnew.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
