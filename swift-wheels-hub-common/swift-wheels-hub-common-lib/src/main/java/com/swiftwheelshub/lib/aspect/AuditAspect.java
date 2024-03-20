package com.swiftwheelshub.lib.aspect;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.lib.service.AuditLogProducerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "audit", name = "enabled")
@Slf4j
public class AuditAspect {

    private static final String USERNAME = "X-USERNAME";

    private final AuditLogProducerService auditLogProducerService;

    @Around("@annotation(LogActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogActivity logActivity = method.getAnnotation(LogActivity.class);

        log.info("Method called: " + signature);

        String username = getUsername();
        List<String> parametersValues = getParametersValues(joinPoint, logActivity, signature);

        AuditLogInfoRequest auditLogInfoRequest = getAuditLogInfoDto(method.getName(), username, parametersValues);

        try {
            Object proceed = joinPoint.proceed();
            auditLogProducerService.sendAuditLog(auditLogInfoRequest);

            return proceed;
        } catch (Throwable e) {
            throw new SwiftWheelsHubException(e);
        }
    }

    private String getUsername() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Optional.ofNullable(requestAttributes)
                .orElseThrow()
                .getRequest();

        return Optional.ofNullable(request.getHeader(USERNAME))
                .orElse(StringUtils.EMPTY);
    }

    private List<String> getParametersValues(ProceedingJoinPoint joinPoint, LogActivity logActivity,
                                             MethodSignature signature) {
        return Arrays.stream(logActivity.sentParameters())
                .map(parameter -> {
                    List<String> parameters = Arrays.asList(signature.getParameterNames());
                    int indexOfElement = parameters.indexOf(parameter);

                    if (indexOfElement < 0) {
                        return StringUtils.EMPTY;
                    }

                    return joinPoint.getArgs()[indexOfElement].toString();
                })
                .toList();
    }

    private AuditLogInfoRequest getAuditLogInfoDto(String methodName, String username, List<String> parametersValues) {
        return new AuditLogInfoRequest(methodName, username, parametersValues);
    }

}
