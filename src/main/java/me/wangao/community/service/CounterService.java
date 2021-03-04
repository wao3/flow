package me.wangao.community.service;

import me.wangao.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 计数器服务
 */
@Service
public class CounterService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void incr(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void decr(String key) {
        redisTemplate.opsForValue().decrement(key);
    }

    public int count(String key) {
        Object counter = redisTemplate.opsForValue().get(key);
        if (counter == null) {
            return 0;
        }
        if (counter instanceof Integer) {
            return (Integer) counter;
        }
        return Integer.parseInt((String) counter);
    }

    public void set(String key, int count) {
        redisTemplate.opsForValue().set(key, count);
    }

    public int getUserCount() {
        return getCount(RedisKeyUtil.getUserCounterKey());
    }

    public int getPostCount() {
        return getCount(RedisKeyUtil.getPostCounterKey());
    }

    public int getCommentCount() {
        return getCount(RedisKeyUtil.getCommentCounterKey());
    }

    public int getNodePostCount(int nodeId) {
        return getCount(RedisKeyUtil.getNodePostCounterKey(nodeId));
    }

    private int getCount(String key) {
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return count == null ? 0 : count;
    }
}
