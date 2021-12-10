package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author : Real
 * @date : 2021/11/16 21:36
 * @description :
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    /*@Insert({"insert into login_ticket(user_id, ticket, status, expired) " +
            "values(#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")*/

    /**
     * 插入登录凭证
     * @param loginTicket 登录凭证
     * @return
     */
    int insertLoginTicket(LoginTicket loginTicket);

    /*@Select({"select id, user_id, ticket, status, expired " +
            "from login_ticket where ticket = #{ticket}"})*/

    /**
     * 查询登录凭证
     * @param ticket 登录生成的随机字符串
     * @return LoginTicket 对象
     */
    LoginTicket selectByTicket(String ticket);

    /*@Update({"update login_ticket set status = #{status} " +
            "where ticket = #{ticket}"})*/

    /**
     * 更新登录凭证的状态
     * @param ticket 使用生成的字符串
     * @param status 更改状态
     * @return 修改的行数
     */
    int updateStatus(String ticket, int status);
}
