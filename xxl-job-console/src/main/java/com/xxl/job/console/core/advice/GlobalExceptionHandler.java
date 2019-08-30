package com.xxl.job.console.core.advice;

import com.xxl.job.console.model.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-06-25
 */
@Component
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(HttpServletRequest req, Exception e) {
        BaseResponse result = new BaseResponse();
        String errorMsg = (e.getMessage() == null) ? e.getClass().getSimpleName() : e.getMessage();
        result.Fail(errorMsg);

        logger.debug(errorMsg,e);

        return ResponseEntity.status(500).body(result);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpServletRequest req, HttpMessageNotReadableException e){
        BaseResponse result = new BaseResponse();
        String errorMsg = "请求参数不匹配";
        result.Fail(errorMsg,HttpStatus.BAD_REQUEST.value());

        logger.debug(errorMsg, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler({TypeMismatchException.class})
    public ResponseEntity requestTypeMismatch(HttpServletRequest req, TypeMismatchException e){
        BaseResponse result = new BaseResponse();

        String errorMsg = "参数类型不匹配：参数" + e.getPropertyName() + "类型应该为" + e.getRequiredType();

        result.Fail(errorMsg,HttpStatus.BAD_REQUEST.value());

        logger.debug(errorMsg, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity requestMissingServletRequest(HttpServletRequest req, MissingServletRequestParameterException e){
        BaseResponse result = new BaseResponse();
        String errorMsg = "缺少必要参数：" + e.getParameterName();

        result.Fail(errorMsg,HttpStatus.BAD_REQUEST.value());

        logger.debug(errorMsg, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        BaseResponse result = new BaseResponse();
        String errorMsg = StringUtils.collectionToDelimitedString(errorMessages, ",");
        result.Fail(errorMsg,HttpStatus.BAD_REQUEST.value());

        logger.debug(errorMsg,e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        List<String> errorMessages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        BaseResponse result = new BaseResponse();
        String errorMsg = StringUtils.collectionToDelimitedString(errorMessages, ",");
        result.Fail(errorMsg, 400);

        logger.debug(errorMsg, e);

        return ResponseEntity.status(400).body(result);
    }
}
