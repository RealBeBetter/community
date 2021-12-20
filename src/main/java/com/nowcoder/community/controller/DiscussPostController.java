package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发布帖子，获取发布的用户，传入发布的帖子对象
     *
     * @param title   标题
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

        // 触发发帖事件，将帖子转存到 ES 服务器中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        // 计算帖子初始分数
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        // 将贴子 ID 转存到 set 中
        redisTemplate.opsForSet().add(postScoreKey, discussPost.getId());

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
        // 帖子点赞个数
        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);
        // 帖子点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);

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
                // 评论点赞个数
                likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                // 评论点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus", likeStatus);
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
                        // 回复点赞个数
                        likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 回复点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);
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

    /**
     * 将贴子修改为置顶
     * 置顶中，帖子 type 为 1
     *
     * @param id 帖子 ID
     * @return JSON 提示
     */
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);

        // 触发发帖事件，将帖子转存到 ES 服务器中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 取消帖子置顶状态
     * 普通帖子的 type 值为 0
     *
     * @param id 帖子 ID
     * @return JSONString
     */
    @RequestMapping(path = "/cancelTop", method = RequestMethod.POST)
    @ResponseBody
    public String cancelTop(int id) {
        discussPostService.updateType(id, 0);

        // 触发发帖事件，将帖子转存到 ES 服务器中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 帖子加精
     * 加精帖子的 status 设置为 1
     *
     * @param id 帖子 ID
     * @return JSON 操作提示
     */
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);
        // 触发帖子加精事件，将帖子转存到 ES 服务器中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey, id);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 取消帖子加精，将帖子还原成普通的帖子
     * 普通帖子 status 为 0
     *
     * @param id 帖子 ID
     * @return JSON 提示
     */
    @RequestMapping(path = "/cancelWonderful", method = RequestMethod.POST)
    @ResponseBody
    public String cancelWonderful(int id) {
        discussPostService.updateStatus(id, 0);
        // 触发发帖事件，将帖子转存到 ES 服务器中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 帖子删除
     *
     * @param id 帖子 ID
     * @return 操作结果 JSON 字符串
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);

        // 触发删帖事件，将 ES 服务器中对应的帖子删除，屏蔽搜索
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }


}
