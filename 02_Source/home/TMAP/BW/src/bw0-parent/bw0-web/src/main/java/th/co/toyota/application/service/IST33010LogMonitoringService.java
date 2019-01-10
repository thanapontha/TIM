/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST33010LogMonitoringService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 2, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import th.co.toyota.st3.api.exception.EntityMappingException;
import th.co.toyota.st3.api.exception.LogsDoesNotExistsException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.exception.UnsupportedQuerySyntaxException;
import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;

/**
 * Service interface for log monitoring screen.
 * 
 * @author danilo
 * 
 */
public interface IST33010LogMonitoringService {
	/**
	 * Log monitoring initial screen, auto checks the module id's. This
	 * operation is use to find all module id's display on screen.
	 * 
	 * @return List of {@link ModuleHeaderInfo}
	 */
	List<ModuleHeaderInfo> queryModules();

	/**
	 * This operation will search the log details using the entered selection
	 * criteria.
	 * <ul>
	 * <li>For date range are entered, the search result should display all
	 * records satisfying the selection criteria within the date range.
	 * <li>For user id is entered, the selection criteria should include the
	 * user ID.
	 * <li>For application id is entered, the selection criteria should include
	 * the app ID.
	 * <li>For status is entered, the selection criteria should include the
	 * specified status.
	 * <li>For message type is entered, the selection criteria should include
	 * the message type.
	 * </ul>
	 * <note>Note: all section criteria is stored in {@link LogInfo} along with
	 * the respective module id and function to find the log details. </note>
	 * 
	 * @param logInfo A {@link LogInfo}
	 * @param dateFrom A form date
	 * @param dateTo A to date.
	 * @return List of {@link List<LogInfo>}
	 * @throws LogsDoesNotExistsException
	 */
	List<List<LogInfo>> searchLog(LogInfo logInfo, Date dateFrom, Date dateTo)
			throws LogsDoesNotExistsException;
	
	
	List<List<String[]>> searchLogDisplay(LogInfo logInfo, Date dateFrom,
			Date dateTo) throws LogsDoesNotExistsException;

	/**
	 * If search result is found, Excel file will be created for display result.
	 * User can download the generated excel file.
	 * 
	 * @param logInfo A {@link LogInfo}
	 * @param dateFrom A from date.
	 * @param dateTo A to date.
	 * @return A excel sheet {@link HSSFWorkbook}
	 * @throws LogsDoesNotExistsException If log details does not found
	 * @throws UnableToCreateExcelForDowloadException If fails to create the excel report.
	 */
	HSSFWorkbook listRolesToExcelXLS(LogInfo logInfo, Date dateFrom, Date dateTo)
			throws LogsDoesNotExistsException,
			UnableToCreateExcelForDowloadException;
	
	String listRolesToExcelXLSX(LogInfo logInfo, Date dateFrom, Date dateTo,
			String reportName) throws LogsDoesNotExistsException,
			UnableToCreateExcelForDowloadException, EntityMappingException,
			ClassNotFoundException, NoDataFoundException,
			UnsupportedQuerySyntaxException, IOException;

	// START - pagination
	/**
	 * @param logInfo
	 * 				Search filter log details
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @param firstResult
	 * 				Starting range of no. of records to be searched
	 * 				Starts from 0, incrementing depending on rowsPerPage
	 * @param rowsPerPage
	 * 				No. of rows to be displayed in page
	 * 				Default: 10
	 * @return 
	 * 				List of {@link LogInfo} - Header logs
	 * @throws LogsDoesNotExistsException
	 */
	List<List<LogInfo>> searchHeaderLog(LogInfo logInfo, Date dateFrom, 
			Date dateTo, int firstResult, int rowsPerPage) throws LogsDoesNotExistsException;

	/**
	 * @param logInfo
	 * 				Search filter log details
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @return
	 * 				Total no. of header count
	 * @throws LogsDoesNotExistsException
	 */
	int searchHeaderCount(LogInfo logInfo, Date dateFrom, Date dateTo) 
			throws LogsDoesNotExistsException;
	
	/**
	 * @param log
	 * 				Search filter log details
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @return
	 * 				Total no. of detail count
	 * @throws LogsDoesNotExistsException
	 */
	int getLogDetailCount(LogInfo log, Date dateFrom, Date dateTo) throws LogsDoesNotExistsException;

	/**
	 * @param log
	 * 				Search filter log details
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @param firstResult
	 * 				Starting range of no. of records to be searched
	 * 				Starts from 0, incrementing depending on rowsPerPage
	 * @param rowsPerPage
	 * 				No. of rows to be displayed in page
	 * 				Default: 10
	 * @return List of String[] - Detail logs
	 * @throws LogsDoesNotExistsException
	 */
	List<List<String[]>> searchLogDisplay(LogInfo log, Date dateFrom, Date dateTo, 
			int firstResult, int rowsPerPage) throws LogsDoesNotExistsException;
	// END - pagination

	
}
