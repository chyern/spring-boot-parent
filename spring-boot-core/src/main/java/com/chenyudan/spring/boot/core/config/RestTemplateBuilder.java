package com.chenyudan.spring.boot.core.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/11/26 15:14
 */
@Slf4j
public class RestTemplateBuilder {

    private final HttpClientPoolConfig httpClientPoolConfig;
    private final List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors;

    public RestTemplateBuilder(HttpClientPoolConfig httpClientPoolConfig) {
        this.httpClientPoolConfig = httpClientPoolConfig;
        this.clientHttpRequestInterceptors = null;
    }

    public RestTemplateBuilder(HttpClientPoolConfig httpClientPoolConfig, List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors) {
        this.httpClientPoolConfig = httpClientPoolConfig;
        this.clientHttpRequestInterceptors = clientHttpRequestInterceptors;
    }

    public RestTemplate build() {
        ClientHttpRequestFactory clientHttpRequestFactory = this.clientHttpRequestFactory();
        return this.createRestTemplate(clientHttpRequestFactory);
    }

    /**
     * 配置restTemplate
     */
    private RestTemplate createRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        //我们采用RestTemplate内部的MessageConverter
        //重新设置StringHttpMessageConverter字符集，解决中文乱码问题
        this.modifyDefaultCharset(restTemplate);
        //设置错误处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        if (CollectionUtils.isNotEmpty(clientHttpRequestInterceptors)) {
            restTemplate.setInterceptors(clientHttpRequestInterceptors);
        }
        return restTemplate;
    }

    /**
     * 修改默认的字符集类型为utf-8
     */
    private void modifyDefaultCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }
        Charset defaultCharset = Charset.forName(httpClientPoolConfig.getCharset());
        converterList.add(1, new StringHttpMessageConverter(defaultCharset));
    }

    /**
     * 创建HTTP客户端工厂
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        //maxTotalConnection 和 maxConnectionPerRoute 必须要配
        if (httpClientPoolConfig.getMaxTotalConnect() <= 0) {
            throw new IllegalArgumentException("invalid maxTotalConnection: " + httpClientPoolConfig.getMaxTotalConnect());
        }
        if (httpClientPoolConfig.getMaxConnectPerRoute() <= 0) {
            throw new IllegalArgumentException("invalid maxConnectionPerRoute: " + httpClientPoolConfig.getMaxConnectPerRoute());
        }
        HttpClient httpClient = this.httpClient();
        return this.httpComponentsClientHttpRequestFactory(httpClient);
    }

    /**
     * 配置httpClient
     */
    private HttpClient httpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try {
            //设置信任ssl访问
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    // 注册http和https请求
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();

            //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 最大连接数
            connectionManager.setMaxTotal(httpClientPoolConfig.getMaxTotalConnect());
            // 同路由并发数
            connectionManager.setDefaultMaxPerRoute(httpClientPoolConfig.getMaxConnectPerRoute());
            //配置连接池
            httpClientBuilder.setConnectionManager(connectionManager);
            // 重试次数
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientPoolConfig.getRetryTimes(), true));

            //设置默认请求头
            List<Header> headers = this.getDefaultHeaders();
            httpClientBuilder.setDefaultHeaders(headers);
            //设置长连接保持策略
            ConnectionKeepAliveStrategy keepAliveStrategy = this.connectionKeepAliveStrategy();
            httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
            return httpClientBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("初始化HTTP连接池出错", e);
        }
        return null;
    }

    /**
     * 设置请求头
     */
    private List<Header> getDefaultHeaders() {
        List<Header> headers = new ArrayList<>();
        /*headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));*/
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        //headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        return headers;
    }

    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                log.info("HeaderElement:{}", new Gson().toJson(he));
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                        log.error("解析长连接过期时间异常", ignore);
                    }
                }
            }
            HttpHost target = (HttpHost) context.getAttribute(
                    HttpClientContext.HTTP_TARGET_HOST);
            //如果请求目标地址,单独配置了长连接保持时间,使用该配置
            Optional<Entry<String, Integer>> any = Optional.ofNullable(httpClientPoolConfig.getKeepAliveTargetHost()).orElseGet(HashMap::new)
                    .entrySet().stream().filter(
                            e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();
            //否则使用默认长连接保持时间
            return any.map(en -> en.getValue() * 1000L).orElse(httpClientPoolConfig.getKeepAliveTime() * 1000L);
        };
    }

    private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        factory.setConnectTimeout(httpClientPoolConfig.getConnectTimeout());
        // 数据读取超时时间，即SocketTimeout
        factory.setReadTimeout(httpClientPoolConfig.getReadTimeout());
        // 从连接池获取请求连接的超时时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        factory.setConnectionRequestTimeout(httpClientPoolConfig.getConnectionRequestTimeout());
        return factory;
    }
}
