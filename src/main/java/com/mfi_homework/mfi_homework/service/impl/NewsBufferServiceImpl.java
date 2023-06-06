package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.service.ArticleService;
import com.mfi_homework.mfi_homework.service.NewsBufferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class NewsBufferServiceImpl implements NewsBufferService {

    private final Map<String, List<NewsItem>> bufferMap = new ConcurrentHashMap<>();

    @Value("${news.buffer.max_count_for_site}")
    private int maxCountForSite;

    private final ArticleService articleService;

    @Override
    public void putAllAndCheck(Map<String, List<NewsItem>> map) {
        putAll(map);
        var newsList = getFullSitesAndRemove();
        if (!newsList.isEmpty()) {
            articleService.saveArticles(newsList);
        }
    }

    private synchronized List<NewsItem> getFullSitesAndRemove() {
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
            fullSites.forEach(bufferMap::remove);
            return newsList;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void saveAllAndClear() {
        var newsList = getAllNewsAndClear();
        if (!newsList.isEmpty()) {
            articleService.saveArticles(newsList);
        }
    }

    private synchronized List<NewsItem> getAllNewsAndClear() {
        var newsList = bufferMap.values().stream()
                .flatMap(List::stream)
                .toList();
        bufferMap.clear();

        return newsList;
    }

    private synchronized void putAll(Map<String, List<NewsItem>> map) {
        map.entrySet().stream()
                .distinct()
                .forEach(entry ->
                    bufferMap.merge(entry.getKey(), entry.getValue(), (left, right) -> {
                        left.addAll(right);
                        return left;
                    })
                );
    }
}
