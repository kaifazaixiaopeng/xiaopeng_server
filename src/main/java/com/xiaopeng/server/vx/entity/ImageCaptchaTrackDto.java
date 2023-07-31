package com.xiaopeng.server.vx.entity;

import lombok.Data;

/**
 * @ClassName: ImageCaptchaTrackDto
 * @Author: BUG-WZP
 * @Since: 2023/7/28
 * @Remark:
 */
@Data
public class ImageCaptchaTrackDto {
    private String type;
    private Integer randomX;
    private Integer targetX;
    private Integer targetY;
    /** 背景图片宽度. */
    private Integer backgroundImageWidth;
    /** 背景图片高度. */
    private Integer backgroundImageHeight;
    /** 滑块图片宽度. */
    private Integer templateImageWidth;
    /** 滑块图片高度. */
    private Integer templateImageHeight;
}
