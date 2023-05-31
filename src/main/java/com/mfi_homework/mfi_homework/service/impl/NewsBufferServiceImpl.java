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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class NewsBufferServiceImpl implements NewsBufferService {

    private final ReentrantLock locker = new ReentrantLock();
    private final Map<String, List<NewsItem>> bufferMap = new HashMap<>();

    @Value("${buffer.max_size}")
    private int maxSize;

    private final ArticleService articleService;

    @Override
    public void putAndSave(Map<String, List<NewsItem>> map) {
        locker.lock();
        try {
            putAll(map);
            checkSizeAndSave();
        } finally {
            locker.unlock();
        }
    }

    private void checkSizeAndSave() {
        if (bufferMap.size() >= maxSize) {
            var newsList = bufferMap.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            articleService.saveArticles(newsList);
            bufferMap.clear();
        }
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
