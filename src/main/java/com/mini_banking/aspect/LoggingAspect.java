package com.mini_banking.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.mini_banking.service_implementation..*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.mini_banking.controller..*(..))")
    public void controllerLayer() {}

    @Before("serviceLayer()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.info("Entering Method: {}", joinPoint.getSignature().getName());
        log.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        log.info("Method Executed Successfully: {}", joinPoint.getSignature().getName());
        log.info("Returned Value: {}", result);
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in Method: {}", joinPoint.getSignature().getName());
        log.error("Exception Message: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "controllerLayer()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Exception ex) {
        log.error("Controller Exception in {} : {}",
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}