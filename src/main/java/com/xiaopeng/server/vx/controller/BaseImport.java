//package com.xiaopeng.server.vx.controller;
//
//import com.alibaba.excel.annotation.ExcelIgnore;
//import com.alibaba.excel.annotation.ExcelProperty;
//import com.baiinfo.backend.common.BaseParamDto;
//import com.baomidou.mybatisplus.annotation.TableField;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import java.io.Serializable;
//
//@Data
//public class BaseImport extends BaseParamDto implements Serializable {
//    @ExcelIgnore
//    @TableField(exist = false)
//    private boolean checkPass = true;
//    @TableField(exist = false)
//    @ExcelProperty(value = "异常信息")
//    private String errMsg;
//
//    @TableField(exist = false)
//    @ExcelIgnore
//    private boolean saveResult;
//    @TableField(exist = false)
//    @ExcelIgnore
//    private int retryCount;
//
//    @TableField(exist = false)
//    @ExcelIgnore
//    @ApiModelProperty(value = "0:新增，1删除,2:更新")
//    private Integer operType;
//    @TableField(exist = false)
//    @ExcelIgnore
//    private boolean importOper;
//}
