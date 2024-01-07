package com.github.vinayakmp007.LoggerExample.Interceptor;

import com.github.vinayakmp007.microservicelogger.log.concurrency.MicroServiceLogger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class DownstreamCorrIdInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("CORRELID", MicroServiceLogger.getCurrentCorrelationId());
        return execution.execute(request,body);
    }
}
