package me.wangao.community.service;

import lombok.extern.slf4j.Slf4j;
import me.wangao.community.dao.CommentMapper;
import me.wangao.community.dao.DiscussPostMapper;
import me.wangao.community.dao.NodeMapper;
import me.wangao.community.dao.UserMapper;
import me.wangao.community.entity.Node;
import me.wangao.community.util.RedisKeyUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class InitialService implements CommandLineRunner {

    @Resource
    private CounterService counterService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private NodeMapper nodeMapper;

//    private static final String userCounterThreadName = "userCounterInit";
//    private static final String postCounterThreadName = "postCounterInit";
//    private static final String commentCounterThreadName = "commentCounterInit";
//    private static final String nodePostCounterThreadName = "nodePostCounterInit";


    @Override
    public void run(String... args) throws Exception {
        // TODO 初始化计数器
        counterService.set(RedisKeyUtil.getUserCounterKey(), 0);
        counterService.set(RedisKeyUtil.getPostCounterKey(), 0);
        counterService.set(RedisKeyUtil.getCommentCounterKey(), 0);
        counterService.set(RedisKeyUtil.getNodePostCounterKey(1), 0);
        log.info("开始刷新计数器");

        CountDownLatch counterLatch = new CountDownLatch(4);
        ExecutorService executor = Executors.newFixedThreadPool(50);

        executor.execute(() -> {
            int userCount = userMapper.selectRows();
            counterService.set(RedisKeyUtil.getUserCounterKey(), userCount);
            log.info("已刷新用户计数器");
            counterLatch.countDown();
        });

        executor.execute(() -> {
            int postCount = discussPostMapper.selectDiscussPostRows(null);
            counterService.set(RedisKeyUtil.getPostCounterKey(), postCount);
            log.info("已刷新帖子计数器");
            counterLatch.countDown();
        });

        executor.execute(() -> {
            int commentRows = commentMapper.selectRows();
            counterService.set(RedisKeyUtil.getCommentCounterKey(), commentRows);
            log.info("已刷新评论帖子计数器");
            counterLatch.countDown();
        });

        executor.execute(() -> {
            List<Node> nodes = nodeMapper.selectAllNodes();
            CountDownLatch nodePostCounterLatch = new CountDownLatch(nodes.size());
            nodes.forEach(node -> {
                int nodePostCount = discussPostMapper.selectRowsByNodeId(node.getId());
                counterService.set(RedisKeyUtil.getNodePostCounterKey(node.getId()), nodePostCount);
                log.info("已刷新评论节点计数器(" + node.getName() + ")");
                nodePostCounterLatch.countDown();
            });
            try {
                nodePostCounterLatch.await();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        });

        counterLatch.await();
        log.info("已刷新所有计数器");
    }
}
