package com.simplefanc.office.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author chenfan
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    //    @ResponseBody//需写到响应里面
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//响应状态码
    @ExceptionHandler(Exception.class)//Exception均可捕获
    public ResponseEntity<String> exceptionHandler(Exception e) {
        log.error("执行异常", e);
        //验证失败异常
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            //将错误信息返回给前台
            return new ResponseEntity<>(exception.getBindingResult().getFieldError().getDefaultMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            return exception.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof EmosException) {
            EmosException exception = (EmosException) e;
            return new ResponseEntity<>(exception.getMsg(), HttpStatus.INTERNAL_SERVER_ERROR);
//            return exception.getMsg();
        } else if (e instanceof UnauthorizedException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
//            return "你不具备相关权限";
        } else {
            return new ResponseEntity<>("后端执行异常", HttpStatus.INTERNAL_SERVER_ERROR);
//            return "后端执行异常";
        }
    }
}
