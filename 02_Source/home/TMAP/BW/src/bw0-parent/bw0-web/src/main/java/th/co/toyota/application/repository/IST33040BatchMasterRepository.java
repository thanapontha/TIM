/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33040BatchMasterRepository.java
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
package th.co.toyota.application.repository;

import java.util.List;

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.BatchMaster;

/**
 * This repository interface use by the batch master screen to interact with database and search,
 * add, update, delete the batch master details.
 * 
 * @author Manego
 * 
 */
public interface IST33040BatchMasterRepository {

	/**
	 * This operation use to read the priority list. 
	 * TODO: move this code to common repository
	 * 
	 * @return priority list.
	 */
	List<String> queryBatchMasterPriority();

	/**
	 * This operation use to read the concurrency list. 
	 * TODO: move this code to common repository
	 * 
	 * @return concurrency list.
	 */
	List<String> queryBatchMasterConcurrency();

	/**
	 * This operation is use to query batch master details based on given
	 * BatchMaster object as a search criteria.
	 * 
	 * @param batchMasterSearchCriteria
	 *            A serach criteria for batch master result.
	 * @return A list if batch master objects found for given search criteria.
	 */
	List<BatchMaster> queryBatchMaster(BatchMaster batchMasterSearchCriteria);

	/**
	 * This operation is use to find the BatchMaster entity for given batch id.
	 * 
	 * @param projectCode
	 *            A code - to find the batch master.
	 * @param batchId
	 *            A batch id to find batch master.
	 * @return A batch master object associated with given batch id.
	 */
	BatchMaster findBatchMasterInfo(String projectCode, String batchId);

	/**
	 * This operation use to add/save given batch master information in table.
	 * 
	 * @param batchMasterInfo
	 *            A batch master information to add.
	 * @throws SystemAlreadyExistsException
	 *             If the batch master details does exists in database for
	 *             given batch id
	 */
	void addBatchMaster(BatchMaster batchMasterInfo)
			throws SystemAlreadyExistsException;

	/**
	 * This operation use to update given batch master information in database.
	 * 
	 * @param batchMasterInfo
	 *            A batch master information to add.
	 * @throws ConcurrencyException
	 *             If the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             If the batch master details does exists in database for
	 *             given batch id
	 */
	void updateBatchMaster(BatchMaster batchMasterInfo)
			throws ConcurrencyException, SystemDoesNotExistsException;

	/**
	 * This operation use to delete the batch master details for given batch id
	 * proved in BatchMaster object as input.
	 * 
	 * @param batchMaster
	 *            A batch master information to delete.
	 * @throws ConcurrencyException
	 *             If the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             If the batch master details does exists in database for
	 *             given batch id
	 */
	void deleteBatchMaster(BatchMaster batchMaster)
			throws ConcurrencyException, SystemDoesNotExistsException;
}