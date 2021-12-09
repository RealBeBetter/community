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
    public static final String PREFIX_USER_LIKE = "like:user";

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
     * @param userId 用户 ID
     * @return Redis 中用到的 Key 值
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

}
