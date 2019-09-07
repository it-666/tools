package com.wzb.tools.exception;

/**
 * @author 邬志斌 <br>
 * 2018年5月22日 上午9:53:07
 */
public class PageBO<T> {

    PageBO() {
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
     * 分页个数
     */
    private Integer count;

    /**
     * 返回的具体内容<br>
     * 有默认的初始值——空字符串"",
     * 因为阿里巴巴的fastjson处理数据时，不允许存在内容为null的字段
     */
    @SuppressWarnings("unchecked")
    private T data = (T) "";

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PageBO [code=" + code + ", msg=" + msg + ", count=" + count + ", data=" + data + "]";
    }


}
