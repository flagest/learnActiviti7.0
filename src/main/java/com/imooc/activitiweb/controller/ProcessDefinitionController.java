package com.imooc.activitiweb.controller;


import com.google.common.collect.Maps;
import com.imooc.activitiweb.util.AjaxResponse;
import com.imooc.activitiweb.util.GlobaConfig;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author jzwu
 * @since 2021/3/3 0003
 */
@RestController
@RequestMapping("/processDefinition")
public class ProcessDefinitionController {

    @Resource
    private RepositoryService repositoryService;

    // 添加流程定义通过上传bpmn
    @PostMapping
    public AjaxResponse uploadStreamAndDeployment(@RequestParam("multipartFile") MultipartFile multipartFile,
                                                  @RequestParam("deploymentName") String deploymentName) {

        String originalFilename = multipartFile.getOriginalFilename();
        String extFile = FilenameUtils.getExtension(originalFilename);
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Deployment deployment = null;
        if ("zip".equals(extFile)) {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            deployment = repositoryService.createDeployment()
                    .addZipInputStream(zipInputStream)
                    .name(deploymentName)
                    .deploy();
        } else {
            deployment = repositoryService.createDeployment()
                    .addInputStream(originalFilename, inputStream)
                    .name(deploymentName)
                    .deploy();
        }
        return AjaxResponse.AjaxData(
                GlobaConfig.ResponseCode.SUCCESS.getCode(),
                GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                deployment.getId() + ";" + originalFilename
        );
    }

    // 添加流程定义通过在线提交BPMN的XML
    @PostMapping("/addDeploymentByString")
    public AjaxResponse addDeploymentByString(@RequestParam("multipartFile") String stringBPMN,
                                              @RequestParam("deploymentName") String deploymentName) {

        try {
            Deployment deploy = repositoryService.createDeployment()
                    .addString("CreateWithBPMJS.bpmn", stringBPMN)
                    .name(deploymentName)
                    .deploy();
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    deploy.getId() + ";" + deploymentName
            );
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    e.getMessage()
            );
        }
    }

    // 获取流程定义列表
    @GetMapping
    public AjaxResponse getDefinitions() {
        List<Map<String, Object>> listMap = new ArrayList<>();
        try {
            Map<String, Object> hashMap = Maps.newHashMap();
            List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery()
                    .listPage(0, 100);
            for (ProcessDefinition processDefinition : definitionList) {
                hashMap.put("name", processDefinition.getName());
                hashMap.put("key", processDefinition.getKey());
                hashMap.put("resourcename", processDefinition.getResourceName());
                hashMap.put("deploymentid", processDefinition.getDeploymentId());
                hashMap.put("version", processDefinition.getVersion());
                listMap.add(hashMap);
            }
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listMap
            );
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.ERROR.getDesc(),
                    e.toString()
            );
        }
    }

    // 获取流程定义XML
    @GetMapping("/getdefinitionxml")
    public void getDefinitionXML(HttpServletResponse response, @RequestParam("deploymentID") String deploymentID,
                                 @RequestParam("resourceName") String resourceName) {
        try {
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentID, resourceName);
            int count = inputStream.available();
            byte[] bytes = new byte[count];
            response.setContentType("text/xml");
            ServletOutputStream outputStream = response.getOutputStream();
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取流程部署里列表
    @GetMapping("/getDeployments")
    public AjaxResponse getDeployments() {
        try {
            List<Map<String, Object>> listMaps = new ArrayList<>();
            List<Deployment> deployments = repositoryService.createDeploymentQuery()
                    .listPage(0, 100);
            for (Deployment deployment : deployments) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", deployment.getId());
                hashMap.put("Name", deployment.getName());
                hashMap.put("DeploymentTime", deployment.getDeploymentTime());
                listMaps.add(hashMap);
            }
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    listMaps);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.ERROR.getDesc(),
                    e.toString());
        }
    }

    // 删除流程定义
    @DeleteMapping("/delDelfinition")
    public AjaxResponse DeleteMapping(@RequestParam("pdID") String pdID) {
        try {
            repositoryService.deleteDeployment(pdID);
            return AjaxResponse.AjaxData(GlobaConfig.ResponseCode.SUCCESS.getCode(),
                    GlobaConfig.ResponseCode.SUCCESS.getDesc(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.AjaxData(
                    GlobaConfig.ResponseCode.ERROR.getCode(),
                    GlobaConfig.ResponseCode.ERROR.getDesc(),
                    e.toString());
        }
    }
}