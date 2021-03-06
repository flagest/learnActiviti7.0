package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.SecurityUtil;
import com.imooc.activitiweb.pojo.UserInfoBean;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例控制类
 *
 * @author jzwu
 * @since 2021/3/4 0004
 */
@RestController
@RequestMapping("/processInstance")
public class ProcessInstanceController {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private SecurityUtil securityUtil;

    @Resource
    private ProcessRuntime processRuntime;

    // 获取流程实例
    @GetMapping
    public AjaxResponse getInstance(@AuthenticationPrincipal UserInfoBean userInfoBean) {
        try {
            Page<ProcessInstance> processInstancePage = processRuntime
                    .processInstances(Pageable.of(0, 100));
            List<ProcessInstance> content = processInstancePage.getContent();
            content.sort((y, x) -> x.getStartDate().toString().compareTo(y.getStartDate().toString()));
            List<Map<String, Object>> listMaps = new ArrayList<>();
            for (ProcessInstance processInstance : content) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", processInstance.getId());
                hashMap.put("name", processInstance.getName());
                hashMap.put("status", processInstance.getStatus());
                hashMap.put("processDefinition", processInstance.getProcessDefinitionId());
                hashMap.put("startDate", processInstance.getStartDate());
                hashMap.put("processDefinitionVersion", processInstance.getProcessDefinitionVersion());
                // 因为 processInstance里面没有历史高亮需要的deploymentID和资源name，所以需要再次查询
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processInstance.getProcessDefinitionId())
                        .singleResult();
                hashMap.put("deploymentId", processDefinition.getDeploymentId());
                hashMap.put("resourceName", processDefinition.getResourceName());
                listMaps.add(hashMap);

            }
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listMaps);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.ERROR.getDesc(),
                    e.getMessage());
        }
    }

    // 启动流程实例
    @GetMapping("/startProcess")
    public AjaxResponse startProcess(@RequestParam("processDefinitionKey") String processDefinitionKey,
                                     @RequestParam("instanceName") String instanceName) {
        try {
            ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start()
                    .withProcessDefinitionKey(processDefinitionKey)
                    .withName(instanceName)
                    .withBusinessKey("自定义BusinessKey")
                    .build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.ERROR.getDesc(),
                    e.getMessage());
        }
    }

    // 挂起流程实例
    @GetMapping("/suspendInstance")
    public AjaxResponse suspendInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder.suspend()
                    .withProcessInstanceId(instanceID).build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    processInstance.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "挂起流是失败",
                    e.getMessage());
        }
    }

    // 激活流程实例
    @GetMapping("/resumeInstance")
    public AjaxResponse resumeInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder.resume()
                    .withProcessInstanceId(instanceID).build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    processInstance.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "激活流程失败",
                    e.getMessage());
        }
    }

    // 删除流程实例
    @DeleteMapping
    public AjaxResponse deleteInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance delete = processRuntime.delete(ProcessPayloadBuilder.delete()
                    .withProcessInstanceId(instanceID).build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "删除流程失败",
                    e.getMessage());
        }
    }

    // 查询流程参数
    @GetMapping("/variables")
    public AjaxResponse variableInstance(@RequestParam("instanceID") String instanceID) {
        try {
            List<VariableInstance> variableInstances = processRuntime.variables(ProcessPayloadBuilder.variables()
                    .withProcessInstanceId(instanceID).build());
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    variableInstances);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.ERROR.getCode(),
                    "查询流程参数失败",
                    e.getMessage());
        }
    }
}