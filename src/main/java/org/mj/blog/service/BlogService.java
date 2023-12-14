package org.mj.blog.service;

import lombok.RequiredArgsConstructor;
import org.mj.blog.domain.Article;
import org.mj.blog.dto.AddArticleRequest;
import org.mj.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest addArticleRequest){
        return blogRepository.save(addArticleRequest.toEntity());
    }
}
