package com.university.research.framework.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.research.common.annotation.Log;
import com.university.research.common.utils.SecurityUtils;
import com.university.research.system.domain.SysOperationLog;
import com.university.research.system.service.SysOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysOperationLogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.university.research.common.annotation.Log)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime now = LocalDateTime.now();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logAnn = signature.getMethod().getAnnotation(Log.class);

        SysOperationLog log = new SysOperationLog();
        log.setTitle(logAnn != null ? logAnn.title() : "");
        log.setBusinessType(logAnn != null ? logAnn.businessType() : 0);
        log.setOperatorType(logAnn != null ? logAnn.operatorType() : 0);
        log.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        log.setRequestMethod(request != null ? request.getMethod() : "");
        log.setRequestUrl(request != null ? request.getRequestURI() : "");
        log.setUserId(SecurityUtils.getUserId());
        log.setRealName(null);
        log.setDeptName(null);
        log.setOperationTime(now);

        try {
            String params = stringify(joinPoint.getArgs());
            log.setRequestParam(params);
            Object result = joinPoint.proceed();
            log.setStatus(0);
            log.setResponseResult(stringify(result));
            logService.insert(log);
            return result;
        } catch (Throwable ex) {
            log.setStatus(1);
            log.setErrorMsg(ex.getMessage());
            logService.insert(log);
            throw ex;
        }
    }

    private String stringify(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}