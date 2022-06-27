package com.budwhite.studying.framework.web.config;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ApiLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(logger.isTraceEnabled()) {
            logger.trace("REQUEST - {} {} - headers: [{}]",      //TODO: add body
                    request.getMethod(),
                    request.getRequestURI(),
                    Collections.list(request.getHeaderNames())
                            .stream()
                            .map(headerName -> headerName + ":" + request.getHeader(headerName))
                            .collect(Collectors.joining(";")));

        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if(logger.isTraceEnabled()) {
            logger.trace("RESPONSE - {} {} - return-code: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus());
        }
    }
}
