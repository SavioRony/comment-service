package com.dante.comment_service.api.config.client;

import com.dante.comment_service.api.client.ModerationClient;
import com.dante.comment_service.api.client.RestClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
    @Bean
    public ModerationClient moderationClient(RestClientFactory factory) {
        RestClient restClient = factory.temperatureMonitoringRestClient();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(ModerationClient.class);
    }
}
