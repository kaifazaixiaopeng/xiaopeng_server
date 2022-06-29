//package com.xiaopeng.server.app.feign;
//
//import com.alibaba.fastjson.JSONArray;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
///**
// * @Auto:BUGPeng
// * @Date:2022/6/28 14:46
// * @ClassName:DemoFeign
// * @Remark:
// */
//@Component
//@FeignClient(value = "wapnm-service", fallback = DemoFeignFallBack.class)
//public interface DemoFeign {
//        @GetMapping("/api/v1/demo/pageList")
//        List getFlowConfigs(@RequestParam("page")int page, @RequestParam("size") int size);
//
//}
