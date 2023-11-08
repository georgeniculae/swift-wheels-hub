package com.carrental.lib.resttemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "rest-template", name = "enabled")
public class ApacheHttpClientConfig {

    private static final String TIMEOUT = "timeout";

    private static final String IDLE_MONITOR = "idleMonitor";

    @Value("${server.port}")
    private int port;

    @Value("${rest-template.hostname}")
    private String hostname;

    @Value("${rest-template.config.pool-size}")
    private int poolSize;

    @Value("${rest-template.config.idle-connection-wait-time}")
    private int idleConnectionWaitTime;

    @Value("${rest-template.config.socket-timeout}")
    private int socketTimeout;

    @Value("${rest-template.config.request-timeout}")
    private int requestTimeout;

    @Value("${rest-template.config.connect-timeout}")
    private int connectTimeout;

    @Value("${rest-template.config.default-keep-alive-time}")
    private int defaultKeepAliveTime;

    @Value("${rest-template.config.max-localhost-connections}")
    private int maxLocalhostConnections;

    @Value("${rest-template.config.max-total-connections}")
    private int maxTotalConnections;

    @Value("${rest-template.config.max-route-connections}")
    private int maxRouteConnections;

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();

        poolingConnectionManager.setMaxTotal(maxTotalConnections);
        poolingConnectionManager.setDefaultMaxPerRoute(maxRouteConnections);

        HttpHost localhost = new HttpHost(hostname, port);
        poolingConnectionManager.setMaxPerRoute(new HttpRoute(localhost), maxLocalhostConnections);

        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (httpResponse, httpContext) -> {
            HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
            HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

            while (elementIterator.hasNext()) {
                HeaderElement element = elementIterator.nextElement();
                String param = element.getName();
                String value = element.getValue();

                if (value != null && param.equalsIgnoreCase(TIMEOUT)) {
                    return Long.parseLong(value) * 1000;
                }
            }

            return defaultKeepAliveTime;
        };
    }

    @Bean
    public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 20000)
            public void run() {
                if (pool != null) {
                    pool.closeExpiredConnections();
                    pool.closeIdleConnections(idleConnectionWaitTime, TimeUnit.MILLISECONDS);

                    log.info("Idle connection monitor: Closing expired and idle connections");
                }
            }
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setThreadNamePrefix(IDLE_MONITOR);
        scheduler.setPoolSize(poolSize);

        return scheduler;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(requestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

}
