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

    @Value("${news.buffer.max_count_for_site}")
    private int maxCountForSite;

    private final ArticleService articleService;

    @Override
    public synchronized void putAllAndCheck(Map<String, List<NewsItem>> map) {
        putAll(map);
        saveFullSitesAndRemove();
    }

    private void saveFullSitesAndRemove() {
        var fullSites = bufferMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= maxCountForSite)
                .map(Map.Entry::getKey)
                .toList();
        if (fullSites.size() > 0) {
            var newsList = bufferMap.entrySet().stream()
                    .filter(entry -> fullSites.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .flatMap(List::stream)
                    .toList();
            articleService.saveArticles(newsList);
            fullSites.forEach(bufferMap::remove);
        }
    }

    @Override
    public synchronized void saveAllAndClear() {
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
