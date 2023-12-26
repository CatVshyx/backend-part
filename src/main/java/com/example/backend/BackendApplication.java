package com.example.backend;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(BackendApplication.class,args);
	}
	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> {
			servletContext.setInitParameter("defaultHtmlMimeType", "text/html");
			servletContext.setInitParameter("defaultXmlMimeType", "application/xml");
			servletContext.setInitParameter("defaultCssMimeType", "text/css");
			servletContext.setInitParameter("defaultJsMimeType", "text/javascript");
		};
	}
}
