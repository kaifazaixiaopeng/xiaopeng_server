package com.xiaopeng.server.vx.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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
     *
     * 交换机类型：new DirectExchange();---- direct 它会把消息路由到那些 binding key 与 routing key 完全匹配的 Queue 中
     *           new FanoutExchange();---- fanout 它会把所有发送到该 Exchange 的消息路由到所有与它绑定的 Queue 中
     *           new HeadersExchange();---- headers 类型的 Exchange 不依赖于 routing key 与 binding key 的匹配规则来路由消息，
     *                                              而是根据发送的消息内容中的 headers 属性进行匹配。
     *                                              (headers 类型的交换器性能差，不实用，基本不会使用。）
     *           new TopicExchange();---- topic 与direct模型相比，多了个可以使用通配符！，
     *                                           这种模型Routingkey一般都是由一个或多个单词组成，
     *                                           多个单词之间以"."分割，
     *                                           例如：item.insert ---------星号 匹配一个1词 ,
     *                                           例audit.* ------- #号匹配一个或多个词 audit.#
     *           new CustomExchange()---- x-delayed-message 延迟交换机，可以延迟接收消息,这个比较复杂，
     *                                      1：延迟队列可以使用普通队列加死信队列完成，将消息放入没有被监听的队列中，然后将最大存活时间设置为延迟时间，
     *                                         时间到了没有被消费，转入死信队列，监听死信队列来进行消费，从而达到延迟效果
     *                                      2：去rabbitMQ官网下载延迟队列的插件，交换机就新增了一个延迟模式
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

//    @Bean
//    CustomExchange chatPluginDirect() {
//        //创建一个自定义交换机，可以发送延迟消息
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-delayed-type", "direct");
//        return new CustomExchange(DELAYED_TOPIC, "x-delayed-message", true, false, args);
//    }
//    @Bean
//    public Queue chatPluginQueue() {
//        return new Queue(DELAYED_QUEUE);
//    }
//    @Bean
//    public Binding chatPluginBinding() {
//        return BindingBuilder.bind(chatPluginQueue()).to(chatPluginDirect()).with(ROUTING_KEY).noargs();
//    }
    /**
     * 延迟队列
     */
    //队列
    public static final String DELAYED_QUEUE_NAME = "delayed_queue";

    //交换机
    public static final String DELAYED_EXCHANGE_NAME = "DELAYED_EXCHANGE";

    //路由key
    public static final String DELAYED_ROUTING_KEY = "delayed";

    //声明延迟队列
    @Bean
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    //声明延迟交换机
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>(3);
        //设置延迟类型
        arguments.put("x-delayed-type","direct");
        /*
          声明自定义交换机
          第一个参数：交换机的名称
          第二个参数：交换机的类型
          第三个参数：是否需要持久化
          第四个参数：是否自动删除
          第五个参数：其他参数
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME ,"x-delayed-message",true,false,arguments);
    }

    //绑定队列和延迟交换机
    @Bean
    public Binding delayedQueueBindingDelayedExchange(@Qualifier("delayedQueue") Queue delayedQueue,
                                                      @Qualifier("delayedExchange") Exchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }

    /**
     * 将自定义的RabbitTemplate对象注入bean容器
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
        //消息发送到交换机，但是没有到队列
//        设置ReturnCallback回调
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
