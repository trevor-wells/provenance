package io.collective.articles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of("application/json", "text/html"), request, servletResponse, () -> {

            {
                List<ArticleRecord> articles = gateway.findAll();
                List<ArticleInfo> articleInfos = new ArrayList<>();
                articles.forEach((articleRecord) -> {
                    articleInfos.add(new ArticleInfo(articleRecord.getId(), articleRecord.getTitle()));
                });

                writeJsonBody(servletResponse, articleInfos);
            }
        });

        get("/available", List.of("application/json"), request, servletResponse, () -> {

            {
                List<ArticleRecord> availableArticle = gateway.findAvailable();
                List<ArticleInfo> availableArticleInfos = new ArrayList<>();
                availableArticle.forEach((articleRecord) -> {
                    availableArticleInfos.add(new ArticleInfo(articleRecord.getId(), articleRecord.getTitle()));
                });

                writeJsonBody(servletResponse, availableArticleInfos);
            }
        });
    }
}
