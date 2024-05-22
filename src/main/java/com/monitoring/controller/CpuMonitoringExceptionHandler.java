package com.monitoring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class CpuMonitoringExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "값이 잘못되었거나 제공하지 않는 기간입니다.");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "값이 존재하지 않습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleConversionFailed(){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "올바르지 않은 값 형식입니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleParameter(){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "필요한 매개변수가 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e){
        log.error("서버 데이터 수집 오류 발생", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 데이터를 수집할 수 없습니다.");
    }


    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, String> result = new HashMap<>();
        result.put("code", String.valueOf(status.value()));
        result.put("message", message);
        return ResponseEntity.status(status).body(result);
    }
}
