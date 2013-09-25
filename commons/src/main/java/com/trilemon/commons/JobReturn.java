package com.trilemon.commons;

/**
 * @author kevin
 */
public enum JobReturn {

    SUCCESSFUL("001", "任务执行完毕"),
    FAIL("002", "任务执行失败"),
    DOING("003", "正在执行任务");
    private String msg;
    private String code;

    private JobReturn(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
