package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * 网关
 *
 * @author jzwu
 * @since 2021/2/28 0028
 */
@SpringBootTest
public class Part7_Gateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part7_Gateway.class);
    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Test
    public void initProcessInstance() {
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_Inclusive");
        LOGGER.info("流程实例ID = {}", processInstance.getProcessDefinitionId());

    }

    @Test
    public void completeTask() {

        taskService.complete("9ccb64e5-7997-11eb-b887-80a589c981f4");
//        taskService.complete("9ccb64e7-7997-11eb-b887-80a589c981f4");
        LOGGER.info("任务完成！");
    }


}