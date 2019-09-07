package com.wzb.tools.exception;

/**
 * 系统异常类<br>
 * 在系统中，我们不再抛出原生异常，将原生异常进行封装处理，抛出 {@link OrgException }，<br>
 * 通过其构造函数创建异常，该构造函数需要一个 {@link  ExceptionInfoBO } 类型的参数，
 * {@link  ExceptionInfoBO } 是封装了一系列异常信息的接口，使用时像枚举一样调用
 * （因为枚举不能被继承，使得异常类的构造器不能使用统一的参数类型）。
 *
 * @author 邬志斌 <br>
 * 2018年4月18日 上午10:50:27
 */
public class OrgException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8564191176900879504L;

    /**
     * 异常编号，系统捕获到异常后，该码成为接口响应的状态码
     */
    private Integer code;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * <!--设为私有的？ 原因：在构造器中无法对为null的参数em进行处理，
     * 获取该对象的实例时，使用静态的  getThis(ExceptionInfoBO em) 方法-->
     * 通过 一个{@link  ExceptionInfoBO } 参数获取一个对象
     */
    public OrgException(ExceptionInfoBO em) {
        super(em.getMsg());
        this.code = em.getCode();
    }

    /**
     * 通过 一个{@link  ExceptionInfoBO } 参数获取一个实例化对象
     * <br> 2018年4月20日上午10:39:41
     */
    public static OrgException getThis(ExceptionInfoBO em) {
        if (em == null) {
            ExceptionInfoBO eb = new ExceptionInfoBO(-1, "未定义异常");
            return new OrgException(eb);
        }
        return new OrgException(em);
    }


}
