package com.mfi_homework.mfi_homework.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
@Table(name = "articles")
public class Article {
    @Id
    private long id;
    private String title;
    private String news_site;
    private Date published_date;
    private String article;
}


