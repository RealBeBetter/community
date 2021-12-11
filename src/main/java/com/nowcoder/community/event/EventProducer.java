package com.nowcoder.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : Real
 * @date : 2021/12/11 16:29
 * @description : 事件生产者
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件，将 Event 对象转换成 JSON 字符串，发送到 Kafka 队列中
     *
     * @param event event 事件对象
     */
    public void fireEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }

}
