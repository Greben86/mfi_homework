package com.mfi_homework.mfi_homework.service;

import com.mfi_homework.mfi_homework.entity.NewsItem;
import com.mfi_homework.mfi_homework.entity.NewsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.Comparator;
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

    @Override
    public void run() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        var uriTemplate = new UriTemplate(url + URI_PARAMS_TEMPLATE);
        while (skipCount.get() < max_count) {
            try {
                int skip = skipCount.getAndUpdate(val -> Math.min(max_count, val+limit));
                processGettingNews(uriTemplate, skip);

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

    private void processGettingNews(UriTemplate uriTemplate, int skip) {
        var response = webClient
                .get()
                .uri(uriTemplate.expand(limit, skip))
                .retrieve()
                .bodyToMono(NewsResponse.class)
                .blockOptional();

        if (response.isPresent()) {
            var newsMap = response.get().stream()
                    .filter(this::checkBlackList)
                    .sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed())
                    .peek(item -> log.info(item.toString()))
                    .collect(Collectors.groupingBy(NewsItem::getNewsSite, Collectors.toList()));

            buffer.putAllAndCheck(newsMap);
        }
    }

    private boolean checkBlackList(NewsItem item) {
        return Stream.of(black_list)
                .filter(word -> !word.isBlank())
                .noneMatch(item.getTitle()::contains);
    }
}
