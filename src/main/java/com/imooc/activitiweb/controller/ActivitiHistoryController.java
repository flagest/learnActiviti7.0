package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.pojo.UserInfoBean;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private RepositoryService repositoryService;

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

    // 高亮显示流程实例
    @GetMapping("/getHighLine")
    public AjaxResponse getHighLine(@RequestParam("instanceID") String instanceID, @AuthenticationPrincipal UserInfoBean userInfoBean) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceID).singleResult();
            // 读取BPM
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            Process process = bpmnModel.getProcesses().get(0);
            // 获取所有流程FlowElement的信息
            Collection<FlowElement> flowElements = process.getFlowElements();
            HashMap<String, String> map = new HashMap<>();
            for (FlowElement flowElement : flowElements) {
                // 判断是否是线条
                if (flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    String sourceRef = sequenceFlow.getSourceRef();
                    String targetRef = sequenceFlow.getTargetRef();
                    map.put(sourceRef + targetRef, sequenceFlow.getId());
                }
            }

            // 获取全部历史流程
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(instanceID).listPage(0, 100);
            // 各历史节点两两组合为key
            Set<String> keyList = new HashSet<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                for (HistoricActivityInstance s : historicActivityInstances) {
                    if (historicActivityInstance != s) {
                        keyList.add(historicActivityInstance.getActivityId() + s.getActivityId());
                    }
                }
            }

            // 高亮联系ID
            Set<String> hightLine = new HashSet<>();
            keyList.forEach(s -> hightLine.add(map.get(s)));
            // 获取已经完成的节点
            List<HistoricActivityInstance> listFinshed = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(instanceID).finished().list();
            // 已经完成的节点高亮
            Set<String> highPoint = new HashSet<>();
            listFinshed.forEach(s -> highPoint.add(s.getActivityId()));

            // 获取待办节点
            List<HistoricActivityInstance> listUnFinshed = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(instanceID).unfinished().list();
            // 待办高连节点
            Set<String> watingTODO = new HashSet<>();
            listUnFinshed.forEach(item -> watingTODO.add(item.getActivityId()));

            // 当前登录人姓名
            String assigneeName = userInfoBean.getUsername();
            List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(assigneeName)
                    .processInstanceId(instanceID)
                    .finished()
                    .list();
            // 当前用户完成任务
            Set<String> iDO = new HashSet<>();
            taskInstanceList.forEach(item -> iDO.add(item.getTaskDefinitionKey()));
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("hightLine",hightLine);
            hashMap.put("highPoint",highPoint);
            hashMap.put("watingTODO",watingTODO);
            hashMap.put("iDO",iDO);

            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    hashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询高亮流程历史失败",
                    e.getMessage());
        }
    }
}