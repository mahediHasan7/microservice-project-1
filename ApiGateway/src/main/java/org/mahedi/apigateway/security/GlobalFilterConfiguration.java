package org.mahedi.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalFilterConfiguration {

    Logger LOGGER = LoggerFactory.getLogger(GlobalFilterConfiguration.class);

    @Order(1)
    @Bean
    public GlobalFilter secondPreFilter() {
        return (exchange, chain) -> {
            LOGGER.info("2nd Global pre filter");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        LOGGER.info("2nd Global post filter");
                    }));
        };
    }

    @Order(2)
    @Bean
    public GlobalFilter thirdPreFilter() {
        return (exchange, chain) -> {
            LOGGER.info("3rd Global pre filter");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        LOGGER.info("3rd Global post filter");
                    }));
        };
    }

    @Order(3)
    @Bean
    public GlobalFilter fourthPreFilter() {
        return (exchange, chain) -> {
            LOGGER.info("4th Global pre filter");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        LOGGER.info("4th Global post filter");
                    }));
        };
    }
}
