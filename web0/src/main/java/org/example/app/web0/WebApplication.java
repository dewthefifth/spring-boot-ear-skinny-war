package org.example.app.web0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan()
@EnableAutoConfiguration
@EnableSwagger2
public class WebApplication extends SpringBootServletInitializer
{

	public static void main(String[] args)
	{
		SpringApplication.run(applicationClass, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(applicationClass);
	}

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

	private static Class<WebApplication> applicationClass = WebApplication.class;
}
