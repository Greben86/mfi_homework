package com.mfi_homework.mfi_homework.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@Table(name = "articles")
public class Article {
    @Id
    private long id;
    private String title;
    @Column(name = "news_site")
    private String newsSite;
    @Column(name = "published_date")
    private Date publishedDate;
    @Lob
    @JsonIgnore
    private String article;
}


