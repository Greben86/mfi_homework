package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.entity.NewsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class ReadNewsTask implements Runnable {
    private static final String URI_PARAMS_TEMPLATE = "?_limit={articles_limit}&_start={skipped_articles}";

    private final String url;
    private final int limit;
    private final int max_count;
    private final int duration_sleep;
    private final String[] black_list;
    private final WebClient webClient;
    private final NewsBufferService buffer;

    private static final AtomicInteger skipCount = new AtomicInteger(0);
    private static final AtomicInteger readCount = new AtomicInteger(0);

    @Override
    public void run() {
        var uriTemplate = new UriTemplate(url + URI_PARAMS_TEMPLATE);
        while (readCount.get() < max_count) {
            try {
                int skippedArticles = skipCount.getAndAdd(limit);
                int articlesLimit = Math.min(limit, max_count - readCount.get());
                int readArticles = processGettingNews(uriTemplate, articlesLimit, skippedArticles);
                readCount.addAndGet(readArticles);

                TimeUnit.SECONDS.sleep(duration_sleep);
            } catch (InterruptedException e) {
                log.warn("Await interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        buffer.saveAllAndClear();
        log.info("All news already read");
    }

    private int processGettingNews(UriTemplate uriTemplate, int articlesLimit, int skippedArticles) {
        log.info("Request: ?_limit={}&_start={}", articlesLimit, skippedArticles);
        var response = webClient
                .get()
                .uri(uriTemplate.expand(articlesLimit, skippedArticles))
                .retrieve()
                .bodyToMono(NewsResponse.class)
                .blockOptional();

        if (response.isPresent()) {
            var newsMap = response.get().stream()
                    .filter(this::checkBlackList)
                    .sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed())
                    .collect(Collectors.groupingBy(NewsItem::getNewsSite, Collectors.toList()));

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
