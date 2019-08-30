package com.xxl.job.console.controller;

import com.xxl.job.console.config.XxlJobScheduler;
import com.xxl.job.core.biz.ConsoleBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
@Api(tags="Api")
@RestController
public class ApiController implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @ApiOperation(value = "api", httpMethod = "GET")
    @RequestMapping(ConsoleBiz.MAPPING)
    public void api(HttpServletRequest request, HttpServletResponse response) {
        try {
            XxlJobScheduler.invokeAdminService(request, response);
        } catch (Exception e) {
            logger.error("api", e);
        }
    }
}
