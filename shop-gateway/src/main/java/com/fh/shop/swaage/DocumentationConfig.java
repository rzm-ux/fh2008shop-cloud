package com.fh.shop.swaage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String applicationName;

    public DocumentationConfig(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }
    // 自动获取系统配置的路由资源集合
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        // 排除自身，将其他的服务添加进去
        discoveryClient.getServices().stream().filter(s -> !s.equals(applicationName)).forEach(name -> {
            Optional<ServiceInstance> instanceOptional = discoveryClient.getInstances(name).stream().findFirst();
            if (instanceOptional.isPresent() && instanceOptional.get().getMetadata().containsKey("context-path")) {
                String contexPath = instanceOptional.get().getMetadata().get("context-path");
                resources.add(swaggerResource(name, "/" + name + contexPath + "/v2/api-docs", "2.0"));
            } else {
                resources.add(swaggerResource(name, "/" + name + "/v2/api-docs", "2.0"));
            }

        });
        return resources;
    }
    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
