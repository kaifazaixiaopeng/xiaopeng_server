package com.xiaopeng.server.vx.service;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 自定义HttpServletRequestWrapper 用于修改请求体
 */
public class ModifyRequestBodyWrapper extends HttpServletRequestWrapper {

    private String oldRequestBody;

    private String newRequestBody;

    public ModifyRequestBodyWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.oldRequestBody = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            final ByteArrayInputStream bis = new ByteArrayInputStream(newRequestBody.getBytes(StandardCharsets.UTF_8));
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }

    public String getOldRequestBody() {
        return oldRequestBody;
    }

    public void setOldRequestBody(String oldRequestBody) {
        this.oldRequestBody = oldRequestBody;
    }

    public String getNewRequestBody() {
        return newRequestBody;
    }

    public void setNewRequestBody(String newRequestBody) {
        this.newRequestBody = newRequestBody;
    }
}

