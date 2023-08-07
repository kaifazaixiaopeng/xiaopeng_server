package com.xiaopeng.server.vx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @ClassName: StaticPage
 * @Author: BUG-WZP
 * @Since: 2023/8/7
 * @Remark:
 */
@Controller
public class StaticPage {
        @GetMapping({ "/robots"})
        public String toPage(){
            return null;
        }

}
