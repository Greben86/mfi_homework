package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.service.ArticleServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class ArticleServiceHelperImpl implements ArticleServiceHelper {

    private final WebClient webClient;

    @Override
    public String loadArticleByUrl(String url) {
        log.info("Download article body from {}", url);
        var response = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional();
        return response.orElse("");
    }
}
