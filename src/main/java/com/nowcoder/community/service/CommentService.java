package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Comment> findCommentsByEntity(int entityId, int entityType, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityId, entityType, offset, limit);
    }

    public int findCommentsCount(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }

}
