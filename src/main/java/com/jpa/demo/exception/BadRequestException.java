package com.jpa.demo.exception;

public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	// 异常编码
	private Long code;
	// 异常自定义信息
	private String customMsg;

	public BadRequestException() {
	}

	public BadRequestException(Long code, String customMsg) {
		super();
		this.code = code;
		this.customMsg = customMsg;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getCustomMsg() {
		return customMsg;
	}

	public void setCustomMsg(String customMsg) {
		this.customMsg = customMsg;
	}

}