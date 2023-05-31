package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.service.ArticleService;
import com.mfi_homework.mfi_homework.service.NewsBufferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class NewsBufferServiceImpl implements NewsBufferService {

    private final Map<String, List<NewsItem>> bufferMap = new HashMap<>();

    @Value("${news.buffer.max_size}")
    private int maxSize;

    private final ArticleService articleService;

    @Override
    public synchronized void putAndSave(Map<String, List<NewsItem>> map) {
        putAll(map);
        if (bufferMap.size() >= maxSize) {
            saveAndClear();
        }
    }

    @Override
    public synchronized void saveAndClear() {
        if (bufferMap.isEmpty()) {
            return;
        }

        var newsList = bufferMap.values().stream()
                .flatMap(List::stream)
                .toList();
        articleService.saveArticles(newsList);
        bufferMap.clear();
    }

    private void putAll(Map<String, List<NewsItem>> map) {
        map.forEach((key, value) -> {
            if (bufferMap.containsKey(key)) {
                bufferMap.get(key).addAll(value);
            } else {
                bufferMap.put(key, value);
            }
        });
    }
}
