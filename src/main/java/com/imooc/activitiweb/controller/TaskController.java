package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.mapper.FormDataMapper;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作任务接口
 *
 * @author jzwu
 * @since 2021/3/6 0006
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private FormDataMapper formDataMapper;

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
                listHashMap.add(hashMap);
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
    public AjaxResponse fromDataShow(@RequestParam("taskID") String taskID) {
        try {
            Task task = taskRuntime.task(taskID);
            // 构建表单历史数据字典
            HashMap<String, Object> controlerListMap = new HashMap<>();
            List<HashMap<String, String>> hashMaps = formDataMapper.selectFormData(task.getProcessInstanceId());
            for (HashMap<String, String> hashMap : hashMaps) {
                controlerListMap.put(hashMap.get("Control_ID_").toString(), hashMap.get("Control_VALUE_").toString());
            }
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
//                hashMap.put("controlDefvalue", splitString[3]);
                if (splitString[3].startsWith("FormProperty_")) {
                    if (controlerListMap.containsKey(splitString[3])) {
                        hashMap.put("controlDefvalue", controlerListMap.get(splitString[3]));
                    } else {
                        hashMap.put("controlDefvalue", "读取失败，检查" + splitString[0] + "配置");
                    }
                } else {
                    hashMap.put("controlDefvalue", splitString[3]);
                }
                hashMap.put("controlParam", splitString[4]);
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "渲染动态表单失败",
                    e.toString());
        }
    }

    // 保存动态表单
    @Transactional
    @PostMapping("/fromDataSave")
    public AjaxResponse fromDataSave(@RequestParam("taskID") String taskID,
                                     @RequestParam("formData") String formData) {
        try {
            Map<String, Object> values = new HashMap<>();
            boolean hasValues = false;
            Task task = taskRuntime.task(taskID);
            List<HashMap<String, Object>> listHashMaps = new ArrayList<>();
            String[] formDataSplit = formData.split("!_!");
            for (String stringFormData : formDataSplit) {
                String[] stringSplit = stringFormData.split("-_!");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
                hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
                hashMap.put("FORM_KEY_", task.getFormKey());
                hashMap.put("Control_ID_", stringSplit[0]);
                hashMap.put("Control_VALUE_", stringSplit[1]);
                listHashMaps.add(hashMap);
                /* 完成任务 */
                this.isHasValues(taskID, values, hasValues, stringSplit);
            }
            formDataMapper.insertFormData(listHashMaps);
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listHashMaps);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "保存动态失败",
                    e.toString());
        }
    }

    private void isHasValues(String taskID, Map<String, Object> values, boolean hasValues, String[] stringSplit) throws ParseException {
        switch (stringSplit[2]) {
            case "f":
                log.info("控件值不作为参数");
                break;
            case "s":
                values.put(stringSplit[0], stringSplit[1]);
                hasValues = true;
                break;
            case "t":
                values.put(stringSplit[0], new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(stringSplit[1]));
                hasValues = true;
                break;
            case "b":
                values.put(stringSplit[0], BooleanUtils.toBoolean(stringSplit[1]));
                hasValues = true;
                break;
            default:
                log.info("控件类型" + stringSplit[0] + ";" + "的参数" + stringSplit[1] + "不存在");
        }
        if (hasValues) {
            taskRuntime.complete(TaskPayloadBuilder.complete()
                    .withTaskId(taskID).withVariables(values).build());
        } else {
            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID).build());
        }
    }
}