package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.entity.Article;
import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.repository.ArticleRepository;
import com.mfi_homework.mfi_homework.service.ArticleService;
import com.mfi_homework.mfi_homework.service.ArticleServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

    private final ArticleServiceHelper helper;
    private final ArticleRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveArticles(List<NewsItem> items) {
        var articles = items.stream()
                .map(item -> Article.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .news_site(item.getNewsSite())
                        .published_date(item.getPublishedAt())
                        .article(helper.loadArticleByUrl(item.getUrl()))
                        .build())
                .toList();
        repository.saveAll(articles);
    }

    @Override
    public List<Article> getAllArticles() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Article> getArticlesByNewsSite(String newsSite) {
        try {
            return repository.findByNews_site(newsSite);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getArticleBodyById(Long id) {
        try {
            return repository.findById(id)
                    .map(Article::getArticle)
                    .orElse("Article not found");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
