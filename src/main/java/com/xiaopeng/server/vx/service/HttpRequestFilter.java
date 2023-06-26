//package com.xiaopeng.server.vx.service;
//
//
//import org.bouncycastle.util.encoders.Hex;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
//
//
//public class HttpRequestFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(HttpRequestFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        ModifyRequestBodyWrapper modifyRequestBodyWrapper = new ModifyRequestBodyWrapper(request);
//        String oldRequestBody = modifyRequestBodyWrapper.getOldRequestBody();
//        String sign = request.getHeader("IamSign");
//        logger.info("path:{}",request.getRequestURI());
//        logger.info("oldRequestBody：{}",oldRequestBody);
//        String newRequestBody = changeRequestBody(oldRequestBody);
//        logger.info("newRequestBody:{}",newRequestBody);
//        logger.info("pubKey:{}",pubKey);
//        logger.info("签名sign:{}",sign);
//        boolean flag = SM2Util.verify(pubKey,  Hex.toHexString("nfdw-csg".getBytes()), sign);
//        if (!flag){
//            logger.info("验签失败");
//            return;
//        }
//        //构造新请求体
//        modifyRequestBodyWrapper.setNewRequestBody(newRequestBody);
//        ModifyResponseBodyWrapper modifyResponseBodyWrapper = new ModifyResponseBodyWrapper(response);
//
//        filterChain.doFilter(modifyRequestBodyWrapper,modifyResponseBodyWrapper);
//
//        String oldResponseBody = modifyResponseBodyWrapper.getResponseBody();
//        logger.info("oldResponseBody:{}",oldResponseBody);
//        String newResponseBody = changeResponseBody(oldResponseBody);
//        logger.info("newResponseBody:{}",newResponseBody);
//        response.setContentType(request.getContentType());
//        byte[] responseBodyData = newResponseBody.getBytes(StandardCharsets.UTF_8);
//        response.setHeader("Content-Length",String.valueOf(responseBodyData.length));//解决数据过长导致截断问题
//
//        ServletOutputStream out = response.getOutputStream();
//        out.write(responseBodyData);
//    }
//
//    /**
//     * 修改请求体
//     * @param oldRequestBody 修改前的请求体
//     * @return 修改后的请求体
//     */
//    public String changeRequestBody(String oldRequestBody) throws UnsupportedEncodingException {
//        return Sm4Utils.decryptData_ECB(oldRequestBody,priKey);
//    }
//
//    /**
//     * 修改响应体
//     * @param oldResponseBody 修改前的响应体
//     * @return 修改够的响应体
//     */
//    public String changeResponseBody(String oldResponseBody) throws UnsupportedEncodingException {
//        return Sm4Utils.encryptData_ECB(oldResponseBody, priKey);
//    }
//
