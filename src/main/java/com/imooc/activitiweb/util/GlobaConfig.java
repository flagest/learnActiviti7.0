package com.imooc.activitiweb.util;


/**
 * 全局配置
 *
 * @author jzwu
 * @since 2021/3/2 0002
 */
public class GlobaConfig {
    public static final Boolean test = true;
    /* windows 环境下的地址 */
    public static final String BPMN_MAPPING = "file:E:\\study\\Internet_Java\\imooc\\Activiti7.0\\code\\learn\\learnActivitiWeb\\src\\main\\resources\\resources\\bpmn\\";
    /* Linux 环境下地址 */
//    public static final String BPMN_MAPPING = "/root/activiti";


    public enum ResponseCode {

        SUCCESS(0, "操作成功"),
        ERROR(1, "操作失败");

        private final int code;
        private final String desc;

        ResponseCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}