package com.xiaopeng.server.vx.service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @ClassName: ModifyResponseBodyWrapper
 * @Author: BUG-WZP
 * @Since: 2023/6/20
 * @Remark:
 */
public class ModifyResponseBodyWrapper extends HttpServletResponseWrapper {
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public ModifyResponseBodyWrapper(HttpServletResponse response) {
        super(response);
    }


}
