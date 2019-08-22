package com.xxl.job.console.controller;

import com.xxl.job.console.controller.annotation.PermissionLimit;
import com.xxl.job.console.config.XxlJobScheduler;
import com.xxl.job.core.biz.ConsoleBiz;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
public class JobApiController implements InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @RequestMapping(ConsoleBiz.MAPPING)
    @PermissionLimit(limit=false)
    public void api(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        XxlJobScheduler.invokeAdminService(request, response);
    }


}
