package com.wzb.tools.exception;

/**
 * 异常信息，纯属性接口 <br>
 * 该接口封装了一系列异常信息，即一个个封装的 {@link  ExceptionInfoBO } 对象
 * 开发人员开发时可以自定义自己的异常信息封装接口,继承该接口，
 * 在自定义的类中继续封装自己定义的异常信息
 * <br> code 编号 0-99 归当前类使用 自定义时请避开此区间
 *
 * @author 邬志斌 <br>
 * 2018年4月18日 下午2:52:15
 */
public interface OrgExceptionInfoEnum {

    /**
     * code: 0 <br>
     * msg:成功
     */
    ExceptionInfoBO SUCCESS = new ExceptionInfoBO(0, "成功");

    /**
     * code: 1 <br>
     * msg:未知异常
     */
    ExceptionInfoBO EXCEPTION_UNKONW = new ExceptionInfoBO(1, "未知异常");

    /**
     * code: 2 <br>
     * msg:空数据异常
     */
    ExceptionInfoBO NULL_DATA_EXCEPTION = new ExceptionInfoBO(2, "空数据异常");

    /**
     * code: 3 <br>
     * msg:非法参数异常
     */
    ExceptionInfoBO ILLEGAL_PARAMETER_EXCEPTION = new ExceptionInfoBO(3, "非法参数异常");

    /**
     * code: 4 <br>
     * msg: 没有配置邮件发送人邮箱，配置文件缺少 spring.mail.username 配置
     */
    ExceptionInfoBO MISSING_PROPERTY_SENDER_MAIL = new ExceptionInfoBO(4,
            "没有配置邮件发送人邮箱，配置文件缺少 spring.mail.username 配置");

    /**
     * code: 5 <br>
     * msg: 没有配置邮件收件人邮箱，配置文件缺少 yuyi.exception.mail.addressee 配置
     */
    ExceptionInfoBO MISSING_PROPERTY_ADDRESSEE = new ExceptionInfoBO(5,
            "没有配置邮件收件人邮箱，配置文件缺少 yuyi.exception.mail.addressee 配置");

    /**
     * code: 6 <br>
     * msg: 没有配置日志文件路径，配置文件缺少 logging.file 配置
     */
    ExceptionInfoBO MISSING_PROPERTY_LOG_FILE = new ExceptionInfoBO(6,
            "没有配置日志文件路径，配置文件缺少 logging.file 配置");


}
