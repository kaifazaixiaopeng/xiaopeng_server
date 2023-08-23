package com.xiaopeng.server.vx.vo;

import cloud.tianai.captcha.generator.common.model.dto.CustomData;
import lombok.Data;

/**
 * @ClassName: ImageCaptchaInfoVo
 * @Author: BUG-WZP
 * @Since: 2023/8/22
 * @Remark:
 */
@Data
public class ImageCaptchaInfoVo {
    private String backgroundImage;
    private String templateImage;
    private String backgroundImageTag;
    private String templateImageTag;
    private Integer backgroundImageWidth;
    private Integer backgroundImageHeight;
    private Integer templateImageWidth;
    private Integer templateImageHeight;
    private Integer Y;
    private Float tolerant;
    private String type;
    private CustomData data;
    private String randomX;
}
