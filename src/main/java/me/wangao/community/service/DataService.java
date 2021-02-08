package me.wangao.community.service;

import me.wangao.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将指定IP计入UV
    public void recordUV(String ip) {
        String key = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key, ip);
    }

    // 统计指定日期范围内的UV
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }

        // 合并数据
        String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray(new String[0]));

        // 返回统计结果
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        redisTemplate.opsForHyperLogLog().delete(redisKey);
        return size;
    }

    // 将指定用户计入DAU
    public void recordDAU(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("userId 错误");
        }
        String dauKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        String rangeKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
        // 进行or运算
        Long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, rangeKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(rangeKey.getBytes());
            }
        });

        redisTemplate.delete(rangeKey);
        return count == null ? 0 : count;
    }
}
