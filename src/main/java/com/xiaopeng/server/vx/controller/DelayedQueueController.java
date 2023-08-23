package com.xiaopeng.server.vx.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.vx.config.RabbitMqConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DelatQueueConsumer
 * @Author: BUG-WZP
 * @Since: 2023/8/11
 * @Remark:
 */
@RestController
@RequestMapping("/delayedQueue")
@Slf4j
@Api(tags = "延时队列")
public class DelayedQueueController {
    /**
     * todo 延时队列
     */
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    /**
     * 对消息本身进行设置，ttl设置过期时间
     * @param message
     * @return
     */
    @GetMapping("/sendMsg")
    @ApiOperation("发送消息--带message")
    public Boolean sendMsg(@RequestParam("message") String message) {
        log.info("当前时间：{}，发送一条消息给两个TTL队列：{}", DateUtil.now(), message);
//        long delay = TimeUnit.DAYS.toMillis(7);
        JSONObject s = new JSONObject();
        s.put("消息来着ttl为10s的队列",message);
        JSONObject s1 = new JSONObject();
        s1.put("消息来着ttl为s的队列",message);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAYED_EXCHANGE_NAME, RabbitMqConfig.DELAYED_ROUTING_KEY, s,
                message1 -> {
                    //给消息设置延迟毫秒值
                    message1.getMessageProperties().setHeader("x-delay", 10000);
                    return message1;
                });

        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAYED_EXCHANGE_NAME, RabbitMqConfig.DELAYED_ROUTING_KEY, s1,
                message1 -> {
                    //给消息设置延迟毫秒值
                    message1.getMessageProperties().setHeader("x-delay", 60000);
                    return message1;
                });
        return true;
    }

    @GetMapping("/sendExpirationMsg")
    @ApiOperation("发送消息--带message和ttlTime")
    public void sendMsg(@RequestParam("message") String message, @RequestParam("ttlTime") String ttlTime) {
        log.info("当前时间：{}，发送一条时长{}毫秒的TTL消息给normal03队列：{}", DateUtil.now(), ttlTime, message);
        JSONObject s = new JSONObject();
        s.put("message",message);
        s.put("ttlTime",ttlTime);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAYED_EXCHANGE_NAME, RabbitMqConfig.DELAYED_ROUTING_KEY, s, msg -> {
            //发送消息的时候延迟时长
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    /**
     * 给延迟队列发送消息
     *
     * @param message
     * @param delayTime
     */
    @GetMapping("/sendDelayMsg")
    @ApiOperation("发送消息--带message和delayTime")
    public void sendMsg(@RequestParam("message") String message, @RequestParam("delayTime") Integer delayTime) {
        log.info("当前时间：{}，发送一条时长{}毫秒的消息给延迟队列：{}", DateUtil.now(), delayTime, message);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAYED_QUEUE_NAME, RabbitMqConfig.DELAYED_ROUTING_KEY, message, msg -> {
            //发送消息的时候延迟时长
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        });
    }
}
