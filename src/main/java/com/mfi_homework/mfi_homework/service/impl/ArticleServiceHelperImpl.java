package com.mfi_homework.mfi_homework.service.impl;

import com.mfi_homework.mfi_homework.service.ArticleServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ArticleServiceHelperImpl implements ArticleServiceHelper {

    @Override
    public String loadArticleByUrl(String url) {
        log.info(url);
        var response = WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional();
        return response.orElse("");
    }
}
