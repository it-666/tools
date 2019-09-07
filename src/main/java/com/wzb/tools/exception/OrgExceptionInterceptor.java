package com.wzb.tools.exception;

import java.util.Objects;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常拦截器
 *
 * @author 邬志斌 <br>
 * 2018年4月18日 上午10:54:26
 */
@Aspect
@RestControllerAdvice
public class OrgExceptionInterceptor {

    private Environment environment;

    /**
     * 注入 Environment 构建异常拦截器对象
     */
    public OrgExceptionInterceptor(Environment environment) {
        this.environment = environment;
    }

    /**
     * 异常处理器，拦截到异常后，进行处理
     * <br> 2018年4月18日上午11:44:25
     */
    @ExceptionHandler(value = Exception.class)
    public ResultBO<?> exceptionHandler(Exception e) {

        //查看配置文件，是否打印异常堆栈信息， true/false 默认打印
        String print = environment.getProperty("wzbjsz.exception.print-stack-trace");
        if (print == null || !Objects.equals(print.trim(), "false")) {
            e.printStackTrace();
        }

        //判定是否是自己定义的异常
        if (e instanceof OrgException) {
            OrgException em = (OrgException) e;
            return ResultTool.error(em.getCode(), em.getMessage());
        }
        //返回系统异常

        return ResultTool.error(OrgExceptionInfoEnum.EXCEPTION_UNKONW);
    }

}
