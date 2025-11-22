package com._oormthonUNIV.newnew.news.repository;

import com._oormthonUNIV.newnew.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsCrawlerRepository extends JpaRepository<News,Long> {

    @Override
    <S extends News> List<S> saveAll(Iterable<S> entities);

    @Override
    <S extends News> S save(S entity);

}
