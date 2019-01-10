/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST30300ApplicationAboutService.java
 * Program Description	    :  
 * Environment	 	        :  Java 7
 * Author					:  Manego
 * Version					:  1.0
 * Creation Date            :  June 3, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.util.List;

import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;

/**
 * service interface for the about screen implementation.
 * 
 * @author Manego
 * 
 */
public interface IST30300ApplicationAboutService {
	/**
	 * This method use to get the application wording from DB. It will use the
	 * category and sub category define in application properties.
	 * 
	 * @return The application wording.
	 */
	public String getApplicationWording() throws SystemDoesNotExistsException,
			SystemIsNotActiveException;

	/**
	 * This method use to get the help desk information from DB. It will use the
	 * category and sub category define in application properties.
	 * 
	 * @return The help desk info.
	 */
	public String getHelpdeskInfo() throws SystemDoesNotExistsException,
			SystemIsNotActiveException;

	/**
	 * Method use to get the application current version from DB. It will use
	 * the category and sub category define in application properties.
	 * 
	 * @return Return the application version.
	 * @throws NoDataFoundException
	 *             If version is not define in DB.
	 */
	public String getCurrentAppVersion() throws SystemDoesNotExistsException,
			SystemIsNotActiveException;

	/**
	 * This method use to get the application versions from DB. It will use the
	 * category and sub category define in application properties and read the
	 * available versions from DB.
	 * 
	 * @return The list of available versions.
	 * @throws NoDataFoundException
	 *             If versions are not found.
	 */
	public List<String> getVersionList() throws SystemDoesNotExistsException;

	/**
	 * This method use to get the enhancement from DB. It will use the category
	 * and sub category define in application properties and read the
	 * enhancement for given code.
	 * 
	 * @param versionCode
	 *            A version code.
	 * @return A enhancement description.
	 * @throws SystemDoesNotExistsException
	 *             If enhancement not found.
	 */
	public String getEnhancement(final String versionCode)
			throws SystemDoesNotExistsException;

	/**
	 * Reads the current standard library version from META-INF of standard
	 * library jar.
	 * 
	 * @return A library version.
	 */
	public String getCurrentStandardLibVersion();

	/**
	 * Reads the current standard batch version from META-INF of standard
	 * library jar.
	 * 
	 * @return A library version.
	 */
	public String getCurrentStandardBatchVersion();

	/**
	 * Reads the current security center version from META-INF of standard
	 * library jar.
	 * 
	 * @return A library version.
	 */
	public String getCurrentSecurityCenterVersion();

}
