package com.carrental.lib.aspect;

import com.carrental.dto.AuditLogInfoDto;
import com.carrental.exception.CarRentalException;
import com.carrental.lib.security.jwt.JwtService;
import com.carrental.lib.service.AuditLogProducerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
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

    private final JwtService jwtService;
    private final AuditLogProducerService auditLogProducerService;

    @Around("@annotation(LogActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogActivity logActivity = method.getAnnotation(LogActivity.class);

        log.info("Method called: " + signature);

        String username = getUsername();
        List<String> parametersValues = getParametersValues(joinPoint, logActivity, signature);

        AuditLogInfoDto auditLogInfoDto = getAuditLogInfoDto(method.getName(), username, parametersValues);

        try {
            Object proceed = joinPoint.proceed();
            auditLogProducerService.sendAuditLog(auditLogInfoDto);

            return proceed;
        } catch (Throwable e) {
            throw new CarRentalException(e);
        }
    }

    private String getUsername() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Optional.ofNullable(requestAttributes).orElseThrow().getRequest();

        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(authenticationToken -> jwtService.extractUsername(authenticationToken.substring(7)))
                .orElse(StringUtils.EMPTY);
    }

    private List<String> getParametersValues(ProceedingJoinPoint joinPoint, LogActivity logActivity,
                                             MethodSignature signature) {
        return Arrays.stream(logActivity.sentParameters())
                .map(parameter -> {
                    List<String> parameters = Arrays.asList(signature.getParameterNames());

                    return joinPoint.getArgs()[parameters.indexOf(parameter)].toString();
                })
                .toList();
    }

    private AuditLogInfoDto getAuditLogInfoDto(String methodName, String username, List<String> parametersValues) {
        return new AuditLogInfoDto(methodName, username, parametersValues);
    }

}
