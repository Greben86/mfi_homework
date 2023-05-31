package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.entity.NewsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class ReadNewsTask implements Runnable {
    private static final String URI_PARAMS_TEMPLATE = "?_limit={articles_limit}&_start={skipped_articles}";
    private static final long SLEEP_DURATION = 10_000L;

    private final String url;
    private final int limit;
    private final int max_count;
    private final String[] black_list;
    private final NewsBufferService buffer;

    private static final AtomicInteger skipCount = new AtomicInteger(0);

    @Override
    public void run() {
        var uriTemplate = new UriTemplate(url + URI_PARAMS_TEMPLATE);
        boolean exitFlag = true;
        while (exitFlag) {
            try {
                int skip = skipCount.getAndUpdate(val -> Math.min(max_count, val+limit));
                processGetNews(uriTemplate, skip);
                if (skip == max_count) {
                    exitFlag = false;
                }

                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                exitFlag = false;
            }
        }
    }

    private void processGetNews(UriTemplate uriTemplate, int skip) {
        try {
            var response = WebClient.create()
                    .get()
                    .uri(uriTemplate.expand(limit, skip))
                    .retrieve()
                    .bodyToMono(NewsResponse.class)
                    .blockOptional();

            if (response.isPresent()) {
                var newsMap = response.get().stream()
                        .filter(this::checkBlackList)
                        .sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed())
                        .collect(Collectors.groupingBy(NewsItem::getNewsSite, Collectors.toList()));

                buffer.putAndSave(newsMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean checkBlackList(NewsItem item) {
        return Stream.of(black_list)
                .noneMatch(item.getTitle()::contains);
    }
}
