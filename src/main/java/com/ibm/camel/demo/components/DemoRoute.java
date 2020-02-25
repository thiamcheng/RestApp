package com.ibm.camel.demo.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DemoRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        restConfiguration().component("netty-http").host("localhost").port(8080).bindingMode(RestBindingMode.json);
//        .setJsonDataFormat("json-jackson");

        from("rest://post:echo").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getBody());
                Map bodyMap = (Map) exchange.getIn().getBody();
                exchange.getMessage().setHeader("postEcho", bodyMap.get("echo"));
            }
        }).to("rest://get:echoGet/{postEcho}");

        from("rest://get:echoGet/{echoValue}").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getHeader("echoValue"));
                exchange.getMessage().setBody(exchange.getIn().getHeader("echoValue"));
            }
        });

    }
}
