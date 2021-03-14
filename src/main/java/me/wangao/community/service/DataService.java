package me.wangao.community.service;

import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final ThreadLocal<DateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    // 将指定IP计入UV
    public void recordUV(String ip) {
        String key = RedisKeyUtil.getUVKey(df.get().format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key, ip);
    }

    // 统计指定日期范围内的UV
    public long calculateUV(Date start, Date end) {
        checkDateParam(start, end);

        // 整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUVKey(df.get().format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }

        // 合并数据
        String redisKey = RedisKeyUtil.getDAUKey(df.get().format(start), df.get().format(end));
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
        String dauKey = RedisKeyUtil.getDAUKey(df.get().format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    public long calculateDAU(Date start, Date end) {
        checkDateParam(start, end);

        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(df.get().format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        String rangeKey = RedisKeyUtil.getDAUKey(df.get().format(start), df.get().format(end));
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

    /**
     * 获取时间段内每天的UV
     * 格式[{date: yyyy-MM-dd, data: xxx}, ....]
     */
    public List<Map<String, Object>> listUV(Date start, Date end) {
        checkDateParam(start, end);

        // 前端需要的时间格式
        SimpleDateFormat frontDF = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        List<Map<String, Object>> result = new ArrayList<>();

        while (!calendar.getTime().after(end)) {
            Map<String, Object> day = new HashMap<>();
            String key = RedisKeyUtil.getUVKey(df.get().format(calendar.getTime()));
            // 获取该日的UV
            long count = redisTemplate.opsForHyperLogLog().size(key);

            // 填充数据
            day.put("date", frontDF.format(calendar.getTime()));
            day.put("data", count);

            result.add(day);
            calendar.add(Calendar.DATE, 1);
        }

        return result;
    }

    /**
     * 获取时间段内每天的UV
     * 格式[{date: yyyy-MM-dd, data: xxx}, ....]
     */
    public List<Map<String, Object>> listDAU(Date start, Date end) {
        checkDateParam(start, end);

        // 前端需要的时间格式
        SimpleDateFormat frontDF = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        List<Map<String, Object>> result = new ArrayList<>();

        while (!calendar.getTime().after(end)) {
            Map<String, Object> day = new HashMap<>();
            String key = RedisKeyUtil.getDAUKey(df.get().format(calendar.getTime()));
            // 获取该日的DAU
            Long countBoxed = redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
            long count = countBoxed == null ? 0 : countBoxed;

            // 填充数据
            day.put("date", frontDF.format(calendar.getTime()));
            day.put("data", count);

            result.add(day);
            calendar.add(Calendar.DATE, 1);
        }

        return result;
    }

    private void checkDateParam(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("时间参数不合法");
        }
    }

    /**
     * 获取今日的UV
     */
    public long getTodayUV() {
        Calendar today = Calendar.getInstance();
//        Calendar lastDay = (Calendar)today.clone();
//        lastDay.add(Calendar.DATE, -1);
        return calculateUV(today.getTime(), today.getTime());
    }

    /**
     * 获取今日的DAU
     */
    public long getTodayDAU() {
        Calendar today = Calendar.getInstance();
//        Calendar lastDay = (Calendar)today.clone();
//        lastDay.add(Calendar.DATE, -1);
        return calculateDAU(today.getTime(), today.getTime());
    }
}
