package com.chenyudan.spring.boot.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/11/26 14:56
 */
public class HttpClientRegister {

    /**
     * http连接池配置
     */
    @ConditionalOnMissingBean
    @Bean(name = "httpClientPoolConfig")
    @ConfigurationProperties(prefix = "spring.http-client.pool")
    public HttpClientPoolConfig httpClientPoolConfig() {
        return new HttpClientPoolConfig();
    }

    /**
     * 初始化RestTemplate,并加入spring的Bean工厂，由spring统一管理
     */
    @ConditionalOnMissingBean
    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(HttpClientPoolConfig httpClientPoolConfig, List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors) {
        return new RestTemplateBuilder(httpClientPoolConfig, clientHttpRequestInterceptors).build();
    }
}
