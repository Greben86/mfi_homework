package com.mfi_homework.mfi_homework.rest;

import com.mfi_homework.mfi_homework.entity.Article;
import com.mfi_homework.mfi_homework.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/articles")
public class ArticlesController {

    private final ArticleService articleService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> getAllArticles() {
        log.info("Calling \"api/articles/all\"");
        return articleService.getAllArticles();
    }

    @GetMapping(value = "/site", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> getArticlesByNewsSite(@RequestParam(value = "news_site", required = false) String newsSite) {
        log.info("Calling \"api/articles/site?news_site={}\"", newsSite);
        if (newsSite == null || newsSite.isBlank()) {
            return articleService.getAllArticles();
        }

        return articleService.getArticlesByNewsSite(newsSite);
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.ALL_VALUE)
    public String getArticleBodyById(@PathVariable("id") Long id) {
        log.info("Calling \"api/articles/id/{}\"", id);
        return articleService.getArticleBodyById(id);
    }
}
