package com.nowcoder.community.util;

/**
 * @author : Real
 * @date : 2021/12/8 18:45
 * @description : 生成 Redis 的 Key 工具类
 */
public class RedisKeyUtil {

    /**
     * 分隔符
     */
    private static final String SPLIT = ":";

    /**
     * 实体点赞数量前缀
     */
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    /**
     * 用户收到的赞前缀
     */
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 关注列表前缀
     */
    private static final String PREFIX_FOLLOWEE = "followee";

    /**
     * 粉丝列表前缀
     */
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 获取某个实体的赞的 Key 值
     * 格式： like:entity:entityType:entityId -> set(userId)
     *
     * @param entityType 实体类型
     * @param entityId   实体 ID
     * @return key 值
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户收到的赞的 Key 值
     * 格式：like:user:userId -> int
     *
     * @param userId 用户 ID
     * @return Redis 中用到的 Key 值
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 构造某个用户关注的实体对象的 Key 值，按照时间排序
     * 格式：followee:userId:entityType -> zset(entityId,now)
     *
     * @param userId     用户 ID
     * @param entityType 实体类型
     * @return 关注列表 Key 值
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体的粉丝数量，按照时间排序
     * 格式：follower:entityType:entityId -> zset(userId, now)
     *
     * @param entityType 实体类型
     * @param entityId   实体 ID
     * @return 实体粉丝的集合 Key 值
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
