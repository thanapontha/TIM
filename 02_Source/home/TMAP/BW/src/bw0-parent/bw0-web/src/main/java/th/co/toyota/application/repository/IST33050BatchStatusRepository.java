/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33050BatchStatusRepository.java
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
import java.util.Map;

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.BatchStatusLog;

/**
 * repository use by the batch status screen to interact with database to find,
 * search, delete, cancel batch status details.
 * 
 * @author Manego
 * 
 */
public interface IST33050BatchStatusRepository {

	/**
	 * this operation is use to query batch status details based on given
	 * BatchMaster object as a search criteria.
	 * 
	 * @param batchStatusSearchCriteria
	 *            A serach criteria for batch master result.
	 * @return A list if batch master objects found for given search criteria.
	 */
	List<Map<String, Object>> queryBatchStatusLog(
			Map<String, Object> batchStatusSearchCriteria);

	/**
	 * this operation is use to find the BatchQueue entity for given queue no.
	 * 
	 * @param queueNo
	 *            - queueNo to find batch queue info.
	 * @return a batch master object associated with given batch id.
	 */
	BatchQueue findBatchQueueInfo(String batchId, Integer queueNo);

	/**
	 * this operation is use to find the BatchStatusLog entity for given batch
	 * log id.
	 * 
	 * @param batchId
	 *            A batch log id to find batch master.
	 * @param seqNo
	 *            A sequence number.
	 * @return a batch master object associated with given batch id.
	 */
	BatchStatusLog findLatestBatchStatusLog(String batchId, Integer seqNo);

	/**
	 * this operation use to delete the batch status details for given batch
	 * status id provided in BatchStatusLog object as input.
	 * 
	 * @param batchStatusLog
	 *            - batch master information to delete.
	 * @throws ConcurrencyException
	 *             - if the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             - if the batch master details does exists in database for
	 *             given batch id
	 */
	void deleteBatchStatus(BatchStatusLog batchStatusLog)
			throws ConcurrencyException, SystemDoesNotExistsException;

	/**
	 * this operation use to delete the batch status log details for given batch
	 * id and the serial number provided in input. It will all batch log details
	 * associated with given batchId and seqNo.
	 * 
	 * @param batchId
	 *            - batch id reference to delete
	 * @param seqNo
	 *            - serial number reference to delete.
	 * @throws ConcurrencyException
	 * @throws SystemDoesNotExistsException
	 *             - throws if details found.
	 */
	void deleteBatchStatus(String batchId, Integer seqNo)
			throws SystemDoesNotExistsException;

	/**
	 * this operation is use to delete the batch queue details for given batch
	 * queue number associated with given BatchQueue instance.
	 * 
	 * @param batchQueue
	 *            A queue reference to delete.
	 * @throws SystemDoesNotExistsException
	 *             - throws if details not found.
	 */
	void cancelBatchStatus(BatchQueue batchQueue)
			throws SystemDoesNotExistsException;

	/**
	 * operation use to add the batch queue information.This operation may use
	 * by test case, however it is not requred for batch status screen.
	 * 
	 * @param batchQueueInfo
	 *            - batch queue information to add.
	 * @throws SystemAlreadyExistsException
	 *             - throws if system already exists.
	 */
	int addBatchQueue(final BatchQueue batchQueueInfo)
			throws SystemAlreadyExistsException;

	/**
	 * operation use to add the batch status log details. This operation may use
	 * by test case, however it is not required for batch status screen.
	 * 
	 * @param batchStatusLog
	 *            - batch status log to add.
	 * @throws SystemAlreadyExistsException
	 *             - throws if system already exists.
	 */
	void addBatchLog(final BatchStatusLog batchStatusLog)
			throws SystemAlreadyExistsException;
}