/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33060SystemMasterRepository.java
 * Program Description	    :  System master repository.
 * Environment	 	        :  Java 7
 * Author					:  LJ
 * Version					:  1.0
 * Creation Date            :  June 5, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.sql.Connection;
import java.util.List;

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

/**
 * A repository interface for system master.
 * 
 * @author LJ
 * 
 */
public interface IST33060SystemMasterRepository {

	/**
	 * Query to find all system master id's from DB.
	 * 
	 * @return List of {@link SystemInfoId}
	 */
	List<SystemInfoId> querySystemMasterInfoId();

	/**
	 * Query system master details based on given system id.
	 * 
	 * @param infoId
	 *            A {@link SystemInfoId}
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> querySystemMasterInfo(SystemInfoId infoId);

	/**
	 * Query to single system master detail based on given system id.
	 * 
	 * @param infoId
	 *            A {@link SystemInfoId}
	 * @return A {@link SystemInfo}
	 */
	SystemInfo findSystemMasterInfo(SystemInfoId infoId);

	/**
	 * Add a system master details to DB.
	 * 
	 * @param sysInfo
	 *            A {@link SystemInfo} details.
	 * @throws SystemAlreadyExistsException
	 *             If details already exists.
	 */
	void addSystemMasterInfo(SystemInfo sysInfo)
			throws SystemAlreadyExistsException;

	/**
	 * Update {@link SystemInfo} in DB.
	 * 
	 * @param sysInfo
	 *            A {@link SystemInfo}
	 * @throws ConcurrencyException
	 *             In case of concurrent update.
	 * @throws SystemDoesNotExistsException
	 *             If details does not exists in DB.
	 */
	void updateSystemMasterInfo(SystemInfo sysInfo)
			throws ConcurrencyException, SystemDoesNotExistsException;

	/**
	 * Delete {@link SystemInfo} details from DB.
	 * 
	 * @param sysInfo
	 *            A {@link SystemInfo}
	 * @throws ConcurrencyException
	 *             In case of concurrent deletion
	 * @throws SystemDoesNotExistsException
	 *             If details does not exist in DB.
	 */
	void deleteSystemMasterInfo(SystemInfo sysInfo)
			throws ConcurrencyException, SystemDoesNotExistsException;

	/**
	 * List out the order system master details, details are order by creation
	 * date.
	 * 
	 * @param category
	 *            A category.
	 * @param subCategory
	 *            A sub-category.
	 * @return List of {@link SystemInfo}
	 * @throws NoDataFoundException
	 *             If no details found in DB.
	 */
	List<SystemInfo> querySystemMasterOrderedCodeValue(String category,
			String subCategory) throws NoDataFoundException;

	/**
	 * Query for system master code and value for given category and sub
	 * category.
	 * 
	 * @param category
	 *            A category.
	 * @param subCategory
	 *            A sub-category.
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory);

}
