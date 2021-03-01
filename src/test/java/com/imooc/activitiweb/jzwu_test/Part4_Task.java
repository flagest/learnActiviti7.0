package com.imooc.activitiweb.jzwu_test;


import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务测试4
 *
 * @author jzwu
 * @since 2021/2/25 0025
 */
@SpringBootTest
public class Part4_Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part4_Task.class);

    @Resource
    private TaskService taskService;

    /*
      任务查询(适用于管理员)
     */
    @Test
    public void getTask() {
        List<Task> taskList = taskService.createTaskQuery().listPage(0, 100);
        for (Task task : taskList) {
            LOGGER.info("task.getId()= {}", task.getId());
            LOGGER.info("task.getName()= {}", task.getName());
            LOGGER.info("task.getAssignee()= {}", task.getAssignee());
        }
    }

    /*
      查询自己的任务
     */
    @Test
    public void getTaskByAssignee() {
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee("bajie").listPage(0, 100);
        LOGGER.info("taskList.size()= {}", taskList.size());
        for (Task task : taskList) {
            LOGGER.info("task.getId()= {}", task.getId());
            LOGGER.info("task.getName()= {}", task.getName());
            LOGGER.info("task.getAssignee()= {}", task.getAssignee());
        }
    }

    /*
      执行任务
     */
    @Test
    public void completeTask() {
        taskService.complete("92e6a3f4-7736-11eb-b366-12a589c981f5");
        LOGGER.info("完成任务");
    }

    /*
      拾取任务
     */
    @Test
    public void claimTask() {
        // 这个要兼容SpringSecurity才能用这个接口
        //    taskService.createTaskQuery().taskCandidateUser("bajie");
        taskService.createTaskQuery().taskId("5b31d78d-773f-11eb-9d36-12a589c981f5").singleResult();
        taskService.claim("5b31d78d-773f-11eb-9d36-12a589c981f5", "bajie");
    }

    /*
      归还与交班任务
     */
    @Test
    public void setTaskAssignee() {
        taskService.createTaskQuery().taskId("5b31d78d-773f-11eb-9d36-12a589c981f5").singleResult();

        taskService.setAssignee("5b31d78d-773f-11eb-9d36-12a589c981f5", null);// 归还任务
        taskService.setAssignee("5b31d78d-773f-11eb-9d36-12a589c981f5", "wukong"); // 交班任务
    }
}