/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33020ExcelDownloadMonitoringService.java
 * Program Description	    :  Excel download monitoring service.
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  Apr 28, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.util.List;

import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.FileProcessingException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;

/**
 * A service interface to serve user request for excel download monitring
 * screen.
 * 
 * @author Thanawutt
 */
public interface IST33020ExcelDownloadMonitoringService {

	/**
	 * Retrieve all Excel download status details.
	 * 
	 * @return Lis of all {@link ExcelDownloadStatus}
	 * @throws NoDataFoundException
	 */
	List<ExcelDownloadStatus> queryExcelDownloads() throws NoDataFoundException;

	/**
	 * Retrieve records according to criteria.
	 * <ul>
	 * <li>In case normal user log-in, retrieve record(s) of only log in user.
	 * Join with table TB_R_ODB_ROLES
	 * <li>In case super admin log-in, retrieve record(s) of all users. Join
	 * with table TB_R_ODB_ROLES
	 * </ul>
	 * 
	 * @param requestDate
	 *            Request date.
	 * @param status
	 *            a status to download the {@link ExcelDownloadStatus}
	 * @param reportName
	 *            A report name to search.
	 * @param roleList
	 *            A list roles.
	 * @return List of {@link ExcelDownloadStatus}
	 * @throws NoDataFoundException
	 */
	List<ExcelDownloadStatus> queryExcelDownloads(String requestDate,
			Integer status, String reportName, List<String> roleList)
			throws NoDataFoundException;

	/**
	 * Create a new {@link ExcelDownloadFile} excel download status instance by
	 * document no and file no.
	 * <p>
	 * Operation will search find the status of excel download for provided
	 * document no and create {@link ExcelDownloadFile} based on its status and
	 * return it.
	 * <p>
	 * If file no is valid and the details for given file
	 * {@link ExcelDownloadFile} present in db, then return the same
	 * {@link ExcelDownloadFile} instance.
	 * 
	 * @param docNo A document number
	 * @param fileNo A file no.
	 * @return A {@link ExcelDownloadFile} file.
	 */
	ExcelDownloadFile findReport(String docNo, int fileNo);

	/**
	 * This operation use to delete the details of given
	 * {@link ExcelDownloadFile} from database.
	 * 
	 * @param exDownload A {@link ExcelDownloadFile}
	 * @return A {@link ServiceStatus} (OK or NG)
	 * @throws ConcurrencyException
	 *             In case of concurrent deletion.
	 * @throws FileProcessingException
	 *             If unable to delete the file from server due the right
	 *             permissions.
	 */
	ServiceStatus deleteExcelDownload(ExcelDownloadFile exDownload)
			throws ConcurrencyException, FileProcessingException;

	/**
	 * Operation use to set file download status and the generated all report
	 * file status to cancel i.e. 4.
	 * 
	 * @param exDownload
	 *            {@link ExcelDownloadFile} to cancel.
	 * @return A cancelation status.
	 */
	ServiceStatus cancelExcelDownload(ExcelDownloadFile exDownload);

	/**
	 * Operation use to find the excel download status by given document number.
	 * 
	 * @param docId A document number.
	 * @return A {@link ExcelDownloadStatus}
	 */
	ExcelDownloadStatus findStatus(String docId);

	/**
	 * Retrieve Excel download status by given user id.
	 * 
	 * @param userId
	 *            User id to search.
	 * @return List of {@link ExcelDownloadStatus}
	 * @throws NoDataFoundException
	 *             If no records found.
	 */
	List<ExcelDownloadStatus> listExcelDownloads(String userId)
			throws NoDataFoundException;

	/**
	 * Retrieve Excel download status based on given search criteria.
	 * 
	 * @param userId
	 *            User id to search.
	 * @param status
	 *            Excel download status to search
	 * @param reportName
	 *            Report name to search.
	 * @param requestDate
	 *            Request date to search.
	 * @return List of {@link ExcelDownloadStatus}
	 * @throws NoDataFoundException
	 *             If no records found.
	 */
	List<ExcelDownloadStatus> listExcelDownloads(String userId, Integer status,
			String reportName, String requestDate) throws NoDataFoundException;

	/**
	 * Operation use to check all roles defined in DB or not.
	 * 
	 * @return true if roles defined.
	 */
	boolean isRolesDefinedInDB();
}
