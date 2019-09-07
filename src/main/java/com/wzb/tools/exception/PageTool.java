package com.wzb.tools.exception;

/**
 * 分页工具了类
 *
 * @author 邬志斌 <br>
 * 2018年5月22日 上午10:02:39
 */
public class PageTool {
    /**
     * 不允许构造该对象
     */
    private PageTool() {
    }

    /**
     * 分页时用
     *
     * @param count
     * @param obj
     * @return
     */
    public static PageBO<Object> success(Integer count, Object obj) {
        if (obj == null) {
            obj = "";
        }
        PageBO<Object> r = new PageBO<>();
        r.setCode(0);
        r.setMsg("成功");
        r.setCount(count);
        r.setData(obj);
        return r;
    }

    /**
     * 失败时调用<br>
     * （传入参数为null时，会自动设置 code为2，msg为"未定义异常"）<br>
     * 2018年4月18日上午11:27:45
     */
    public static PageBO<Object> error(ExceptionInfoBO em) {
        PageBO<Object> r = new PageBO<>();
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
    static PageBO<Object> error(Integer code, String msg) {
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
        PageBO<Object> r = new PageBO<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
