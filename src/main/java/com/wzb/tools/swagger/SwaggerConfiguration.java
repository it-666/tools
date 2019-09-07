package com.wzb.tools.swagger;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.zuul.ZuulProxyAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
@ConditionalOnClass({SwaggerResourcesProvider.class, RouteLocator.class})
@AutoConfigureAfter(ZuulProxyAutoConfiguration.class)
public class SwaggerConfiguration {

    public SwaggerResourcesProvider swaggerResourcesProvider(RouteLocator locator) {
        SwaggerResourceProviderImpl impl = new SwaggerResourceProviderImpl();
        impl.setRouteLocator(locator);
        return impl;
    }
}
