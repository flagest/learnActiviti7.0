package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试UEL
 *
 * @author jzwu
 * @since 2021/2/25 0025
 */
@SpringBootTest
public class Part6_UEL {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part6_UEL.class);

    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    /*
      启动流程实例带参数，执行执行人
     */
    @Test
    public void initProcessInstanceWithArgs() {
        Map<String, Object> var = new HashMap<>();
        var.put("ZhiXingRen", "wokong");

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_UEL_V1", "bKey002", var);
        LOGGER.info("流程实例ID = {}", processInstance.getProcessDefinitionId());
    }

    /*
       完成带参数,指定流程变量测试
     */
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> var = new HashMap<>();
        var.put("pay", "101");
        taskService.complete("484998af-77e9-11eb-921d-12a589c981f5", var);
        LOGGER.info("任务完成！");
    }

    /*
     启动实例带参数，使用实体类
     */
    @Test
    public void initProcessInstanceWithClassArgs() {
        UEL_POJO uel_pojo = new UEL_POJO();
        uel_pojo.setZhixingren("bajie");
        // 流程变量
        Map<String, Object> var = new HashMap<>();
        var.put("uelpojo", uel_pojo);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_uelv3", "bKey002", var);
        LOGGER.info("流程实例ID = {}", processInstance.getProcessDefinitionId());
    }

    /*
      任务完成环节带参数，指定多个是候选人
     */
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        Map<String, Object> var = new HashMap<>();
        var.put("houxuanren", "wukong,tangseng");
        taskService.complete("f29637bf-7811-11eb-ad6a-12a589c981f5", var);
        LOGGER.info("完成任务！");

    }

    /*
     直接指定变量
     */
    @Test
    public void otherArgs() {
        runtimeService.setVariable("f29637bf-7811-11eb-ad6a-12a589c981f5","pay","101");


    }

    /*
     局部变量
     */
    @Test
    public void otherLocalArgs() {
//        runtimeService.setVariablesLocal("f29637bf-7811-11eb-ad6a-12a589c981f5","pay","101");

    }
}