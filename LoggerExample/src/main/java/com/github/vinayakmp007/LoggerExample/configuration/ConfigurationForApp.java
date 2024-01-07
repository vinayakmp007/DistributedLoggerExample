package com.github.vinayakmp007.LoggerExample.configuration;

import com.github.vinayakmp007.microservicelogger.configuration.SystemPropertyReader;
import com.github.vinayakmp007.microservicelogger.daemon.MicroServiceLoggerDaemon;
import com.github.vinayakmp007.microservicelogger.kafka.MessageRecordSerializer;
import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.github.vinayakmp007.LoggerExample.Interceptor.CorrIdInterceptor;
import com.github.vinayakmp007.LoggerExample.Interceptor.DownstreamCorrIdInterceptor;
import com.github.vinayakmp007.microservicelogger.log.MicroServiceLoggerDefaultLogHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Configuration
public class ConfigurationForApp implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CorrIdInterceptor());
    }
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate= new RestTemplate();
        restTemplate.setInterceptors(Arrays.asList(new DownstreamCorrIdInterceptor()));
        return restTemplate;
    }

    @Bean
    public KafkaProducer<String, MessageRecord> producerForLog() {
        Map<String, Object> configProps = new HashMap<>();


        printEnvInf();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,new SystemPropertyReader().getStringProperty("kafkacon"));  //
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageRecordSerializer.class);
        return new KafkaProducer<>(configProps);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void doSomethingAfterStartup(ContextRefreshedEvent contextRefreshedEvent) {
        MicroServiceLoggerDaemon.startDaemon(new SystemPropertyReader(), (KafkaProducer<String, MessageRecord>)contextRefreshedEvent.getApplicationContext().getBean("producerForLog"));

        LogManager.getLogManager().reset();
        Logger system = Logger.getLogger("");
        system.addHandler(new MicroServiceLoggerDefaultLogHandler());


    }


    void printEnvInf(){
        Map<String, String> envMap = System.getenv();
        System.out.println("Env variables");
        for (String envName : envMap.keySet()) {
            System.out.format("%s = %s%n", envName, envMap.get(envName));
        }

        System.out.println("Env propeties");
        Properties properties =System.getProperties();
        for(String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.out.println(key + " => " + value);
        }

    }

}
