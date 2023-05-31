package com.mfi_homework.mfi_homework.rest;

import com.mfi_homework.mfi_homework.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("/api")
public class MainController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public List<String> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/articles/{newsSite}")
    public List<String> getArticlesByNewsSite(@PathVariable("newsSite") String newsSite) {
        return articleService.getArticlesByNewsSite(newsSite);
    }
}
