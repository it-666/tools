package com.wzb.tools.exception;

/**
 * 接口响应结果封装类
 *
 * @param <T>
 * @author 邬志斌 <br>
 * 2018年4月18日 上午11:02:27
 */
public class ResultBO<T> {

    /**
     * 在同一包中用到了
     */
    ResultBO() {
    }

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;


    /**
     * 返回的具体内容<br>
     * 有默认的初始值——空字符串"",
     * 因为阿里巴巴的fastjson处理数据时，不允许存在内容为null的字段
     */
    @SuppressWarnings("unchecked")
    private T data = (T) "";

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBO [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }


}
