package com._oormthonUNIV.newnew.news.service.impl;

import com._oormthonUNIV.newnew.ai.repository.AiNewsReportRepository;
import com._oormthonUNIV.newnew.news.DTO.response.NewsListResponseDto;
import com._oormthonUNIV.newnew.news.entity.News;
import com._oormthonUNIV.newnew.news.repository.NewsCrawlerRepository;
import com._oormthonUNIV.newnew.news.service.NewsService;
import com._oormthonUNIV.newnew.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class NewsServiceImpl implements NewsService {

    private final NewsCrawlerRepository newsCrawlerRepository;
    private final AiNewsReportRepository aiNewsReportRepository;

    @Override
    public News getById(Long newsId) {
        return newsCrawlerRepository.findById(newsId)
                .orElseThrow( () -> new IllegalArgumentException("Newsasdasd"));
    }

    @Override
    public List<News> getUserSurveiedNews(Users user) {
        // TODO: 유저가 설문한 뉴스만 조회하고 싶으면 여기에 구현
        // 지금은 일단 전체 목록 반환 혹은 비워두고 나중에 구현
        return List.of();
    }

    @Override
    public NewsListResponseDto getNewsList() {
        List<News> news = newsCrawlerRepository.findAll();
        return NewsListResponseDto.of(news);
    }

    @Override
    public boolean isReportBlur(Long userId) {

        return false;   // false면 블러 해제 (reportBlur = false)
    }

    @Transactional
    public News getNewsDetail(Long newsId) {
        // 조회수 먼저 증가
        newsCrawlerRepository.increaseViewCount(newsId);

        // 그리고 엔티티 조회
        return newsCrawlerRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 뉴스입니다. id=" + newsId));
    }

    @Override
    public List<News> getRanking() {
        return newsCrawlerRepository.findTop2ByOrderByViewCountDesc();
    }
}
