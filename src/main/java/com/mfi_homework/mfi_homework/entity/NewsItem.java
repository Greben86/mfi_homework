package com.mfi_homework.mfi_homework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class NewsItem {
    private int id;
    private String title;
    private String url;
    private String imageUrl;
    private String newsSite;
    private String summary;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date publishedAt;
}
