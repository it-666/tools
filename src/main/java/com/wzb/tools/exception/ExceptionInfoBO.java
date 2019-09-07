package com.wzb.tools.exception;

/**
 * 异常信息基础对象，包含 异常编号code和 异常信息msg 两个属性，
 * 开发人员将异常信息封装成该对象，放在 {@link  OrgExceptionInfoEnum } 中，
 * 供OrgException创建异常对象时调用
 *
 * @author 邬志斌 <br>
 * 2018年4月18日 下午2:40:23
 */
public class ExceptionInfoBO {

    /**
     * 异常编号
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String msg;
    private Integer count;

    /**
     * 构造器，通过带参数的构造器实例化对象
     *
     * @param code
     * @param msg
     */
    public ExceptionInfoBO(Integer code, String msg) {
        if (code == null) {
            code = -1;
        }
        if (msg == null) {
            msg = "未定义异常";
        }
        this.code = code;
        this.msg = msg;
    }

    public ExceptionInfoBO(Integer code, String msg, Integer count) {
        super();
        this.code = code;
        this.msg = msg;
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
