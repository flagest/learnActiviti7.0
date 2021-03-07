package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.pojo.UserInfoBean;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程历史控制类
 *
 * @author jzwu
 * @since 2021/3/6 0006
 */
@RestController
@RequestMapping("/activitiHistory")
public class ActivitiHistoryController {

    @Resource
    private HistoryService historyService;

    // 根据姓名查询实例
    @RequestMapping("/getInstancesByUserName")
    public AjaxResponse getInstancesByUserName(@AuthenticationPrincipal UserInfoBean userInfoBean) {
        try {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().desc()
                    .taskAssignee(userInfoBean.getUsername())
                    .listPage(0, 100);
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    historicTaskInstances);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询流程历史失败",
                    e.getMessage());
        }
    }

    // 根据流程id去查询历史记录
    @RequestMapping("/getInstanceByPiID")
    public AjaxResponse getInstanceByPiID(@RequestParam("piID") String piID) {
        try {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().desc()
                    .processInstanceId(piID)
                    .listPage(0, 100);
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    historicTaskInstances);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询流程历史失败",
                    e.getMessage());
        }
    }
}