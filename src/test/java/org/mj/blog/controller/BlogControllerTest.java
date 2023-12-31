package org.mj.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mj.blog.domain.Article;
import org.mj.blog.domain.User;
import org.mj.blog.dto.AddArticleRequest;
import org.mj.blog.dto.ArticleResponse;
import org.mj.blog.dto.UpdateAriticleRequest;
import org.mj.blog.repository.BlogRepository;
import org.mj.blog.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; // 역직렬화, 직렬화, JSON, JAVA 객체 변환에 사용하는 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext(){
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("test@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("addArticle 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception{
        //given
        final String url = "/api/articles";
        final String title = "testTitle";
        final String content = "testContent";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        //then 201 코드 확인
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAll Test")
    @Test
    public void testFindAll() throws Exception {

        //given
        final String url = "/api/articles";
        Article article = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(article.getTitle()))
                .andExpect(jsonPath("$[0].content").value(article.getContent()));


    }

    @DisplayName("test article findById")
    @Test
    public void testArticleFindById() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(get(url, article.getId()).accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(article.getContent()))
                .andExpect(jsonPath("$.title").value(article.getTitle()));
    }

    @DisplayName("test delete article")
    @Test
    public void deleteArticle() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(delete(url, article.getId()));

        //then
        result.andExpect(status().isNoContent());

        List<Article> articles = blogRepository.findAll();
        assertThat(articles.size()).isEqualTo(0);
    }

    @DisplayName("test update article")
    @Test
    public void update() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();

        //when
        final String updateTitle = "updateTitle";
        final String updateContent = "updateContent";
        String requestBody = objectMapper.writeValueAsString(new UpdateAriticleRequest(updateTitle, updateContent));

        ResultActions result = mockMvc.perform(put(url, article.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTitle))
                .andExpect(jsonPath("$.content").value(updateContent));
    }

    private Article createDefaultArticle(){
        return blogRepository.save(Article.builder()
                .title("testTitle")
                .content("testContent")
                .author(user.getUsername())
                .build());
    }
}