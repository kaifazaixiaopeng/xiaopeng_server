//package com.xiaopeng.server.app.feign;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @Auto:BUGPeng
// * @Date:2022/6/28 14:49
// * @ClassName:DemoFeignFallBack
// * @Remark:
// */
//@Component
//@Slf4j
//public class DemoFeignFallBack implements DemoFeign{
//    @Override
//    public List getFlowConfigs(int page, int size) {
//        log.error("Calling remote service [{}] method [{}] failed!", "/pageList");
//        return null;
//    }
//}
