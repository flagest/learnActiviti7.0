package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 历史数据测试
 *
 * @author jzwu
 * @since 2021/2/25 0025
 */
@SpringBootTest
public class Part5_HistorcTaskInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part5_HistorcTaskInstance.class);
    @Resource
    private HistoryService historyService;

    /*
     根据用户名查询历史任务
     */
    @Test
    public void historicTaskInstanceByUser() {
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .taskAssignee("bajie").listPage(0, 100);
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            LOGGER.info("historicTaskInstances.getId= {}", historicTaskInstance.getId());
            LOGGER.info("historicTaskInstances.getParentTaskId= {}", historicTaskInstance.getParentTaskId());
            LOGGER.info("historicTaskInstances.getName = {}", historicTaskInstance.getName());
        }
    }

    /*
     根据流程实例id查询历史
     */
    @Test
    public void historicTaskInstanceByPiID() {
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId("a673d07d-7732-11eb-9e12-12a589c981f5")
                .listPage(0, 100);
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            LOGGER.info("historicTaskInstances.getId= {}", historicTaskInstance.getId());
            LOGGER.info("historicTaskInstances.getParentTaskId= {}", historicTaskInstance.getParentTaskId());
            LOGGER.info("historicTaskInstances.getName = {}", historicTaskInstance.getName());
        }
    }
}