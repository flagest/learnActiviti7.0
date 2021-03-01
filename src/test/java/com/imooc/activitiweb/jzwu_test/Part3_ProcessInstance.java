package com.imooc.activitiweb.jzwu_test;



import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程实例
 *
 * @author jzwu
 * @since 2021/2/25
 */
@SpringBootTest
public class Part3_ProcessInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part3_ProcessInstance.class);

    @Resource
    private RuntimeService runtimeService;

    /*
     初始化流程实例
     */
    @Test
    public void initProcessInstance() {
        /*
          businesssKey 参数是用来携带业务信息
          1,获取页面表单填报的类容，请假时间，请假事由，String fromData
          2,formData写入业务表,返回业务表主键ID==businessKey
          3,把业务数据与Activiti7流程数据关联
        */
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_ProcessRuntime", "bKey006");
        LOGGER.info("流程实例 = {}" + processInstance.getProcessInstanceId());

    }

    /*
      获取流程实例
     */
    @Test
    public void getProcessInstance() {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .listPage(0, 100);
        for (ProcessInstance processInstance : processInstances) {
            LOGGER.info("-----流程实例-------");
            LOGGER.info("processInstances = {}" + processInstance.getProcessInstanceId());
            LOGGER.info("processInstances = {}" + processInstance.getProcessDefinitionId());
            LOGGER.info("processInstances = {}" + processInstance.isEnded());
            LOGGER.info("processInstances = {}" + processInstance.isSuspended());
            LOGGER.info("processInstances" + processInstance.toString());
        }
    }

    /*
      激活流程实例
     */
    @Test
    public void activationProcessInstance() {
        runtimeService.activateProcessInstanceById("6090eaa9-7508-11eb-b46f-12a589c981f5");
        LOGGER.info("激活流程实例");
    }

    /*
      暂停流程实例
     */
    @Test
    public void suspendProcessInstance() {
        runtimeService.suspendProcessInstanceById("6090eaa9-7508-11eb-b46f-12a589c981f5");
        LOGGER.info("流程挂起");
    }
    /*
      删除流程实例
     */
    @Test
    public void delProcessInstance() {
        //0fba3ee3-7713-11eb-98b5-12a589c981f5,35b5c16f-7713-11eb-9531-12a589c981f5
        // ,6b61618c-7713-11eb-97b0-12a589c981f5,c9e83a86-770a-11eb-a6ce-12a589c981f5
        runtimeService.deleteProcessInstance("0fba3ee3-7713-11eb-98b5-12a589c981f5", "打卡删除");
    }
}