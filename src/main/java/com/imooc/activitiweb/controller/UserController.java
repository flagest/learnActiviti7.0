package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.mapper.UserInfoBeanMapper;
import com.imooc.activitiweb.pojo.UserInfoBean;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 获取用户信息
 *
 * @author jzwu
 * @since 2021/3/11 0011
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserInfoBeanMapper userInfoBeanMapper;

    @GetMapping("/")
    public AjaxResponse getUserMessage() {
        try {
            List<Map<String, Object>> mapList = userInfoBeanMapper.selectUser();


            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询高亮流程历史失败",
                    e.getMessage());
        }
    }
}