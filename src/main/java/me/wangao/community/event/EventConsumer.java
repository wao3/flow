package me.wangao.community.event;

import com.alibaba.fastjson.JSON;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Event;
import me.wangao.community.entity.Message;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.ElasticsearchService;
import me.wangao.community.service.MessageService;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
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

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private MailClient mailClient;

    @KafkaListener(topics = { TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW })
    public void handleSystemMessage(ConsumerRecord<String, String> record) {
        Event event;
        if ((event = dealRecord(record)) == null) return;

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

    // 消费发帖事件，更新elasticsearch
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord<String, String> record) {
        Event event;
        if ((event = dealRecord(record)) == null) return;

        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);
    }

    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handleDeleteMessage(ConsumerRecord<String, String> record) {
        Event event;
        if ((event = dealRecord(record)) == null) return;

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    // 消费发送邮件事件
    @KafkaListener(topics = {TOPIC_EMAIL})
    public void handleSendEmail(ConsumerRecord<String, String> record) {
        Event event;
        if ((event = dealRecord(record)) == null) return;

        String to = (String) event.getData().get("to");
        String subject = (String) event.getData().get("subject");
        String content = (String) event.getData().get("content");
        if (StringUtils.isEmpty(to)) {
            logger.error("[send email] recipient cannot be empty");
            return;
        }
        if (StringUtils.isEmpty(subject)) {
            logger.error("[send email] subject cannot be empty");
            return;
        }
        if (StringUtils.isEmpty(content)) {
            logger.error("[send email] content cannot be empty");
            return;
        }

        mailClient.sendMail(to, subject, content);
    }

    /**
     * 处理 ConsumerRecord 通用逻辑，发送消息时应是JSON串，将其解析为 Event 并返回。
     * @param record 消息记录，格式应该是 Event 的JSON字符串。
     * @return 解析得到的 Event 对象，为 null 则说明消息为空或者解析失败。
     */
    private static Event dealRecord(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return null;
        }
        Event event = JSON.parseObject(record.value(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return null;
        }
        return event;
    }
}
