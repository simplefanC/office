package com.simplefanc.office.news.service.impl;

import cn.hutool.http.HttpUtil;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.news.dto.Article;
import com.simplefanc.office.news.service.NewsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    /**
     * 学院新闻
     * 第2页 http://www.wust.edu.cn/jsjkx/1563/list2.htm
     */
    @Override
    public List<Article> getNewsByPage(String url, Integer page) {
        List<Article> list = new ArrayList<>();
        String content = HttpUtil.get(url + "/list" + page + ".htm");
        Document doc = Jsoup.parse(content);
//        #col_news_list下的ul标签
        Elements uls = doc.select(".col_news_list ul");
        if (uls.size() > 0) {
            Element articleList = uls.get(0);
            String protocol, host;// 获取主机名
            try {
                URL tempUrl = new URL(url);
                protocol = tempUrl.getProtocol();
                host = tempUrl.getHost();
            } catch (MalformedURLException e) {
                throw new EmosException("域名解析失败");
            }
            articleList.getElementsByTag("li").forEach(li -> {
                Element aElem = li.select("a").get(0);
                String href = aElem.attr("href");
                String title = aElem.attr("title");
                // li下第二标签下的值
                String date;
                if (li.childNode(3).childNodeSize() == 1) {//教务处
                    date = li.childNode(3).childNode(0).toString();
                } else {
                    date = li.childNode(3).childNode(1).childNode(0).toString();
                }
                list.add(Article.builder().title(title).date(date).articleUrl(protocol + "://" + host + href).build());
            });
        }
        return list;
    }

    /**
     * http://www.wust.edu.cn/jsjkx/2020/0519/c1563a216281/page.htm
     *
     * @param articleUrl
     * @return
     */
    @Override
    public Article getNewsContent(String articleUrl) {
        String content = HttpUtil.get(articleUrl);
        Document doc = Jsoup.parse(content);
        Element articleContent = doc.getElementsByClass("wp_articlecontent").get(0);

        String publisher = "";
        if(doc.getElementsByClass("arti_publisher").size() > 0){
            publisher = doc.getElementsByClass("arti_publisher").get(0).childNode(0).toString();
        }
        String date = "";
        if(doc.getElementsByClass("arti_update").size() > 0){
            date = doc.getElementsByClass("arti_update").get(0).childNode(0).toString();
        }

        String protocol, host;
        try {
            URL tempUrl = new URL(articleUrl);
            protocol = tempUrl.getProtocol();
            host = tempUrl.getHost();
        } catch (MalformedURLException e) {
            throw new EmosException("域名解析失败");
        }
        Elements imgTags = articleContent.getElementsByTag("img");
        imgTags.forEach(imgTag -> imgTag.attr("src", protocol + "://" + host + imgTag.attr("src")));
        String html = articleContent.toString();
        //清除指定HTML标签，不包括内容
//        html = HtmlUtil.unwrapHtmlTag(html, "span");
//        //去除指定标签的所有属性
//        html = HtmlUtil.removeAllHtmlAttr(html, "div", "h1", "p", "span");
        return Article.builder().publisher(publisher).content(html).date(date).build();
    }
}
