package org.efaps.pos.config;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestClientConfig
{

    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfig.class);

    private final ConfigProperties configProperties;

    public RestClientConfig(ConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    @Bean
    public RestClientCustomizer customizer()
    {
        return restClientBuilder -> {
            if (configProperties.getProxy() != null && configProperties.getProxy().getUri() != null) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8080));
                final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setProxy(proxy);
                restClientBuilder.requestFactory(factory);
                LOG.warn("Not completlty implemented!!!!");
            }
            restClientBuilder.requestInterceptor(new ClientRequestLoggingInterceptor())
                            .bufferContent((uri,
                                            httpMethod) -> true);
        };
    }
}
