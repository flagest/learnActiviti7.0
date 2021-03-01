package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程测试发布
 *
 * @author jzwu
 * @since 2021/2/24 0024
 */
@SpringBootTest
public class Part1Deployment {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part1Deployment.class);

    @Resource
    private RepositoryService repositoryService;

    /*
      通过bpmn部署流程
     */
    @Test
    public void initDeployMeneBPMN() {
        String fileName = "BPMN/jzwu_test/Part8_ProcessRun.bpmn";
//        String picName = "BPMN/Part1_Deployment.png";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(fileName) // 文件
//                .addClasspathResource(picName)  // 图片
                .name("流程部署测试RunTime").deploy();
        LOGGER.info("deployName = {}", deployment.getName());
    }

    /*
    通过ZIP部署流程
     */
    @Test
    public void initDeployMentZip() {
        InputStream resourceAsStream = this.getClass().getClassLoader()
                .getResourceAsStream("BPMN/Part1_DeploymentV2.zip");
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);

        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream).name("流程部署测试ZIP")
                .deploy();
        LOGGER.info("BPMN流程部署测试ZIP = {}", deploy.getName());
    }

    /*
    查询流程定义部署
     */
    @Test
    public void getDepolys() {
        List<Deployment> deployments = repositoryService.createDeploymentQuery().listPage(0, 100);
        for (Deployment deployment : deployments) {
            LOGGER.info("deployment.getId= {}", deployment.getId());
            LOGGER.info("deployment.getName= {}", deployment.getName());
            LOGGER.info("deployment.getDeploymentTime= {}", deployment.getDeploymentTime());
            LOGGER.info("deployment.getKey= {}", deployment.getKey());
        }
    }
}