package com.imooc.activitiweb.util;


import lombok.Data;

/**
 * 前后端响应
 *
 * @author jzwu
 * @since 2021/3/2 0002
 */
@Data
public class AjaxResponse {
    private Integer status;
    private String msg;
    private Object obj;

    public AjaxResponse(Integer status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public static AjaxResponse AjaxData(Integer status, String msg, Object obj) {
        return new AjaxResponse(status, msg, obj);
    }
}