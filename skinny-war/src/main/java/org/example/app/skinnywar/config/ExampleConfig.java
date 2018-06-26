package org.example.app.skinnywar.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@EnableMBeanExport(registration=RegistrationPolicy.REPLACE_EXISTING)
@ComponentScan("org.example.app.skinnywar.controller")
public class ExampleConfig {
}
