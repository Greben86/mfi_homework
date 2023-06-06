package com.mfi_homework.mfi_homework.repository;

import com.mfi_homework.mfi_homework.entity.Article;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long> {

    List<Article> findByNewsSite(String newsSite);
}

