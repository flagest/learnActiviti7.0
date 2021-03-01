package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程定义
 *
 * @author jzwu
 * @since 2021/2/24 0024
 */
@SpringBootTest
public class Part2_ProcessDefination {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part2_ProcessDefination.class);
    @Resource
    private RepositoryService repositoryService;

    @Test
    public void initDeploymentBPMN() {

        List<ProcessDefinition> definitionList = repositoryService
                .createProcessDefinitionQuery().listPage(0, 100);
        for (ProcessDefinition processDefinition : definitionList) {
            LOGGER.info("----流程定义-----");
            LOGGER.info("Name :" + processDefinition.getName());
            LOGGER.info("key :" + processDefinition.getKey());
            LOGGER.info("ResourcesName :" + processDefinition.getResourceName());
            LOGGER.info("DeploymentId :" + processDefinition.getDeploymentId());
            LOGGER.info("version :" + processDefinition.getVersion());
        }
    }

    /*
     删除流程定义
     */
    @Test
    public void delDefinition() {
        String pid = "22ac1fa8-767c-11eb-b0cb-12a589c981f5";
        repositoryService.deleteDeployment(pid, false);
    }

}