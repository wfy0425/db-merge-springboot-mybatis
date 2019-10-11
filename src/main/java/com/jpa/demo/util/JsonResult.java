/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责封装controller返回的response
 *
 */
package com.jpa.demo.util;

public class JsonResult {
    private Object obj;
    private String msg;
    private Long code;


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}
