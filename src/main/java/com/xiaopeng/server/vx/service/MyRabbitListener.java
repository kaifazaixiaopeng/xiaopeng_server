package com.xiaopeng.server.vx.service;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.xiaopeng.server.vx.TimedTesk;
import com.xiaopeng.server.vx.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RabbitListener
 * @Author: BUG-WZP
 * @Since: 2023/6/1
 * @Remark:
 */
@Component
@Slf4j
@RabbitListener(queues = {RabbitMqConfig.MY_QUEUE})
public class MyRabbitListener {
    /**
     * @param
     * @RabbitHandler: 需要注意的是，在使用 @RabbitHandler 注解时，方法的参数类型必须与消息的类型相匹配。
     * 例如，如果消息类型为字符串类型，则方法的参数类型也必须为字符串类型
     * 如果我们需要处理多种消息类型，可以在消费者类中定义多个带有 @RabbitHandler 注解的方法，每个方法处理一种消息类型
     */
    @RabbitHandler
    public void getMyRabbitListenerMessage(String weather) {
        log.info(weather);
        log.info("消费成功");
    }

    /**
     * 手动ack  -----      #将配置文件ack配置更改为acknowledge-mode: MANUAL
     * @param weather
     * @param message
     * @param channel
     */
//    @RabbitHandler
//    public void getMyRabbitListenerMessage(@Payload String weather, Message message, Channel channel) {
//        try {
////            TimeUnit.SECONDS.sleep(4);
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("消费成功");
//    }

}
