package com.simplefanc.office.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private String title;

    private String articleUrl;

    private String content;

    private String date;

    private String publisher;
}
