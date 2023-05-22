package com.xiaopeng.server.vx.entity;

import lombok.Data;

/**
 * @ClassName: WeChatMsgResult
 * @Author: BUG-WZP
 * @Since: 2023/5/22
 * @Remark:
 */
@Data
public class WeChatMsgResult {

    private Integer errcode;

    private String errmsg;

    private String msgid;
}

