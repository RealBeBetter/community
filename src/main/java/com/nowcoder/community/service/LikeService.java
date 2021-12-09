package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author : Real
 * @date : 2021/12/9 16:38
 * @description : 点赞功能的 Service 层，使用 redis 来进行优化
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞功能，包括对评论/回复点赞，也包括对用户的点赞
     *
     * @param userId       点赞的执行者
     * @param entityType   点赞目标的类型：帖子/评论
     * @param entityId     点赞目标的 ID
     * @param entityUserId 实体的作者 ID ，要查询用户主页的点赞数量
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 查询不能放在事务中，需要将查询放在事务之外
                Boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);

                redisOperations.multi();
                if (isMember) {
                    // 如果存在该用户点赞的记录，再次点赞应该是取消点赞了
                    redisOperations.opsForSet().remove(entityLikeKey, userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    // 不存在点赞记录，则应该向列表中添加点赞记录（记录的是点赞的用户名单）
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }


                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询实体被点赞的数量
     *
     * @param entityType 实体类型：帖子/评论
     * @param entityId   实体 ID
     * @return 实体被点赞的数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 返回 Key 值对应的 Value 的数量：存取的是点赞的用户 ID
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某个人对于某个帖子是否进行了点赞/踩/无操作
     *
     * @param userId     用户 ID
     * @param entityType 实体类型
     * @param entityId   实体 ID
     * @return 状态对应的 ID 值， 1 对应点赞， 0 对应无操作， -1 对应踩
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return isMember ? 1 : 0;
    }

    /**
     * 查询某个用户获得的赞的数量
     * 查询的逻辑是该用户拥有多少个赞，包括发帖/评论获得的赞
     * @param userId 用户 ID
     * @return 收到赞的个数
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

}
