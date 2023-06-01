package com.xiaopeng.server.vx.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dgf
 * @date 2023年01月19日 15:37
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    /**
     * 交换机名称
     */
    public static final String AMQ_TOPIC = "amq.topic";
    /**
     * 队列名称 队列需要与交换机绑定
     */
    public static final String MY_QUEUE = "my_queue";

    /**
     * 路由key
     */
    public static final String ROUTING_KEY="my_routing_key";


    /**
     * FanoutExchange的参数说明:
     * 1. 交换机名称
     * 2. 是否持久化 true：持久化，交换机一直保留 false：不持久化，用完就删除 默认true
     * 3. 是否自动删除 false：不自动删除 true：自动删除
     */
    @Bean
    public TopicExchange setTopicExchange() {
        return new TopicExchange(AMQ_TOPIC);
    }

    /**
     * Queue的参数说明:
     * 1.队列名
     * 2.durable: 是否持久化；
     * 3.exclusive: 是否独享、排外的；
     * 4.autoDelete: 是否自动删除；
     * @return
     */
    @Bean
    public Queue myQueue() {
        return new Queue(MY_QUEUE);
    }




    /**
     * 3.
     * 队列与交换机绑定
     * routingKey  ---路由key
     */
    @Bean
    public Binding bindTopicMSRecord() {
        return BindingBuilder.bind(myQueue()).to(setTopicExchange()).with(ROUTING_KEY);
    }

    /**
     * 将自定义的RabbitTemplate对象注入bean容器
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置开启回调
        rabbitTemplate.setMandatory(true);
        //设置ConfirmCallback回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("回调数据：{}", correlationData);
            log.info("确认结果：{}", ack);
            log.info("返回原因：{}", cause);
        });
        //设置ReturnCallback回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("发送消息：{}", JSONUtil.toJsonStr(message));
            log.info("结果状态码：{}", replyCode);
            log.info("结果状态信息：{}", replyText);
            log.info("交换机：{}", exchange);
            log.info("路由key：{}", routingKey);
        });
        return rabbitTemplate;
    }
}
