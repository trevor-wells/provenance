package io.collective.endpoints;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleInfo;
import io.collective.restsupport.RestTemplate;
import io.collective.rss.Item;
import io.collective.rss.RSS;
import io.collective.workflow.Worker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear();

        {
            RSS rss = new XmlMapper().readValue(response, RSS.class);
            List<Item> rssItems = rss.getChannel().getItem();

            List<ArticleInfo> articleInfos = new ArrayList<>();

            for (int i = 0; i < rssItems.size(); i++) {
                Item item = rssItems.get(i);
                articleInfos.add(new ArticleInfo(i, item.getTitle()));
            }

            for (ArticleInfo articleInfo : articleInfos) {
                this.gateway.save(articleInfo.getTitle());
            }
        }
    }
}