package com.wzb.tools.exception;

/**
 * 接口响应结果处理工具，
 *
 * @author 邬志斌 <br>
 * 2018年4月18日 上午11:06:25
 */
public class ResultTool {

    /**
     * 不允许构造该对象
     */
    private ResultTool() {
    }

    /**
     * 成功时调用，向调用者返回指定格式的数据
     * <br>2018年4月18日上午11:22:40
     */
    public static ResultBO<Object> success(Object obj) {
        if (obj == null) {
            obj = "";
        }
        ResultBO<Object> r = new ResultBO<Object>();
        r.setCode(0);
        r.setMsg("成功");
        r.setData(obj);
        return r;
    }

    /**
     * 成功时调用，调用者不需要数据
     * <br> 2018年4月18日上午11:24:15
     */
    public static ResultBO<Object> success() {
        return success(null);
    }

    /**
     * 失败时调用<br>
     * （传入参数为null时，会自动设置 code为2，msg为"未定义异常"）<br>
     * 2018年4月18日上午11:27:45
     */
    public static ResultBO<Object> error(ExceptionInfoBO em) {
        ResultBO<Object> r = new ResultBO<>();
        if (em == null) {
            r.setCode(-1);
            r.setMsg("未定义异常");
            return r;
        }
        r.setCode(em.getCode());
        r.setMsg(em.getMsg());
        return r;
    }

    /**
     * 异常处理器中捕获到异常时调用<br>
     * （参数code为null时会设置 code为-1，
     * msg为null时会设置msg为"未定义异常",
     * msg为空内容时会设置msg为"未定义异常"）
     * <br> 2018年4月20日上午10:09:12
     */
    static ResultBO<Object> error(Integer code, String msg) {
        if (code == null) {
            code = -1;
        }
        if (msg == null) {
            msg = "未定义异常";
        }
        msg = msg.trim();
        if (msg.length() == 0) {
            msg = "未定义异常";
        }
        ResultBO<Object> r = new ResultBO<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }


}
