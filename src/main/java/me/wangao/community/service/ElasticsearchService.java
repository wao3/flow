package me.wangao.community.service;

import me.wangao.community.dao.elasticsearch.DiscussPostRepository;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.SearchPage;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {

    @Resource
    private DiscussPostRepository discussRepository;

    @Resource
    private ElasticsearchRestTemplate elasticTemplate;

    public void saveDiscussPost(DiscussPost discussPost) {
        discussRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    public SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();

        SearchHits<DiscussPost> searchHits = elasticTemplate.search(searchQuery, DiscussPost.class);
        System.out.println("total hits: " + searchHits.getTotalHits());

        List<DiscussPost> postList = new ArrayList<>();
        searchHits.forEach(hit -> {
            DiscussPost post = hit.getContent();
            if (!hit.getHighlightField("content").isEmpty()) {
                post.setContent(hit.getHighlightField("content").get(0));
            }
            if (!hit.getHighlightField("title").isEmpty()) {
                post.setTitle(hit.getHighlightField("title").get(0));
            }
            postList.add(post);
        });

        return new SearchPage<DiscussPost>()
                .setCurrent(current)
                .setLimit(limit)
                .setTotal(searchHits.getTotalHits())
                .setList(postList);
    }
}
