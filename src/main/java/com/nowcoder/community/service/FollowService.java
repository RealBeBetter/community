package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : Real
 * @date : 2021/12/10 15:03
 * @description : 关注/取关服务
 */
@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 用户关注实体
     *
     * @param userId     用户 ID
     * @param entityType 被关注的实体类型
     * @param entityId   被关注的实体 ID
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 生成两个 Key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务，同时存储关注列表以及粉丝列表
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    /**
     * 用户取关实体
     *
     * @param userId     用户 ID
     * @param entityType 被取关的实体类型
     * @param entityId   被取关的实体 ID
     */
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 生成两个 Key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务，同时存储关注列表以及粉丝列表
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);

                return redisOperations.exec();
            }
        });
    }

    /**
     * 获取关注实体的数量
     *
     * @param userId     用户 ID
     * @param entityType 实体类型，关注的同类型实体的个数
     * @return 关注数量
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 获取实体的粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体 ID
     * @return 实体的粉丝数量
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 获取用户是否关注实体
     *
     * @param userId     用户 ID
     * @param entityType 实体类型
     * @param entityId   实体 ID
     * @return 是否关注
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }


    /**
     * 查询用户关注的用户对象，只查询关注的用户
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit  分页限制
     * @return Map 集合存储对应的关注类型以及关注对象
     */
    public List<Map<String, Object>> findFollowee(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, CommunityConstant.ENTITY_TYPE_USER);
        // 需要查询的数据，包含：关注的用户、关注的时间
        Set<Integer> range = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        // 判断是否存在关注的用户
        if (range == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : range) {
            Map<String, Object> map = new HashMap<>(2);
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 查询用户的粉丝列表，查询是哪些用户关注了该用户
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit  分页限制
     * @return 粉丝列表 List
     */
    public List<Map<String, Object>> findFollower(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(CommunityConstant.ENTITY_TYPE_USER, userId);
        // 查询在分页偏移量下的用户集合
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (set == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer followerId : set) {
            Map<String, Object> map = new HashMap<>(2);
            User user = userService.findUserById(followerId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, followerId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}
