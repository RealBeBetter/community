package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
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

import java.util.*;

/**
 * @author : Real
 * @date : 2021/12/1 21:01
 * @description : 和 post 相关的请求处理
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 发布帖子，获取发布的用户，传入发布的帖子对象
     * @param title 标题
     * @param content 内容
     * @return JSON 字符串
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录哦~");
        }

        // 构造讨论贴
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 异常情况统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    /**
     * 获取帖子详情，包括帖子详情、作者、帖子评论
     *
     * @param id    帖子 ID
     * @param model model对象
     * @return 帖子详情页面
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page) {
        // 帖子详情
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPost);
        // 帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPost.getCommentCount());
        // 评论：帖子的评论
        // 回复：评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(discussPost.getId(), ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        // 评论 VO 列表
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment", comment);
                commentVO.put("user", userService.findUserById(comment.getUserId()));
                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(comment.getId(), ENTITY_TYPE_COMMENT, 0, Integer.MAX_VALUE);
                // 回复 VO 列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复
                        replyVO.put("reply", reply);
                        // 回复者
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);
                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("reply", replyVOList);
                // 回复数量
                int commentsCount = commentService.findCommentsCount(comment.getId(), ENTITY_TYPE_COMMENT);
                commentVO.put("replyCount", commentsCount);
                commentVOList.add(commentVO);
            }
        }

        model.addAttribute("comments", commentVOList);
        return "/site/discuss-detail";
    }


}
