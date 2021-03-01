package com.imooc.activitiweb.jzwu_test;


import com.imooc.activitiweb.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试9
 *
 * @author jzwu
 * @since 2021/3/1 0001
 */
@SpringBootTest
public class Part9_TaskRuntime {
    private static final Logger LOGGER = LoggerFactory.getLogger(Part9_TaskRuntime.class);
    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private SecurityUtil securityUtil;

    @Test
    public void getTask() {
        securityUtil.logInAs("bajie");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
        int totalItems = tasks.getTotalItems();
        LOGGER.info("总的数据为 = {}", totalItems);
        List<Task> content = tasks.getContent();
        for (Task task : content) {
            LOGGER.info("task.id = {}", task.getId());
            LOGGER.info("task.getName() = {}", task.getName());
            LOGGER.info("task.getStatus() = {}", task.getStatus());
            LOGGER.info("task.getCreatedDate() = {}", task.getCreatedDate());
        }
    }

    @Test
    public void completeTask() {
        securityUtil.logInAs("bajie");
        Task task = taskRuntime.task("");
        if (task.getAssignee() == null) {
            LOGGER.info("Assignee: 待拾取任务");
            taskRuntime.claim(TaskPayloadBuilder
                    .claim()
                    .withTaskId(task.getId())
                    .build());
        } else {
            taskRuntime.complete(TaskPayloadBuilder
                    .complete()
                    .withTaskId(task.getId())
                    .build());
            LOGGER.info("Assignee: = {}", task.getAssignee());
        }
    }
}