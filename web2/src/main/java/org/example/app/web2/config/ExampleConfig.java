package org.example.app.web2.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.web.servlet.DispatcherServlet;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@EnableMBeanExport(registration=RegistrationPolicy.REPLACE_EXISTING)
@ComponentScan("org.example.app.web2.controller")
public class ExampleConfig
{
	@Bean
	public ServletRegistrationBean<?> dispatcherRegistration()
	{
		ServletRegistrationBean<?> registration = new ServletRegistrationBean<>(dispatcherServlet());
		registration.addUrlMappings("/*");
		return registration;
	}

	@Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
	public DispatcherServlet dispatcherServlet()
	{
		return new DispatcherServlet();
	}
}
