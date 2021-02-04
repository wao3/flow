package me.wangao.community.service;

import me.wangao.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember != null && isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Long count = redisTemplate.opsForSet().size(entityLikeKey);
        return count == null ? 0 : count; // 避免空指针异常
    }

    // 查询某人对某实体的点赞状态，使用int方便以后拓展业务，比如点踩等功能
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

        int ret = 0;
        if (isMember != null && isMember) {
            ret = 1;
        }

        return ret;
    }
}
