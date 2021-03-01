package com.imooc.activitiweb.jzwu_test;


import java.io.Serializable;

/**
 * @author jzwu
 * @since 2021/2/26 0026
 */

public class UEL_POJO implements Serializable {
    private String zhixingren;
    private String pay;

    public String getZhixingren() {
        return zhixingren;
    }

    public void setZhixingren(String zhixingren) {
        this.zhixingren = zhixingren;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }
}