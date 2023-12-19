package org.mj.blog.controller;

import lombok.RequiredArgsConstructor;
import org.mj.blog.dto.ArticleViewListResponse;
import org.mj.blog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model){
        List<ArticleViewListResponse> articles = blogService.findAll().stream()
                .map(ArticleViewListResponse::new).toList();

        model.addAttribute("articles", articles);

        return "articleList";
    }
}
