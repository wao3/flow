package me.wangao.community.event;

import com.alibaba.fastjson.JSON;
import me.wangao.community.entity.Event;
import me.wangao.community.entity.Message;
import me.wangao.community.service.MessageService;
import me.wangao.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Resource
    private MessageService messageService;

    @KafkaListener(topics = { TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW })
    public void handleSystemMessage(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }

        Event event = JSON.parseObject(record.value(), Event.class);

        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        // 发送站内通知
        Message message = new Message()
                .setFromId(SYSTEM_USER_ID)
                .setToId(event.getEntityUserId())
                .setConversationId(event.getTopic())
                .setStatus(0)
                .setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            event.getData().forEach(content::put);
        }

        message.setContent(JSON.toJSONString(content));
        messageService.addMessage(message);
    }
}
