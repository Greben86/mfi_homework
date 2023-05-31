package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.NewsItem;

import java.util.List;

public interface ArticleService {

    void saveArticles(List<NewsItem> items);

    List<String> getAllArticles();

    List<String> getArticlesByNewsSite(String newsSite);
}
