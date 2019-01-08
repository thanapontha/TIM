/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.config
 * Program ID 	            :  AppConfig.java
 * Program Description	    :  Application Configuration
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Feb 28, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.config;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * A application configuration class use to load the messages used for:
 * <ul>
 * <li>For application specifics
 * <li>For application messages
 * <li>Development Standard
 * <li>For screen labels
 * <li>Development standard labels
 * </ul>
 * 
 * @author danilo
 * 
 */
@Configuration
@PropertySource({ "classpath:properties/data-access.properties",
		"classpath:properties/configuration.properties" })
@ImportResource("classpath:application-config.xml")
@ComponentScan({ "th.co.toyota.application.web",
		"th.co.toyota.application.service",
		"th.co.toyota.application.repository",
		"th.co.toyota.st3.api.util",
		"th.co.toyota.st3.api.repository",
		"th.co.toyota.st3.api.download",
		"th.co.toyota.st3.api.upload",
		"th.co.toyota.bw0.api.common.upload",
		"th.co.toyota.bw0.api.repository.common",//refer to api model
		"th.co.toyota.bw0.api.service.common",//refer to api model
		"th.co.toyota.bw0.batch.report",
		"th.co.toyota.bw0.batch.repository",
		"th.co.toyota.bw0.batch.preprocess.repository",
		"th.co.toyota.bw0.api.common",
		"th.co.toyota.bw0.common.repository",
		"th.co.toyota.bw0.common.service",
		"th.co.toyota.bw0.web.common",
		"th.co.toyota.bw0.web.report",
		"th.co.toyota.bw0.web",
		"th.co.toyota.bw0.util"})
public class AppConfig {

	/**
	 * Application property place holder.
	 * 
	 * @return {@link PropertySourcesPlaceholderConfigurer}
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * Application message validator.
	 * 
	 * @return {@link Validator}
	 */
	@Bean
	public Validator getValidator() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames(new String[] {
				"classpath:properties/ApplicationResources", // For application specifics
				"classpath:properties/MessageResources", // For application messages
				"classpath:properties/MessageResources_ST3", // Development Standard
				"classpath:properties/LabelResources", // For screen labels
				"classpath:properties/LabelResources_ST3"}); // Development Standard
		messageSource.setDefaultEncoding("UTF-8");

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource);

		return validator;
	}
}