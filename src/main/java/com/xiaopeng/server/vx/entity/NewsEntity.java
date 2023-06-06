package com.xiaopeng.server.vx.entity;

/**
 * @ClassName: NewsEntity
 * @Author: BUG-WZP
 * @Since: 2023/6/1
 * @Remark:
 */
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("news")
public class NewsEntity {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @JSONField(ordinal = 1)
    @TableField("title")
    private String title;

    @JSONField(ordinal = 2)
    @TableField("content")
    private String content;

    @JSONField(ordinal = 3)
    @TableField("images")
    private String img;

    @JSONField(ordinal = 4)
    private String url;

}
