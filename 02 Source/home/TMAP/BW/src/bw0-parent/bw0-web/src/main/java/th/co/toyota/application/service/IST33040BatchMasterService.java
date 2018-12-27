/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST33040BatchMasterService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  manego
 * Version					:  1.0
 * Creation Date            :  June 5, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.BatchMaster;

/**
 * A service interface for batch master screen.
 * 
 * @author Manego
 * 
 */
public interface IST33040BatchMasterService {

	/**
	 * This service operation us to get the priority list define in master data.
	 * 
	 * @return - priority list.
	 */
	Map<Integer, String> retrieveBatchMasterPriority();

	/**
	 * This service operation use to read the concurrency list.
	 * 
	 * @return concurrency list.
	 */
	Map<Integer, String> retrieveBatchMasterConcurrency();

	/**
	 * This service operation is use to find the BatchMaster entity for given
	 * batch id.
	 * 
	 * @param projectCode
	 *            A projectCode reference to find batch master details
	 * @param batchId
	 *            A batch id to find batch master.
	 * @return A batch master object associated with given batch id.
	 */
	BatchMaster getBatchMaster(String projectCode, String batchId);

	/**
	 * This service operation is use to query batch master details based on
	 * given batchId, batchName, projectCode, priorityLevel and concurrency as a
	 * search criteria input. operation fill all details in BatchMaster object
	 * and give it to repository to list the batch master details.
	 * 
	 * @param batchId
	 *            A batch id to find the details.
	 * @param batchName
	 *            A batch name to find the details.
	 * @param projectCode
	 *            A project code to find the details.
	 * @param priorityLevel
	 *            A priority level.
	 * @param concurrency
	 *            A concurrency flag.
	 * @return List of {@link BatchMaster}
	 */
	List<BatchMaster> listBatchInfo(String batchId, String batchName,
			String projectCode, int priorityLevel, int concurrency);

	/**
	 * This service operation saves the given input in database.
	 * 
	 * @param batchId
	 *            A batchId input
	 * @param projectCode
	 *            A project code input.
	 * @param batchName
	 *            A batch name input
	 * @param priorityLevel
	 *            A priority input.
	 * @param concurrencyFlag
	 *            A concurrency input.
	 * @param runningCount
	 *            It set to zero.
	 * @param runAs
	 *            The owner input.
	 * @param shell
	 *            The sell path input.
	 * @param supportId
	 *            The support id input.
	 * @param userId
	 *            The user id input.
	 * @throws SystemAlreadyExistsException
	 */
	void addBatchMasterInfo(String batchId, String projectCode,
			String batchName, int priorityLevel, int concurrencyFlag,
			int runningCount, String runAs, String shell, String supportId,
			String userId) throws SystemAlreadyExistsException;

	/**
	 * This service operation use to update the batch master details.
	 * 
	 * @param batchId
	 *            A batch id to update.
	 * @param projectCode
	 *            A project code to update.
	 * @param batchName
	 *            A batch name to update.
	 * @param priorityLevel
	 *            A priority to update.
	 * @param concurrencyFlag
	 *            A concurrency to update.
	 * @param runAs
	 *            A owner to update.
	 * @param shell
	 *            A sell path to update.
	 * @param supportId
	 *            A support id to update.
	 * @param userId
	 *            A user id to update.
	 * @param updateDt
	 *            The update date for concurrency update check.
	 * @throws ConcurrencyException
	 *             - if the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             - if the batch master details does exists in database for
	 *             given batch id
	 */
	void updateBatchMasterInfo(String batchId, String projectCode,
			String batchName, int priorityLevel, int concurrencyFlag,
			String runAs, String shell, String supportId, String userId,
			Date updateDt) throws ConcurrencyException,
			SystemDoesNotExistsException;

	/**
	 * Delete the batch master for given project code and batch id.
	 * 
	 * @param projectCode
	 *            A project code reference.
	 * @param batchID
	 *            A batch id reference.
	 * @param updateDt
	 * @throws ConcurrencyException
	 *             If the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             If the batch master details does exists in database for
	 *             given batch id
	 */
	void deleteBatchMasterInfo(String projectCode, String batchID, Date updateDt)
			throws ConcurrencyException, SystemDoesNotExistsException;

	/**
	 * This operation write the search result in excel file.
	 * 
	 * @param batchId
	 *            A batch id search
	 * @param batchName
	 *            A batch name search.
	 * @param projectCode
	 *            A project code search.
	 * @param priorityLevel
	 *            A priority level search.
	 * @param concurrencyFlag
	 *            A concurrencyFlag search.
	 * @return the excel object.
	 * @throws SystemDoesNotExistsException
	 *             If the batch master details does exists in database for
	 *             given batch id & project code.
	 * @throws UnableToCreateExcelForDowloadException
	 *             In case of any other exception while creating the excel
	 *             instance.
	 */
	HSSFWorkbook listBatchMasterInfoToExcel(String batchId, String batchName,
			String projectCode, int priorityLevel, int concurrencyFlag)
			throws SystemDoesNotExistsException,
			UnableToCreateExcelForDowloadException;
}
