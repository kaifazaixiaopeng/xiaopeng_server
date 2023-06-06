package com.xiaopeng.server.vx.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: Controller
 * @Author: BUG-WZP
 * @Since: 2023/6/5
 * @Remark:
 */
@RestController
@RequestMapping("/test")
public class Controller {

    @PostMapping("/delete")
    public void delete(@RequestParam("ids") List<Integer> ids){
        System.out.println(JSONObject.toJSONString(ids));

    }
}
