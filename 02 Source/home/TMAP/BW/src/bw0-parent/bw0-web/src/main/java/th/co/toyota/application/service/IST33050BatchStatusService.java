/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST33050BatchStatusService.java
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

import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;

/**
 * A service interface for batch status screem.
 * 
 * @author Manego
 * 
 */
public interface IST33050BatchStatusService {

	/**
	 * This service operation is use to query batch status details based on
	 * given batchId, batchName, projectCode, runDate and runBy as a search
	 * criteria input. operation find all details in Map object(search criteria)
	 * and give it to repository to list the batch status details in Map object.
	 * 
	 * @param batchId
	 *            A batch id to find the details.
	 * @param batchName
	 *            A batch name to find the details.
	 * @param projectCode
	 *            A project code to find the details.
	 * @param runDate
	 *            Run date to find the status details
	 * @param runBy
	 *            The owner to find the status details
	 * @return List of BatchStatus value Map.
	 */
	List<Map<String, Object>> listBatchStatus(String batchId, String batchName,
			String projectCode, String runDate, String runBy);

	/**
	 * Delete the batch status for given batch sequence id. service operation
	 * does not delete on queue & processing batch's.
	 * 
	 * @param batchSerialNo
	 *            Batch sequence number.
	 * @param batchId
	 *            A batch id reference.
	 * @param updateDt
	 *            A update date.
	 * @throws ConcurrencyException
	 *             If the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             If the batch status details does exists in database for given
	 *             batch id
	 */
	ServiceStatus deleteBatchStatus(int batchSerialNo, String batchId,
			Date updateDt) throws ConcurrencyException,
			SystemDoesNotExistsException;

	/**
	 * Cancel the batch status for given batch sequence id. service operation
	 * cancels only on queue status batch.
	 * 
	 * @param batchSerialNo
	 *            Batch sequence number.
	 * @param batchId
	 *            A batch id reference.
	 * @param updateDt
	 *            A update date.
	 * @throws ConcurrencyException
	 *             If the concurrent update happen.
	 * @throws SystemDoesNotExistsException
	 *             If the batch status details does exists in database for given
	 *             batch id
	 */
	ServiceStatus cancelBatchStatus(int batchSerialNo, String batchId,
			Date updateDt) throws ConcurrencyException,
			SystemDoesNotExistsException;

	/**
	 * Cancel the batch status for given batch sequence id.
	 * 
	 * @param batchId
	 *            A batch id reference.
	 * @throws SystemIsNotActiveException
	 *             If system master is not active..
	 * @throws SystemDoesNotExistsException
	 *             If the batch log details does exists in database for given
	 *             batch id
	 */
	String detailedBatchStatus(final String batchId)
			throws SystemDoesNotExistsException, SystemIsNotActiveException;

	/**
	 * It write the search result in excel file.
	 * 
	 * @param batchId
	 *            A batch id search
	 * @param batchName
	 *            A batch name search.
	 * @param projectCode
	 *            A project code search.
	 * @param runDate
	 *            A batch run date
	 * @param runBy
	 *            A owner od.
	 * @return The excel object.
	 * @throws SystemDoesNotExistsException
	 *             If the batch status details does exists in database for given
	 *             batch id & project code.
	 * @throws UnableToCreateExcelForDowloadException
	 *             In case of any other exception while creating the excel
	 *             instance.
	 */
	HSSFWorkbook listBatchStatusToExcel(String batchId, String batchName,
			String projectCode, String runDate, String runBy)
			throws SystemDoesNotExistsException,
			UnableToCreateExcelForDowloadException;
}