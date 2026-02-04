package org.mahedi.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class GlobalPreFilter implements GlobalFilter, Ordered {
    Logger LOGGER = LoggerFactory.getLogger(GlobalPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info("1st Global pre filter");

        String uri = exchange.getRequest().getURI().toString();
        LOGGER.info("Uri: {}", uri);

        LOGGER.info("Headers:");
        HttpHeaders headers = exchange.getRequest().getHeaders();
        headers.toSingleValueMap().keySet().forEach(header -> {
            LOGGER.info("{}: {}", header, headers.getFirst(header));
        });


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
