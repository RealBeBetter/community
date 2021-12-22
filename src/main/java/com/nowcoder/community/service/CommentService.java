package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author : Real
 * @date : 2021/12/2 22:10
 * @description : Comment 的 Service 层
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityId, int entityType, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityId, entityType, offset, limit);
    }

    public int findCommentsCount(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }


    /**
     * 添加评论，需要保证在一个事务当中
     *
     * @param comment 评论实体
     * @return 添加的评论条数
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 需要对评论的内容进行一些过滤
        if (comment == null) {
            throw new IllegalArgumentException("评论不能为空！");
        }
        // 过滤 html 标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 评论进行过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        // 添加评论
        int rows = commentMapper.insertComment(comment);
        // 更新帖子评论数量
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityId(), comment.getEntityType());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    /**
     * 根据用户 ID 查询用户发表的评论/回复数量
     *
     * @param userId 用户 ID
     * @return Comment 数量
     */
    public int findCommentCountByUserId(int userId) {
        return commentMapper.selectCountByUserId(userId);
    }

    /**
     * 根据用户查询发表的评论/回复
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit  分页限制
     * @return Comment 分页列表
     */
    public List<Comment> findCommentsByUserId(int userId, int offset, int limit) {
        return commentMapper.selectCommentsByUserId(userId, offset, limit);
    }
}
