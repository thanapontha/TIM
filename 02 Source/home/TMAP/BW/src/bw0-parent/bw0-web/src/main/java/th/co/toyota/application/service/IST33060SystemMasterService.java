/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST33060SystemMasterService.java
 * Program Description	    :  System master service interface.
 * Environment	 	        :  Java 7
 * Author					:  LJ
 * Version					:  1.0
 * Creation Date            :  Nov 4, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

/**
 * A service interface for system master screen.
 * 
 * @author LJ
 * 
 */
public interface IST33060SystemMasterService {

	/**
	 * Retrieve all system master id's from database.
	 * 
	 * @return List of {@link SystemInfoId}
	 */
	List<SystemInfoId> retrieveSystemMasterInfoId();

	/**
	 * Takes value from criteria field and search data from DB and display
	 * result in the data grid.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @param code
	 *            A code
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> listSystemMasterInfo(String category, String subCategory,
			String code);

	/**
	 * Save new System master data in the DB.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @param code
	 *            A code
	 * @param value
	 *            A value
	 * @param remark
	 *            A remark
	 * @param status
	 *            A status
	 * @param userId
	 *            A user id.
	 * @throws SystemAlreadyExistsException
	 *             If system already exists.
	 */
	void addSystemMasterInfo(String category, String subCategory, String code,
			String value, String remark, Character status, String userId)
			throws SystemAlreadyExistsException;

	/**
	 * Update System master details in DB.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @param code
	 *            A code
	 * @param value
	 *            A value
	 * @param remark
	 *            A remark
	 * @param status
	 *            A status
	 * @param userId
	 *            A user id.
	 * @param updateDt
	 *            The last updated date.
	 * @throws ConcurrencyException
	 *             In case of con-currency update.
	 * @throws SystemDoesNotExistsException
	 *             If system already exists.
	 */
	void updateSystemMasterInfo(String category, String subCategory,
			String code, String value, String remark, Character status,
			String userId, Date updateDt) throws ConcurrencyException,
			SystemDoesNotExistsException;

	/**
	 * Deletye the System master detais from DB.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @param code
	 *            A code
	 * @param updateDt
	 *            The last updated date.
	 * @throws ConcurrencyException
	 *             In case of con-currency update.
	 * @throws SystemDoesNotExistsException
	 *             If system already exists.
	 */
	void deleteSystemMasterInfo(String category, String subCategory,
			String code, Date updateDt) throws ConcurrencyException,
			SystemDoesNotExistsException;

	/**
	 * Operation use to list the system master details in Excel.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @param code
	 *            A code
	 * @return A {@link HSSFWorkbook} excel report.
	 * @throws SystemDoesNotExistsException
	 *             If system master details not found in DB.
	 * @throws UnableToCreateExcelForDowloadException
	 */
	HSSFWorkbook listSystemMasterInfoToExcel(String category,
			String subCategory, String code)
			throws SystemDoesNotExistsException,
			UnableToCreateExcelForDowloadException;

	/**
	 * Operation use to retrieve system master information for given category
	 * and sub-category value.
	 * 
	 * @param category
	 *            A category
	 * @param subCategory
	 *            A sub-category
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory);
}