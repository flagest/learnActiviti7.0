package com.imooc.activitiweb.jzwu_test;


import com.imooc.activitiweb.SecurityUtil;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jzwu
 * @since 2021/2/28 0028
 */
@SpringBootTest
public class Part8_ProcessRuntime {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part8_ProcessRuntime.class);
    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private SecurityUtil securityUtil;

    /*
      获取流程实例
     */
    @Test
    public void getProcessInstance() {
        securityUtil.logInAs("bajie");
        Page<ProcessInstance> processInstancePage = processRuntime.processInstances(Pageable.of(0, 100));
        int totalItems = processInstancePage.getTotalItems();
        LOGGER.info("启动流程实例 = {}", totalItems);
        List<ProcessInstance> content = processInstancePage.getContent();
        for (ProcessInstance processInstance : content) {
            LOGGER.info("------------------------");
            LOGGER.info("id = {}", processInstance.getId());
            LOGGER.info("Name = {}", processInstance.getName());
            LOGGER.info("StartDate = {}", processInstance.getStartDate());
            LOGGER.info("Status = {}", processInstance.getStatus());
            LOGGER.info("ProcessDefinitionId = {}", processInstance.getProcessDefinitionId());
            LOGGER.info("ProcessDefinitionKey = {}", processInstance.getProcessDefinitionKey());
        }
    }

    /*
      启动流程实例
     */
    @Test
    public void startProcessInstance() {
        securityUtil.logInAs("bajie");
        ProcessInstance processInstance = processRuntime.start(
                ProcessPayloadBuilder.start()
                        .withProcessDefinitionKey("myProcess_ProcessRuntime")
                        .withName("第一个流程实例名称")
                        .withBusinessKey("自定义key")
                        .build()
        );
    }

    /*
    删除流程实例
     */
    @Test
    public void delProcessInstance() {
        securityUtil.logInAs("bajie");
        processRuntime.delete(
                ProcessPayloadBuilder
                        .delete()
                        .withProcessInstanceId("ae3f7967-7a36-11eb-8698-80a589c981f4")
                        .build()
        );
    }

    /*
     挂起流程实例
     */
    @Test
    public void suspendProcessInstance() {
        securityUtil.logInAs("baijie");
        processRuntime.suspend(
                ProcessPayloadBuilder
                        .suspend()
                        .withProcessInstanceId("ae3f7967-7a36-11eb-8698-80a589c981f4")
                        .build()
        );
    }

    /*
      重启流程实例
     */
    @Test
    public void resumeProcessInstance() {
        securityUtil.logInAs("bajie");
        processRuntime
                .resume(ProcessPayloadBuilder
                        .resume()
                        .withProcessInstanceId("ae3f7967-7a36-11eb-8698-80a589c981f4")
                        .build()
                );
    }

    /*
    流程实例参数
     */
    @Test
    public void getVariables() {
        securityUtil.logInAs("bajie");
        List<VariableInstance> variables = processRuntime.variables(ProcessPayloadBuilder
                .variables()
                .withProcessInstanceId("ae3f7967-7a36-11eb-8698-80a589c981f4")
                .build()
        );
        for (VariableInstance variable : variables) {
            LOGGER.info("---------------");
            LOGGER.info("getName = {}",variable.getName());
            LOGGER.info("getTaskId = {}",variable.getTaskId());
            LOGGER.info("getProcessInstanceId = {}",variable.getProcessInstanceId());
        }
    }
}