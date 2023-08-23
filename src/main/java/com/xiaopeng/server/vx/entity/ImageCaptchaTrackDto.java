package com.xiaopeng.server.vx.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ImageCaptchaTrackDto
 * @Author: BUG-WZP
 * @Since: 2023/7/28
 * @Remark:
 */
@Data
public class ImageCaptchaTrackDto {
    @ApiModelProperty("验证码类型")
    private String type;
    @ApiModelProperty("验证码标识")
    private String randomX;
    @ApiModelProperty("用户滑动x轴")
    private Integer targetX;
    @ApiModelProperty("用户滑动y轴")
    private Integer targetY;
    /** 背景图片宽度. */
    @ApiModelProperty("背景图片宽度")
    private Integer backgroundImageWidth;
    /** 背景图片高度. */
    @ApiModelProperty("背景图片高度")
    private Integer backgroundImageHeight;
    /** 滑块图片宽度. */
    @ApiModelProperty("滑块图片宽度")
    private Integer templateImageWidth;
    /** 滑块图片高度. */
    @ApiModelProperty("滑块图片高度")
    private Integer templateImageHeight;
    @ApiModelProperty("偏移量")
    private Float tolerant;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("应用id，默认201")
    private String clientId="201";
}
