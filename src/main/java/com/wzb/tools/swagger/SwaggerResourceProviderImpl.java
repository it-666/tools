package com.wzb.tools.swagger;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合展示所有的微服务接口文档
 *
 * @see RouteLocator 路由配置
 */
public class SwaggerResourceProviderImpl implements SwaggerResourcesProvider {

    private RouteLocator routeLocator;

    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<Route> routes = routeLocator.getRoutes();
        routes.forEach(route -> resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"))));
        return resources;
    }

    public SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

    public RouteLocator getRouteLocator() {
        return routeLocator;
    }

    public void setRouteLocator(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }
}
