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
     * 验证码前缀
     */
    private static final String PREDIX_KAPTCHA = "kaptcha";

    /**
     * 忘记密码页面验证码前缀
     */
    private static final String PREFIX_FORGET = "forget";

    /**
     * 登录凭证前缀
     */
    private static final String PREFIX_TICKET = "ticket";

    /**
     * 用户缓存前缀
     */
    private static final String PREFIX_USER = "user";

    /**
     * 独立访客前缀
     */
    private static final String PREFIX_UV = "uv";

    /**
     * 日活跃用户前缀
     */
    private static final String PREFIX_DAU = "dau";

    /**
     * 帖子前缀
     */
    private static final String PREFIX_POST = "post";

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

    /**
     * 获取验证码 Key 值
     *
     * @param owner 标识验证码应该属于哪个用户
     * @return 验证码 Key 值
     */
    public static String getKaptchaKey(String owner) {
        return PREDIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 获取忘记密码页面的验证码
     *
     * @param userId 用户 ID
     * @return Redis Key
     */
    public static String getForgetKey(int userId) {
        return PREFIX_FORGET + SPLIT + userId;
    }

    /**
     * 生成登录凭证的 key 值
     *
     * @param ticket ticket 字符串
     * @return 登录凭证保存的 key 值
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 返回用户缓存的 Key 值
     *
     * @param userId 用户 ID
     * @return 用户缓存 Key 值
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 获取当日 UV 的 Key 值
     * 格式： uv:date
     *
     * @param date 当天的日期字符串
     * @return UV Key
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 获取区间之间的 UV
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return redis 的 key 值
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 获取当日的 DAU 的 key 值
     *
     * @param date 日期字符串
     * @return 单日 DAU 的 key
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 获取区间之间的 DAU 数据
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 区间之间的 DAU
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 获取帖子分数的 key
     *
     * @return key 值
     */
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

}
