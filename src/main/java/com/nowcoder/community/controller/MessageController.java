package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author : Real
 * @date : 2021/12/6 15:01
 * @description :
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 显示私信列表
     * 处理的时候应该判断用户是否登录，获取用户的一些状态
     *
     * @param model model对象
     * @param page  分页显示对象
     * @return 私信列表页面
     */
    @LoginRequired
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        // 获取用户对象
        User user = hostHolder.getUser();
        // 设置分页的一些信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationsCount(user.getId()));
        // 会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        // 还需要显示的信息包含：未读消息总条数，单次会话消息的未读数量、总数量，会话对象相关信息
        if (conversationsList != null) {
            for (Message message : conversationsList) {
                // 查询每一条消息的其他相关信息
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                // 查询会话中消息的总数
                map.put("letterCount", messageService.findLettersCount(message.getConversationId()));
                // 查询未读消息的数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                // 查询会话列表的目标用户 ID ，需要显示会话中对方的信息
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                User targetUser = userService.findUserById(targetId);
                map.put("target", targetUser);
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询用户未读消息总数量，显示在会话 header 位置
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    /**
     * 处理会话列表的详情
     * 显示一个会话中的所有对话信息，同时处理的时候取消未读消息
     *
     * @param conversationId 会话 ID ，使用 URL 传递参数
     * @param model          model 对象
     * @return 会话详情页面
     */
    @LoginRequired
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        User user = hostHolder.getUser();
        // 设置分页的一些参数
        page.setRows(messageService.findLettersCount(conversationId));
        page.setPath("/letter/detail/" + conversationId);
        page.setLimit(5);
        // 获取会话对象
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letterList = new ArrayList<>();
        if (letters != null) {
            for (Message letter : letters) {
                Map<String, Object> map = new HashMap<>();
                // int targetId = letter.getFromId() == user.getId() ? letter.getToId() : letter.getFromId();
                // User target = userService.findUserById(targetId);
                // map.put("target", target);
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letterList.add(map);
            }
        }
        // 私信详情列表
        model.addAttribute("letters", letterList);
        // 私信目标用户
        model.addAttribute("target", getTargetUser(conversationId));
        // 设置已读
        List<Integer> unreadConversationId = getUnreadConversationId(letters);
        if (!unreadConversationId.isEmpty()) {
            messageService.readMessage(unreadConversationId);
        }
        return "/site/letter-detail";
    }

    /**
     * 获取私信用户对象
     *
     * @param conversationId 会话 ID
     * @return 对方用户
     */
    private User getTargetUser(String conversationId) {
        // conversationId 是使用双方的 id 中间拼接 _ 符号构成的
        String[] split = conversationId.split("_");
        int user1 = Integer.parseInt(split[0]);
        int user2 = Integer.parseInt(split[1]);
        // 获取当前登录的用户，返回对方用户
        if (hostHolder.getUser().getId() == user1) {
            return userService.findUserById(user2);
        } else {
            return userService.findUserById(user1);
        }
    }

    /**
     * 获取集合中未读消息的状态
     *
     * @param letters
     * @return
     */
    private List<Integer> getUnreadConversationId(List<Message> letters) {
        List<Integer> list = new ArrayList<>();
        if (letters != null) {
            for (Message letter : letters) {
                if (hostHolder.getUser().getId() == letter.getToId() && letter.getStatus() == 0) {
                    // 只有当接收者是一个未读的状态，才会改变消息的状态
                    list.add(letter.getId());
                }
            }
        }
        return list;
    }

    /**
     * 发送私信的方法
     *
     * @param toName  发送的用户名
     * @param content 发送的私信内容
     * @return 发送之后的页面，刷新的私信列表
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }
        // 利用目标用户以及私信内容构建一个 Message 对象
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        messageService.addMessage(message);

        // 表示处理成功
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 显示通知列表
     *
     * @param model model 对象
     * @return 通知列表页面
     */
    @LoginRequired
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        // 三种类型的通知都需要查询
        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), CommunityConstant.TOPIC_COMMENT);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), CommunityConstant.TOPIC_COMMENT);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), CommunityConstant.TOPIC_COMMENT);
            messageVO.put("unread", unread);
            model.addAttribute("commentNotice", messageVO);
        }

        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), CommunityConstant.TOPIC_LIKE);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), CommunityConstant.TOPIC_LIKE);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), CommunityConstant.TOPIC_LIKE);
            messageVO.put("unread", unread);
            model.addAttribute("likeNotice", messageVO);
        }


        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), CommunityConstant.TOPIC_FOLLOW);

        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), CommunityConstant.TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), CommunityConstant.TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            model.addAttribute("followNotice", messageVO);
        }


        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @LoginRequired
    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        // 获取当前登录用户
        User user = hostHolder.getUser();

        // 设置分页的一些参数
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        // 查询通知列表
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);

        // 设置已读
        List<Integer> ids = getUnreadConversationId(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

}
