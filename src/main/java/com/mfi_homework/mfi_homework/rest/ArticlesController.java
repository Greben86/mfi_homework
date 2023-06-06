package com.mfi_homework.mfi_homework.rest;

import com.mfi_homework.mfi_homework.entity.Article;
import com.mfi_homework.mfi_homework.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class ArticlesController {

    private final ArticleService articleService;

    @GetMapping(value = "/articles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> getAllArticles() {
        log.info("Calling \"api/articles\"");
        return articleService.getAllArticles();
    }

    @GetMapping(value = "/site/{site}/articles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> getArticlesByNewsSite(@PathVariable("site") String site) {
        log.info("Calling \"api/site/{}/articles\"", site);

        return articleService.getArticlesByNewsSite(site);
    }

    @GetMapping(value = "/articles/{id}", produces = MediaType.ALL_VALUE)
    public String getArticleBodyById(@PathVariable("id") Long id) {
        log.info("Calling \"api/articles/{}\"", id);
        return articleService.getArticleBodyById(id);
    }
}
