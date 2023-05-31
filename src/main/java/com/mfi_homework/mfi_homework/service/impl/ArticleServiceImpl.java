package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.entity.Article;
import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.repository.ArticleRepository;
import com.mfi_homework.mfi_homework.service.ArticleService;
import com.mfi_homework.mfi_homework.service.ArticleServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

    private final ArticleServiceHelper helper;
    private final ArticleRepository repository;

    @Override
    @Transactional
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
    public List<String> getAllArticles() {
        return repository.findAll().stream()
                .map(Article::getArticle)
                .toList();
    }

    @Override
    public List<String> getArticlesByNewsSite(String newsSite) {
        return repository.findByNews_site(newsSite).stream()
                .map(Article::getArticle)
                .toList();
    }
}
