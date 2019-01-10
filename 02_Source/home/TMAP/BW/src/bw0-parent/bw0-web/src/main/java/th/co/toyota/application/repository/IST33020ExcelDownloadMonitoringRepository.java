/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33020ExcelDownloadMonitoringRepository.java
 * Program Description	    :  Excel download monitor repository.
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
package th.co.toyota.application.repository;

import java.util.Date;
import java.util.List;

import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadFileId;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;
import th.co.toyota.st3.api.model.ODBRoles;

/**
 * Repository interface for Excel download monitoring screen.
 * 
 * @author Thanawut T.
 * 
 */
public interface IST33020ExcelDownloadMonitoringRepository {

	/**
	 * Query to load all excel download status.
	 * 
	 * @return list of {@link ExcelDownloadStatus}
	 */
	List<ExcelDownloadStatus> queryExcelDownloads();

	/**
	 * Query to load excel download status based on given search criteria.
	 * 
	 * @param startRequestDate
	 *            A initial to to find results.
	 * @param endRequestDate
	 *            A last date to find the results.
	 * @param status
	 *            A search by status.
	 * @param reportName
	 *            A search by report name.
	 * @param roleList
	 *            List of user roles.
	 * @return List of {@link ExcelDownloadStatus}
	 */
	List<ExcelDownloadStatus> queryExcelDownloads(Date startRequestDate,
			Date endRequestDate, Integer status, String reportName,
			List<String> roleList);

	/**
	 * Verify the given list of roles has admin privileges.
	 * 
	 * @param roleList User role list.
	 * @return true if has admin permissions.
	 */
	boolean isAdmin(List<String> roleList);

	/**
	 * Find details of Excel download file by given file id.
	 * 
	 * @param excelDownloadFileId
	 *            A file id.
	 * @return {@link ExcelDownloadFile} in details.
	 */
	ExcelDownloadFile findExcelDownloadFile(
			ExcelDownloadFileId excelDownloadFileId);

	/**
	 * Find the excel download files using given document id.
	 * 
	 * @param docId
	 *            A document id.
	 * @return List of {@link ExcelDownloadFile}
	 */
	List<ExcelDownloadFile> findExcelDownloadFileWithDocId(String docId);

	/**
	 * Find excel download status by given document id.
	 * 
	 * @param docId
	 *            A document id to find status.
	 * @return A download status {@link ExcelDownloadStatus}
	 */
	ExcelDownloadStatus findExcelDownloadStatus(String docId);

	/**
	 * Delete excel download status.
	 * 
	 * @param status
	 *            A excel download status to delete.
	 */
	void deleteExcelDownload(ExcelDownloadStatus status);

	/**
	 * Delete excel download file information.
	 * 
	 * @param excelDownloadFile
	 *            A {@link ExcelDownloadFile} to delete.
	 */
	void deleteExcelDownload(ExcelDownloadFile excelDownloadFile);

	/**
	 * Cancel Excel Download file. It will change file status to cancel.
	 * 
	 * @param excelDownloadFile
	 *            A {@link ExcelDownloadFile} to cancel.
	 */
	void cancelExcelDownload(ExcelDownloadFile excelDownloadFile);

	/**
	 * Update excel download status information.
	 * 
	 * @param exDownload
	 *            {@link ExcelDownloadFile} to update.
	 */
	void updateExcelDownloadStatus(ExcelDownloadFile exDownload);

	/**
	 * List the excel download status based on given search criteria.
	 * 
	 * @param startRequestDate
	 *            A initial to to find results.
	 * @param endRequestDate
	 *            A last date to find the results.
	 * @param status
	 *            A search by status.
	 * @param reportName
	 *            A search by report name.
	 * @param userId
	 *            A user id.
	 * @return List of {@link ExcelDownloadStatus}
	 */
	List<ExcelDownloadStatus> queryExcelDownloads(Date startRequestDate,
			Date endRequestDate, Integer status, String reportName,
			String userId);

	/**
	 * List the ODB roles.
	 * 
	 * @return List of {@link ODBRoles}
	 */
	List<ODBRoles> findRolesForExcelDownloadStatus();
}
