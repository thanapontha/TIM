/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.config
 * Program ID 	            :  WebConfig.java
 * Program Description	    :  Web Application Configuration
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

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

/**
 * A ST3 web application configuration.
 * <p>
 * For more details
 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter}
 * 
 * @author danilo
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan("th.co.toyota.application.web")
public class WebConfig extends WebMvcConfigurerAdapter {
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations(
				"/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
	}

	/**
	 * JSP views resolver.
	 * 
	 * @return {@link ViewResolver}
	 */
	@Bean
	public ViewResolver viewResolver() {
		final UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(TilesView.class);

		return viewResolver;
	}

	/**
	 * Tiles configuration.
	 * 
	 * @return {@link TilesConfigurer}
	 */
	@Bean
	public TilesConfigurer tilesConfigurer() {
		final TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(new String[] { "/WEB-INF/tiles.xml" });
		tilesConfigurer.setCheckRefresh(true);

		return tilesConfigurer;
	}

	/**
	 * Use to load the messages used for:
	 * <ul>
	 * <li>For application specifics
	 * <li>For application messages
	 * <li>Development Standard
	 * <li>For screen labels
	 * <li>Development standard labels
	 * </ul>
	 * 
	 * @return {@link MessageSource},a ST3 message source.
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames(new String[] {
				"classpath:properties/ApplicationResources", // For application specifics
				"classpath:properties/MessageResources", // For application messages
				"classpath:properties/MessageResources_ST3", // Development Standard
				"classpath:properties/LabelResources", // For screen labels 
				"classpath:properties/LabelResources_ST3", // Development Standard
				"classpath:properties/configuration" }); 
		messageSource.setDefaultEncoding("UTF-8");

		return messageSource;
	}
	
	/**
	 * Build a web cookie resolver
	 * 
	 * @return {@link CookieLocaleResolver}
	 */
	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localResolver = new CookieLocaleResolver();
		localResolver.setDefaultLocale(new Locale("en_US"));
		
		return localResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
	 * #addInterceptors(org.springframework.web.servlet.config.annotation.
	 * InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		
		registry.addInterceptor(localeChangeInterceptor);
	}
}