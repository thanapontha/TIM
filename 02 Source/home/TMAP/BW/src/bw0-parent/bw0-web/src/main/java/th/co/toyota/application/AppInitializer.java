/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application
 * Program ID 	            :  AppInitializer.java
 * Program Description	    :  Application initializer
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
package th.co.toyota.application;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * A ST3 we application initializer, for more details see {@link
 * org.springframework.web.WebApplicationInitializer}
 * 
 * @author danilo
 * 
 */
public class AppInitializer implements WebApplicationInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet
	 * .ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
		root.scan("th.co.toyota.application.config");

		// Manages the lifecycle of the root application context
		servletContext.addListener(new ContextLoaderListener(root));

		ServletRegistration.Dynamic appServlet = servletContext.addServlet(
				"appServlet", new DispatcherServlet(root));
		appServlet.setLoadOnStartup(1);

		Set<String> mappingConflicts = appServlet.addMapping("/");
		if (!mappingConflicts.isEmpty()) {
			throw new IllegalStateException(
					"'appServlet' could not be mapped to '/' due "
							+ "to an existing mapping. This is a known issue under Tomcat versions "
							+ "<= 7.0.14;");
		}
	}
}