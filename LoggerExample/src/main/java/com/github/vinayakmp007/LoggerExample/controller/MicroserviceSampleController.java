package com.github.vinayakmp007.LoggerExample.controller;

import com.github.vinayakmp007.microservicelogger.configuration.SystemPropertyReader;
import com.github.vinayakmp007.microservicelogger.log.concurrency.MicroServiceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.github.vinayakmp007.LoggerExample.SampleOutput;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/test")
public class MicroserviceSampleController {
    static Logger logger = Logger.getLogger(MicroserviceSampleController.class.getName());

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/first")
    public SampleOutput getFirst() {
     SampleOutput sampleOutput = new SampleOutput();

     MicroServiceLogger.startSpan("Making a rest call");
        logger.log(Level.INFO,"http://"+new SystemPropertyReader().getStringProperty("lb") + "/test/second");
        ResponseEntity<SampleOutput> response
                =restTemplate.getForEntity( "http://"+
                new SystemPropertyReader().getStringProperty("lb") + "/test/second",
                SampleOutput.class);
        logger.log(Level.INFO,"Inside a span");
        MicroServiceLogger.endSpan();
        logger.log(Level.INFO,"outside span");
        String outputfromDownstream = "no output";
        if(response.getBody()!=null){
           outputfromDownstream = response.getBody().text;
        }

        sampleOutput.text = outputfromDownstream + " -This is from  first API  - " ;
     logger.log(Level.INFO,"This is inside  first API");

        return sampleOutput;
    }
    @GetMapping("/second")
    public SampleOutput getSecond() {
        SampleOutput sampleOutput = new SampleOutput();

        ResponseEntity<SampleOutput> response
                = restTemplate.getForEntity("http://"+
                new SystemPropertyReader().getStringProperty("lb") + "/test/third",
                SampleOutput.class);
        String outputfromDownstream = "no output";
        if(response.getBody()!=null){
            outputfromDownstream = response.getBody().text;
        }
        sampleOutput.text = outputfromDownstream + "-This  is from  second API -";
        logger.log(Level.INFO,"This was called second  in API with no span ");
        return sampleOutput;
    }
    @GetMapping("/third")
    public SampleOutput getThird() {
        SampleOutput sampleOutput = new SampleOutput();

        try {
            MicroServiceLogger.startSpan("parent span");

            try {
                MicroServiceLogger.startSpan("child span");

                sampleOutput.text = "-This is from  third API-" ;

                logger.log(Level.INFO, " This is inside third API and Inside two spans");
            } finally {
                MicroServiceLogger.endSpan();
            }
            logger.log(Level.INFO, "This is inside third API and Inside one span");
        }finally{
            MicroServiceLogger.endSpan();
        }
        return sampleOutput;
    }
    @GetMapping("/fourth")
    public SampleOutput getFourth() {
        SampleOutput sampleOutput = new SampleOutput();

        for(int i=0;i<200;i++) {
            try {
                MicroServiceLogger.startSpan("parent span ");


                for (int j = 0; j < 10; j++) {
                    try {
                        MicroServiceLogger.startSpan("child span fourth");

                        sampleOutput.text = "This is inside fourth API" + Thread.currentThread().getName();

                        logger.log(Level.INFO, " This called fourth API and Inside two spans");
                    } finally {
                        MicroServiceLogger.endSpan();
                    }
                }
                logger.log(Level.INFO, "This is inside fourth API and Inside one span");
            }finally{
                    MicroServiceLogger.endSpan();
                }
        }
        return sampleOutput;
    }
}
