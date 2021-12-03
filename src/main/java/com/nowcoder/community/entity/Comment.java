package com.nowcoder.community.entity;

import java.util.Date;

/**
 * @author : Real
 * @date : 2021/12/2 21:42
 * @description : 用户发表的评论
 */
public class Comment {

    /**
     * 评论 ID
     */
    private int id;

    /**
     * 发表的用户 ID
     */
    private int userId;

    /**
     * 评论的实体类型，表示发表的评论属于哪种类别，比如 帖子 、回复的评论
     */
    private int entityType;

    /**
     * 评论的目标具体是哪个帖子 / 回复
     */
    private int entityId;

    /**
     * 评论的对象，表示评论对应的是帖子还是别人的回复
     */
    private int targetId;

    /**
     * 帖子的内容
     */
    private String content;

    /**
     * 评论的状态
     */
    private int status;

    /**
     * 评论的时间
     */
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
