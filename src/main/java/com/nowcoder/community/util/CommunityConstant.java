package com.nowcoder.community.util;

/**
 * @author : Real
 * @date : 2021/11/15 0:25
 * @description :
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    final int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    final int ACTIVATION_FAILED = 2;

    /**
     * 默认状态的登录凭证超时时间，24小时
     */
    final int DEFAULT_EXPIRED_SECONDS = 3600 * 24;

    /**
     * 记住状态的登录凭证超时时间，3个月
     */
    final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 90;

    /**
     * 实体类型：帖子
     */
    final int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    final int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    final int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户 ID ，用于构造系统通知消息。标注系统
     */
    int SYSTEM_USER_ID = 1;

}
