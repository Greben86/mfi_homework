package com.mfi_homework.mfi_homework.repository;

import com.mfi_homework.mfi_homework.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long> {

    @Query("select a from Article a where a.news_site = :newsSite")
    List<Article> findByNews_site(@Param("newsSite") String news_site);
}

