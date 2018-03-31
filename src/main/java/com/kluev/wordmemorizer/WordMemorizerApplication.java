package com.kluev.wordmemorizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@SpringBootApplication
public class WordMemorizerApplication {

	@Autowired
	TemplateEngine templateEngine;

	//TODO Вместо флага хранить DB_URL, которую можно указать в аргументах
	public static boolean useRealDb;

	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.toLowerCase().contains("realdb")){
				useRealDb = true;
				break;
			}
		}
		SpringApplication.run(WordMemorizerApplication.class, args);
	}

	@Bean
	public TemplateEngine engine() {
		return templateEngine;
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setOrder(1);
		viewResolver.setViewNames(new String[]{"*.html", "*.xhtml", "*.htm"});
		return viewResolver;
	}
}
