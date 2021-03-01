package com.imooc.activitiweb.controller;


import com.imooc.activitiweb.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private SecurityUtil securityUtil;


    @Autowired
    private ProcessRuntime processRuntime;


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String say() {
        return "你好";
    }


}


