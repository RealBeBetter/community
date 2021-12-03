package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Real
 * @date : 2021/12/2 21:49
 * @description : 评论 DAO 层
 */
@Mapper
public interface CommentMapper {

    /**
     * 分页查询评论
     *
     * @param entityId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityId, int entityType, int offset, int limit);

    /**
     * 查询评论的数量
     *
     * @param entityId
     * @param entityType
     * @return
     */
    int selectCountByEntity(int entityId, int entityType);

}
