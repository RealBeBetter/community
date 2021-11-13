package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ author： Real
 * @ date： 2021年09月17日 22:33
 */
@Mapper
public interface DiscussPostMapper {

    // 查询首页需要显示的帖子
    // 显示发表用户，届时帖子需要点击至个人主页，所以需要显示用户个人的id
    // offset 每一行的行号， limit 分页显示的条数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param 用于给参数取别名。如果只有一个参数且需要在<if>里面使用，则必须加别名
    // 一共有多少条数据
    int selectDiscussPostRows(@Param("userId") int userId);

}
