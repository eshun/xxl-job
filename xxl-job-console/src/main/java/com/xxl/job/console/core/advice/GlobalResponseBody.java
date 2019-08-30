package com.xxl.job.console.core.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author esun
 */
@ControllerAdvice
public class GlobalResponseBody implements ResponseBodyAdvice<Object> {
    private static Logger logger = LoggerFactory.getLogger(GlobalResponseBody.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        String methodName = methodParameter.getMethod().getName();
        //springfox.documentation.swagger.web.UiConfiguration
        String methods = "uiConfiguration,swaggerResources,getDocumentation";
        if (methods.contains(methodName)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

//        BaseResponse result = new BaseResponse();
//        if (body instanceof Map) {
//            try {
//                if (body instanceof LinkedHashMap) {
//                    Map<String, Object> data = (LinkedHashMap) body;
//                    if (data.containsKey("error") && data.containsKey("status")) {
//                        result.Fail(data.get("message").toString(), (int) data.get("status"));
//                        return result;
//                    }
//                }
//            } catch (Exception e) {
//                logger.error("beforeBodyWrite", body);
//                result.Fail(e.getMessage());
//                return result;
//            }
//        } else if (body instanceof BaseResponse) {
//            return body;
//        } else {
//
//        }
//        result.Success(body);
//        if (body instanceof String || body == null) {
//
//            //HttpServletResponse response = (HttpServletResponse) serverHttpResponse;
//            //response.setCharacterEncoding("UTF-8");
//            //response.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
//
//            //return result;
//        }
        return body;
    }
}
