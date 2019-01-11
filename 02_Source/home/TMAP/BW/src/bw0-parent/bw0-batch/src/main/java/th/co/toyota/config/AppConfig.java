/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-api
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.st3.api.config
 * Program ID 	            :  AppConfig.java
 * Program Description	    :  Application Configuration
 * Environment	 	        :  Java 7
 * Author					:  elaine
 * Version					:  1.0
 * Creation Date            :  Mar 27, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.config;

import javax.validation.Validator;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@PropertySource({ "classpath:properties/data-access.properties",
	"classpath:properties/batch.properties" })
@ImportResource(value = { "classpath:application-config.xml" })
@ComponentScan(value = { "th.co.toyota.st3.api.util", 
						"th.co.toyota.st3.api.repository", 
						"th.co.toyota.st3.api.download",
						"th.co.toyota.st3.batch",
						"th.co.toyota.st3.api.report",
						"th.co.toyota.st3.api.upload",
						"th.co.toyota.bw0.api.common.upload",
						"th.co.toyota.bw0.api.repository.common",
						"th.co.toyota.bw0.api.service.common",
						"th.co.toyota.bw0.batch"})
@EnableTransactionManagement
public class AppConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Validator getValidator() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames(new String[] {
												"classpath:properties/MessageResources", // For application messages
												"classpath:properties/MessageResources_ST3" // Development Standard
												}); // Development Standard
		messageSource.setDefaultEncoding("UTF-8");

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource);

		return validator;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames(new String[] {
												"classpath:properties/MessageResources", // For application messages
												"classpath:properties/MessageResources_ST3" // Development Standard
												}); // Development Standard
		messageSource.setDefaultEncoding("UTF-8");

		return messageSource;
	}

}
