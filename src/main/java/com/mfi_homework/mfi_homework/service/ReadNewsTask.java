package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.entity.NewsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class ReadNewsTask implements Runnable {
    private static final String URI_SCHEME = "https";
    private static final String URI_HOST = "api.spaceflightnewsapi.net";
    private static final String URI_PATH = "/v3/articles";
    private static final String URI_QUERY_PARAMS = "_limit={articles_limit}&_start={skipped_articles}";

    private final int limit;
    private final int max_count;
    private final String[] black_list;
    private final WebClient webClient;
    private final NewsBufferService buffer;

    private static final AtomicInteger skipCount = new AtomicInteger(0);
    private static final AtomicInteger readCount = new AtomicInteger(0);

    @Override
    public void run() {
        var uriBuilder = UriComponentsBuilder.newInstance()
                .scheme(URI_SCHEME).host(URI_HOST).path(URI_PATH).query(URI_QUERY_PARAMS);
        while (readCount.get() < max_count) {
            try {
                int skippedArticles = skipCount.getAndAdd(limit);
                int articlesLimit = Math.min(limit, max_count - readCount.get());
                int readArticles = processGettingNews(uriBuilder, articlesLimit, skippedArticles);
                readCount.addAndGet(readArticles);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        buffer.saveAllAndClear();
        log.info("All news already read");
    }

    private int processGettingNews(UriComponentsBuilder uriBuilder, int articlesLimit, int skippedArticles) {
        log.info("Request: ?_limit={}&_start={}", articlesLimit, skippedArticles);
        var response = webClient
                .get()
                .uri(uriBuilder.build(Map.of("articles_limit", articlesLimit, "skipped_articles", skippedArticles)))
                .retrieve()
                .bodyToMono(NewsResponse.class)
                .blockOptional();

        if (response.isPresent()) {
            var newsMap = response.get().stream()
                    .filter(this::checkBlackList)
                    .sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed())
                    .collect(Collectors.groupingBy(NewsItem::getNewsSite));

            buffer.putAllAndCheck(newsMap);

            return newsMap.values().stream()
                    .mapToInt(List::size)
                    .sum();
        } else {
            return 0;
        }
    }

    private boolean checkBlackList(NewsItem item) {
        return Stream.of(black_list)
                .filter(word -> !word.isBlank())
                .noneMatch(item.getTitle()::contains);
    }
}
