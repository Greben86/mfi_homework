package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.Article;
import com.mfi_homework.mfi_homework.entity.NewsItem;

import java.util.List;

public interface ArticleService {

    void saveArticles(List<NewsItem> items);

    List<Article> getAllArticles();

    List<Article> getArticlesByNewsSite(String newsSite);

    String getArticleBodyById(Long id);
}
