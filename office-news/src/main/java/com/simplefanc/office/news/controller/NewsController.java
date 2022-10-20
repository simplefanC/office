package com.simplefanc.office.news.controller;

import com.simplefanc.office.common.auth.Logical;
import com.simplefanc.office.common.auth.RequiresPermissions;
import com.simplefanc.office.common.util.R;
import com.simplefanc.office.news.controller.form.SearchNewsByPageForm;
import com.simplefanc.office.news.controller.form.SearchNewsContentForm;
import com.simplefanc.office.news.dto.Article;
import com.simplefanc.office.news.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PostMapping("/getNewsByPage")
    @ApiOperation("获取新闻列表")
    public R getNewsByPage(@Valid @RequestBody SearchNewsByPageForm form) {
        List<Article> list = newsService.getNewsByPage(form.getUrl(), form.getPage());
        return R.ok().put("result", list);
    }

    @PostMapping("/getNewsContent")
    @ApiOperation("获取新闻详情")
    public R getNewsContent(@Valid @RequestBody SearchNewsContentForm form) {
        return R.ok().put("result", newsService.getNewsContent(form.getUrl()));
    }
}
