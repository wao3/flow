package me.wangao.community.service;

import me.wangao.community.entity.User;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FollowService implements CommunityConstant {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    // 查询目标关注的实体数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Long count =  redisTemplate.opsForZSet().zCard(followeeKey);

        return count == null ? 0 : count;
    }

    // 查询实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Long count =  redisTemplate.opsForZSet().zCard(followerKey);

        return count == null ? 0 : count;
    }

    // 查询用户是否关注某实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询某用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (ids == null) {
            return null;
        }

        List<Integer> targetIds = ids.stream().map(e -> (Integer) e).collect(Collectors.toList());

        List<Map<String, Object>> list = new ArrayList<>();
        targetIds.forEach(targetId -> {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", score == null ? new Date(0) : new Date(score.longValue()));
            list.add(map);
        });

        return list;
    }

    // 查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);

        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (ids == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        ids.stream().map(e -> (Integer) e).collect(Collectors.toList()).forEach(targetId -> {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", score == null ? new Date(0) : new Date(score.longValue()));
            list.add(map);
        });

        return list;
    }
}
