package com.imooc.activitiweb.pojo;


import lombok.Data;

/**
 * 表单相关数据
 *
 * @author jzwu
 * @since 2021/3/7 0007
 */
@Data
public class FormData {
    private String procDefId;
    private String procInstId;
    private String formKey;
    private String controlId;
    private String controlValue;
}