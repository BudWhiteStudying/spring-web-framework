package com.budwhite.studying.framework.web.config;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.budwhite.studying.framework.web.model.dto.BaseResponse;
import com.budwhite.studying.framework.web.model.message.Message;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger applicationLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleExceptionIfUncaught(Exception exception, WebRequest request) {
        applicationLogger.error("Intercepted an uncaught exception", exception);
        return handleExceptionInternal(exception, new BaseResponse(Message.Error.GENERIC_INTERNAL_ERROR_MESSAGE), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    protected ResponseEntity<Object> handleResponseStatus(Exception exception, WebRequest request) {
        return handleExceptionInternal(exception, new BaseResponse(((ResponseStatusException) exception).getReason()), new HttpHeaders(), ((ResponseStatusException) exception).getStatus(), request);
    }

    // ConstraintViolationException is thrown when a @RequestParam/@RequestHeader param does not pass validation
    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(Exception exception, WebRequest request) {
        final StringBuilder errorMessageBuilder = new StringBuilder(Message.Error.GENERIC_MISSING_REQUEST_PARAMETERS_ERROR_MESSAGE);
        ((ConstraintViolationException) exception).getConstraintViolations().forEach(error -> {
            String propertyPath = error.getPropertyPath().toString();

            // Remove method name from property path
            int fieldNameIndex = propertyPath.indexOf(".") + 1;
            String fieldName = propertyPath.substring(fieldNameIndex);
            String fieldErrorMessage = error.getMessage();

            errorMessageBuilder.append(" - ").append(fieldName).append(": ").append(fieldErrorMessage);
        });
        String errorMessage = errorMessageBuilder.toString();

        applicationLogger.error("Error while validating {} --> {}", request, errorMessage);
        return super.handleExceptionInternal(exception, new BaseResponse(Message.Error.GENERIC_BAD_CLIENT_DATA_ERROR_MESSAGE), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // MissingServletRequestParameterException is thrown when a required @RequestParam is not provided
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        applicationLogger.error("{}", ex.getMessage());
        return super.handleExceptionInternal(ex, new BaseResponse(Message.Error.GENERIC_MISSING_REQUEST_PARAMETERS_ERROR_MESSAGE), headers, status, request);
    }

    // HttpMessageNotReadableException is thrown when the request body cannot be parsed because of errors
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        applicationLogger.error("{}", ex.getMessage());

        if (ex.getCause() instanceof InvalidFormatException)
            return super.handleExceptionInternal(ex, new BaseResponse(Message.Error.GENERIC_INCONSISTENT_CLIENT_DATA_ERROR_MESSAGE), headers, status, request);

        return super.handleExceptionInternal(ex, new BaseResponse(Message.Error.GENERIC_BAD_CLIENT_DATA_ERROR_MESSAGE), headers, status, request);
    }

    // MethodArgumentNotValidException is thrown when a @RequestBody param does not pass validation
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final StringBuilder errorMessageBuilder = new StringBuilder(Message.Error.API_REQUEST_BODY_VALIDATION_ERROR_MESSAGE);
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String fieldErrorMessage = error.getDefaultMessage();

            errorMessageBuilder.append(" - ").append(fieldName).append(": ").append(fieldErrorMessage);
        });
        String errorMessage = errorMessageBuilder.toString();

        applicationLogger.error("Error while validating {} --> {}", request, errorMessage);
        return super.handleExceptionInternal(exception, new BaseResponse(Message.Error.GENERIC_BAD_CLIENT_DATA_ERROR_MESSAGE), headers, status, request);
    }
}
