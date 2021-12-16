package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author : Real
 * @date : 2021/12/3 21:41
 * @description : 处理评论的控制器
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 添加评论
     * @param discussionPostId 帖子 ID
     * @param comment 评论实体对象
     * @return 帖子详情页面，重定向
     */
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussionPostId,
                             Comment comment) {
        // 需要得知发表评论的用户，评论的类型，评论的对象
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussionPostId);

        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 发送消息
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            // 触发发帖事件，评论总数改变，需要修改 ES 中的数据
            Event postEvent = new Event()
                    .setTopic(CommunityConstant.TOPIC_PUBLISH)
                    .setUserId(comment.getId())
                    .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                    .setEntityId(discussionPostId);
            eventProducer.fireEvent(postEvent);
        }

        return "redirect:/discuss/detail/" + discussionPostId;
    }



}