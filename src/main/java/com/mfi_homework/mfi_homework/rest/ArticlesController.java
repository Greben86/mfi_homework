package com.mfi_homework.mfi_homework.rest;

import com.mfi_homework.mfi_homework.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/articles")
public class ArticlesController {

    private final ArticleService articleService;

    @GetMapping(value = "/all", produces = "application/json")
    public List<String> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping(value = "/site", produces = "application/json")
    public List<String> getArticlesByNewsSite(@RequestParam(value = "news_site", required = false) String newsSite) {
        if (newsSite == null || newsSite.isBlank()) {
            return articleService.getAllArticles();
        }

        return articleService.getArticlesByNewsSite(newsSite);
    }
}
