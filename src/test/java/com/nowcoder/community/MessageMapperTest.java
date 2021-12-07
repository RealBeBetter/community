package com.nowcoder.community;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author : Real
 * @date : 2021/12/6 14:01
 * @description : 测试 MessageMapper
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMessageMapper() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
        int i = messageMapper.selectConversationsCount(111);
        System.out.println(i);

        List<Message> letters = messageMapper.selectLetters("111_114", 0, 20);
        for (Message letter : letters) {
            System.out.println(letter);
        }

        int count = messageMapper.selectedLetterCount("111_114");
        System.out.println(count);

        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }

}
