package com.xiaopeng.server.vx.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: ReplyMessageService
 * @Author: BUG-WZP
 * @Since: 2023/7/6
 * @Remark:
 */
public interface ReplyMessageService {
    String reply(HttpServletRequest request);
}
