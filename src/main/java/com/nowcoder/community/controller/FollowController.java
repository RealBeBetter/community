package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Real
 * @date : 2021/12/10 15:03
 * @description : 关注/取关
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @LoginRequired
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已关注！");
    }

    @LoginRequired
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注！");
    }

    /**
     * 查询用户的关注列表
     *
     * @param userId 用户 ID
     * @param page   分页对象
     * @param model  model 对象
     * @return 关注列表页面
     */
    @RequestMapping(path = "/followee/{userId}", method = RequestMethod.GET)
    public String getFollowee(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        // 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        // 设置分页的一些参数
        page.setPath("/followee/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String, Object>> list = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        if (list != null) {
            for (Map<String, Object> map : list) {
                User followeeUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followeeUser.getId()));
            }
        }
        model.addAttribute("users", list);
        return "/site/followee";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }

    /**
     * 查询用户的粉丝列表
     *
     * @param userId 用户 ID
     * @param page   分页对象
     * @param model  model 对象
     * @return 用户粉丝页面
     */
    @RequestMapping(path = "/follower/{userId}", method = RequestMethod.GET)
    public String getFollower(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        // 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        // 设置分页的一些参数
        page.setPath("/follower/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> list = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if (list != null) {
            for (Map<String, Object> map : list) {
                User followerUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followerUser.getId()));
            }
        }
        model.addAttribute("users", list);
        return "/site/follower";
    }

}
