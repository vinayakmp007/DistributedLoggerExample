package com.github.vinayakmp007.LoggerExample.Interceptor;

import com.github.vinayakmp007.microservicelogger.log.concurrency.MicroServiceLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CorrIdInterceptor  implements HandlerInterceptor {
    static Logger logger = Logger.getLogger(CorrIdInterceptor.class.getName());

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {


        String correlationId = request.getHeader("CORRELID");
        if(correlationId==null){
            MicroServiceLogger.createNewLoggingSession();
        }
        else {
            MicroServiceLogger.createNewLoggingSession(correlationId);
        }
        correlationId = MicroServiceLogger.getCurrentCorrelationId();
        logger.log(Level.INFO,correlationId);
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MicroServiceLogger.endCurrentLoggingSession();
    }


}
