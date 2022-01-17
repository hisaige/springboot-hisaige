package com.hisaige.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.ConversionService;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
public class ConfigCoreApplication {

    @Autowired
    @Qualifier("mvcConversionService")
    private ConversionService conversionService;

    @PostConstruct
    public void init(){
        List convert = conversionService.convert("a,b", List.class);
        System.out.println(convert);
    }

    public static void main(String[] args) {
        SpringApplication.run(ConfigCoreApplication.class, args);
    }

}
