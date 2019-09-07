package com.wzb.tools.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * the context holder
 */
public class ApplicationContextHolder implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    /**
     * the static context
     */
    private static ApplicationContext context;

    /**
     * for easy get context
     *
     * @return
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * set the context
     *
     * @param applicationContext
     */
    public void initialize(ConfigurableApplicationContext applicationContext) {
        context = applicationContext;
    }
}
