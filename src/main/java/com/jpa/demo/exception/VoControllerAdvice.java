package com.jpa.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//控制器通知
@ControllerAdvice(
		// 指定拦截包的控制器
		basePackages = { "com.jpa.demo.controller.*" },
		// 限定被标注为@Controller或者@RestController的类才被拦截
		annotations = { Controller.class, RestController.class })
public class VoControllerAdvice {
	// 异常处理，可以定义异常类型进行拦截处理
	@ExceptionHandler(value = NotFoundException.class)
	// 以JSON表达方式响应
	@ResponseBody
	// 定义为服务器错误状态码
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, Object> notFoundExceptionHandler(HttpServletRequest request, NotFoundException ex) {
		Map<String, Object> msgMap = new HashMap<>();
		// 获取异常信息
		msgMap.put("code", ex.getCode());
		msgMap.put("message", ex.getCustomMsg());
		return msgMap;
	}

    // 异常处理，可以定义异常类型进行拦截处理
    @ExceptionHandler(value = BadRequestException.class)
    // 以JSON表达方式响应
    @ResponseBody
    // 定义为服务器错误状态码
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequestExceptionHandler(HttpServletRequest request, BadRequestException ex) {
        Map<String, Object> msgMap = new HashMap<>();
        // 获取异常信息
        msgMap.put("code", ex.getCode());
        msgMap.put("message", ex.getCustomMsg());
        return msgMap;
    }
}
