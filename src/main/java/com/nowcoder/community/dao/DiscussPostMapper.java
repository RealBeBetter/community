package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : Real
 * @date : 2021年09月17日 22:33
 */
@Mapper
public interface DiscussPostMapper {

    /** 查询首页需要显示的帖子
     * 显示发表用户，届时帖子需要点击至个人主页，所以需要显示用户个人的id
     * offset 每一行的行号， limit 分页显示的条数
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit 最大限制
     * @return 分页列表对象
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 用于给参数取别名。如果只有一个参数且需要在<if>里面使用，则必须加别名
     * 一共有多少条数据
     * @param userId 用户 ID
     * @return 查询出的行数
     */
    int selectDiscussPostRows(@Param("userId") int userId);


    /**
     * 插入新的讨论帖子
     * @param discussPost 讨论帖对象
     * @return 插入的提示符
     */
    int insertDiscussPost(DiscussPost discussPost);


    /**
     * 查询帖子详情
     * @param id 根据帖子 id
     * @return 帖子
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 查询所有的帖子 Id
     * @return
     */
    List<Integer> selectDiscussPostIds();

    /**
     * 更新帖子的评论数量
     * @param id 帖子 ID
     * @param commentCount 评论数量
     * @return 更新结果
     */
    int updateCommentCount(int id, int commentCount);

}
