package com.xiaopeng.server.app.bean.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auto:BUGPeng
 * @Date:2022/4/2816:51
 * @ClassName:ResultBean
 * @Remark:
 */
@Data
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NO_LOGIN = 1;

    public static final int SUCCESS = 0;

    public static final int CHECK_FAIL = -1;

    public static final int NO_PERMISSION = 2;

    public static final int FAIL = 3;

    public static final int UNKNOWN_EXCEPTION = -99;


    /**
     * 返回的信息(主要出错的时候使用)
     */
//    @ApiModelProperty(value = "消息内容")
    private String resultMsg = "success";

    /**
     * 接口返回码, 0表示成功, 其他看对应的定义
     * 晓风轻推荐的做法是:
     * 0   : 成功
     * >0 : 表示已知的异常(例如提示错误等, 需要调用地方单独处理)
     * <0 : 表示未知的异常(不需要单独处理, 调用方统一处理)
     */
//    @ApiModelProperty(value = "0 成功，1 字段异常 -99 其他异常")
    private int resultCode = SUCCESS;

    /**
     * 返回的数据
     */
//    @ApiModelProperty(value = "返回数据")
    private T resultData;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.resultData = data;
    }


    public ResultBean(int code, String msg, T data) {
        super();
        this.resultCode = code;
        this.resultMsg = msg;
        this.resultData = data;
    }

    public ResultBean(Throwable e) {
        super();
        this.resultMsg = e.toString();
        this.resultCode = UNKNOWN_EXCEPTION;
    }

    public ResultBean(int code, String msg) {
        super();
        this.resultCode = code;
        this.resultMsg = msg;
    }

    public static <T> ResultBean<T> of(Integer code, String msg,T data){
        return new ResultBean<>(code, msg, data);
    }

    public static <T> ResultBean<T> fail(String msg, T data) {
        return new ResultBean<>(UNKNOWN_EXCEPTION, msg, data);
    }
}
