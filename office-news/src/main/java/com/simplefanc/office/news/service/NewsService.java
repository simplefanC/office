package com.simplefanc.office.news.service;

import com.simplefanc.office.news.dto.Article;

import java.util.List;

public interface NewsService {
    List<Article> getNewsByPage(String url, Integer page);

    Article getNewsContent(String articleUrl);
}
