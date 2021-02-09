package me.wangao.community.quartz;

import me.wangao.community.entity.DiscussPost;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.ElasticsearchService;
import me.wangao.community.service.LikeService;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private final static Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private LikeService likeService;

    @Resource
    private ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01");
        } catch (ParseException e) {
            throw new RuntimeException("初始化起始时间失败");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String scoreKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations<String, Object> operations = redisTemplate.boundSetOps(scoreKey);

        Long size = operations.size();
        if (size == null || size == 0) {
            logger.info("任务取消，没有需要刷新的帖子");
            return;
        }

        logger.info("任务开始，正在刷新帖子");

        while (operations.size() > 0) {
            Integer postId = (Integer ) operations.pop();
            if (postId != null) {
                refresh((int)postId);
            }
        }

        logger.info("任务结束，帖子刷新完毕");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null ) {
            logger.error("该帖子不存在，id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double weight = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 计算分数
        double score = Math.log10(Math.max(weight, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000. * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);

        // 同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
