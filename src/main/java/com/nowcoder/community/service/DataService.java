package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author : Real
 * @date : 2021/12/19 23:25
 * @description : 统计网站数据的 Service 层
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 实例化日期格式化对象
     */
    private final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

    /**
     * 记录 IP 值至 HLL 中
     *
     * @param ip ip 地址值
     */
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(sf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    /**
     * 统计指定日期范围内的 UV
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return UV 值 ( 不同的 IP 总数和）
     */
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null || start.after(end)) {
            throw new IllegalArgumentException("日期输入错误！");
        }

        // 整理日期范围内的 Key 值
        List<String> redisKeys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            // 从开始时间至结束时间将 key 值存入 List 中
            String uvKey = RedisKeyUtil.getUVKey(sf.format(calendar.getTime()));
            redisKeys.add(uvKey);
            // 将日期进行 + 1 处理
            calendar.add(Calendar.DATE, 1);
        }

        // 设置合并之后的 Key 值
        String uvKey = RedisKeyUtil.getUVKey(sf.format(start), sf.format(end));
        // 传入 key 的集合，将合并结果存入 uvKey 中
        redisTemplate.opsForHyperLogLog().union(uvKey, redisKeys.toArray());
        // 返回统计结果
        return redisTemplate.opsForHyperLogLog().size(uvKey);
    }

    /**
     * 将指定用户记录到 DAU
     */
    public void recordDAU(int userId) {
        String dauKey = RedisKeyUtil.getDAUKey(sf.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    public long calculateDAU(Date start, Date end) {
        // 输入日期判断
        if (start == null || end == null || start.after(end)) {
            throw new IllegalArgumentException("日期输入错误！");
        }

        // 整理日期范围内的 Key 值
        List<byte[]> redisKeys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            // 从开始时间至结束时间将 key 值存入 List 中
            String dauKey = RedisKeyUtil.getDAUKey(sf.format(calendar.getTime()));
            redisKeys.add(dauKey.getBytes());
            // 将日期进行 + 1 处理
            calendar.add(Calendar.DATE, 1);
        }

        // 进行 OR 运算
        return (long) redisTemplate.execute((RedisCallback) redisConnection -> {
            String dauKey = RedisKeyUtil.getDAUKey(sf.format(start), sf.format(end));
            redisConnection.bitOp(RedisStringCommands.BitOperation.OR, dauKey.getBytes(), redisKeys.toArray(new byte[0][0]));
            return redisConnection.bitCount(dauKey.getBytes());
        });
    }
}
