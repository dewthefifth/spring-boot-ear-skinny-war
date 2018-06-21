// tag::runner[]
package org.example.app.web0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring-context/initial-context.xml")
public class Application
{

	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

}
// end::runner[]
