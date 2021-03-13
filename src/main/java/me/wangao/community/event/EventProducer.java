package me.wangao.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.wangao.community.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class EventProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    // 处理事件
    public void fireEvent(Event event) {
        if (event == null) {
            log.error("event cannot be null");
            return;
        }
        if (event.getTopic() == null) {
            log.error("topic cannot be null");
            return;
        }
        // 将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}
