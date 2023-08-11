package com.xiaopeng.server.vx.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.vx.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName: DelayQueueConsumer
 * @Author: BUG-WZP
 * @Since: 2023/8/11
 * @Remark:
 */

@Slf4j
@Component
@RabbitListener(queues = RabbitMqConfig.DELAYED_QUEUE_NAME)
public class DelayQueueConsumer {
    @RabbitHandler
    private void receiveDelayQueue(JSONObject message) {

        log.info("当前时间{}，收到延迟队列的消息:{}", DateUtil.now(), message);
    }
}
