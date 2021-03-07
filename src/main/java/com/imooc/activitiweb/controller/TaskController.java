package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工作任务接口
 *
 * @author jzwu
 * @since 2021/3/6 0006
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private RepositoryService repositoryService;

    // 获取我的待办任务
    @GetMapping("/getTasks")
    public AjaxResponse getTasks() {
        try {
            // tasks接口中获取SpringSecurity中登录用户
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
            int count = tasks.getTotalItems();
            List<Task> taskList = tasks.getContent();
            ArrayList<HashMap<String, Object>> listHashMap = new ArrayList<>();
            for (Task task : taskList) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", task.getId());
                hashMap.put("name", task.getName());
                hashMap.put("status", task.getStatus());
                hashMap.put("createDate", task.getCreatedDate());
                // 这里的assignee是候选人或者当前登录人
                if (StringUtils.isEmpty(task.getAssignee())) {
                    hashMap.put("assignee", "待拾取任务");
                } else {
                    hashMap.put("assignee", task.getAssignee());
                }
                // 获取流程实例
                ProcessInstance processInstance = processRuntime.processInstance(task.getProcessInstanceId());
                hashMap.put("instanceName", processInstance.getName());
            }
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listHashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询流程实例失败",
                    e.getMessage());
        }
    }

    // 完成待办任务
    @GetMapping("/completeTask")
    public AjaxResponse completeTask(@RequestParam("taskID") String taskID) {
        try {
            Task task = taskRuntime.task(taskID);
            if (StringUtils.isEmpty(task.getAssignee())) {
                // 拾取任务
                Task claim = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
            }
            // 完成任务
            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId())
                    //                .withVariable("date", LocalDateTime.now())
                    .build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "完成任务失败",
                    e.getMessage());
        }

    }

    //  渲染动态表单
    @GetMapping("/fromDataShow")
    public AjaxResponse fromDataShow(@RequestParam("PiID") String PiID) {
        try {
            Task task = taskRuntime.task(PiID);
            UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
                    .getFlowElement(task.getFormKey());
            if (StringUtils.isEmpty(userTask)) {
                return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                        GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                        "无表单");
            }
            List<FormProperty> formProperties = userTask.getFormProperties();
            ArrayList<HashMap<String, Object>> listMap = new ArrayList<>();
            for (FormProperty formProperty : formProperties) {
                String[] splitString = formProperty.getId().split("-_!");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", splitString[0]);
                hashMap.put("contorlType", splitString[1]);
                hashMap.put("controlLable", splitString[2]);
                hashMap.put("controlDefvalue", splitString[3]);
                hashMap.put("controlParm", splitString[4]);
                listMap.add(hashMap);
            }
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "渲染动态表单失败",
                    e.getMessage());
        }
    }

    // 保存动态表单
}