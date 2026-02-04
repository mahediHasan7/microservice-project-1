package org.mahedi.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class GlobalPostFilter implements GlobalFilter, Ordered {
    Logger LOGGER = LoggerFactory.getLogger(GlobalPostFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            LOGGER.info("1st Global post filter");
        }));
    }

    @Override
    public int getOrder() {
        return 0; //! this will make the post filter run at the last
    }
}
