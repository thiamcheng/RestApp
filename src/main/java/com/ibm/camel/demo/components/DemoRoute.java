package com.ibm.camel.demo.components;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.json.simple.JSONObject;

import java.util.Map;

@Component
public class DemoRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        restConfiguration().component("netty-http").host("0.0.0.0").port(8081).bindingMode(RestBindingMode.json);
//        .setJsonDataFormat("json-jackson");

        from("rest://post:ISO8583").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("This is getBody"+ exchange.getIn().getBody());
                Map<String,String> bodyMap = (Map) exchange.getIn().getBody();
                
               
                JSONObject obj = new JSONObject();
                
            	for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
            	// System.out.println("Field : " + entry.getKey() + " Value : " + entry.getValue());
            		obj.put(entry.getKey(), entry.getValue());
            	}
               
               exchange.getMessage().setBody(obj.toJSONString().getBytes());
               
            }
        }).setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .to("netty-http:http://52.117.213.242:8087/jpos/client?bindingMode=json");
       

        from("rest://get:echoGet/{echoValue}").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getHeader("echoValue"));
                exchange.getMessage().setBody(exchange.getIn().getHeader("echoValue"));
            }
        });

    }
}
