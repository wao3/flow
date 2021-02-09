package me.wangao.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import me.wangao.community.dao.DiscussPostMapper;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (StringUtils.isBlank(key)) {
                            throw new IllegalArgumentException("参数错误");
                        }

                        String[] args = key.split(":");
                        if (args.length != 2) {
                            throw new IllegalArgumentException("参数错误");
                        }

                        int offset = Integer.parseInt(args[0]);
                        int limit = Integer.parseInt(args[1]);

                        // 二级缓存：访问 Redis，暂时未作
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(null, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer integer) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(integer);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(Integer userId, int offset, int limit, int orderMode) {
        if (userId == null && orderMode == 1) {
            String key = offset + ":" + limit;
            return postListCache.get(key);
        }
        logger.debug("load post list from db.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(Integer userId) {
        if (userId == null) {
            Integer rows = postRowsCache.get(0);
            return rows == null ? 0 : rows;
        }
        logger.debug("load post rows from db.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()))
                .setContent(HtmlUtils.htmlEscape(post.getContent()))
                // 过滤敏感词
                .setTitle(sensitiveFilter.filter(post.getTitle()))
                .setContent(sensitiveFilter.filter(post.getContent()))
                // 设置其他字段
                .setCreateTime(new Date())
                .setType(0)
                .setStatus(0)
                .setCommentCount(0)
                .setScore(0.);

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
