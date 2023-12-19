package org.mj.blog.dto;

import lombok.Getter;
import org.mj.blog.domain.Article;

@Getter
public class ArticleViewListResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ArticleViewListResponse(Article article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
