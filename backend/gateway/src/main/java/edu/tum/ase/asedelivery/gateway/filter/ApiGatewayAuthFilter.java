package edu.tum.ase.asedelivery.gateway.filter;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetStatusGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ApiGatewayAuthFilter extends AbstractGatewayFilterFactory<ApiGatewayAuthFilter.Config> {
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    public ApiGatewayAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            InstanceInfo instance = discoveryClient.getNextServerFromEureka("user-mngmt", false);
            String url = instance.getHomePageUrl();
            restTemplate.getForObject(instance.getHomePageUrl() + "/regulation", List.class);
            if (isValidRequest(request)) {
                // Allow processing
            } else {
                // set UNAUTHORIZED 401 response and stop the processing
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }
}
