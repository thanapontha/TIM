/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST33010LogMonitoringRepository.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 2, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.util.Date;
import java.util.List;

import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;

/**
 * Repository interface for log monitoring screen.
 * 
 * @author danilo
 * @author m.valdeo
 * 
 */
public interface IST33010LogMonitoringRepository {
	/**
	 * Query on "TB_M_MODULE_H" table to find all module id's
	 * 
	 * @return List of {@link ModuleHeaderInfo}
	 */
	List<ModuleHeaderInfo> queryModules();

	/**
	 * Query on "TB_L_LOGGER" table to find log details as per input module id,
	 * function id, user id, application id, status and message type (stored in
	 * {@link LogInfo} by given date range.
	 * 
	 * @param log
	 *            A input log details.
	 * @param dateFrom
	 *            A from date.
	 * @param dateTo
	 *            A to date.
	 * @return List of {@link LogInfo}
	 */
	List<LogInfo> queryLog(LogInfo log, Date dateFrom, Date dateTo);


	/**
	 * Query on TB_L_LOGGER table to either find total number of Header logs or
	 * Get the list of Header Application Ids to be displayed depending on the 
	 * parameters below 
	 * 
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
	 * @return List of App Id - Max List No.: 10
	 */
	List<Integer> queryLogGroup(LogInfo logInfo, Date dateFrom, Date dateTo, 
			int firstResult, int rowsPerPage);

	/**
	 * Query on TB_L_LOGGER table to get the actual Header logs to be displayed  
	 * depending the parameters below including certain application id based on 
	 * initial search
	 * 
	 * @param logInfo
	 * 				Search filter log details 
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @param appIdList
	 * 				Arraylist of App Id based from initial search
	 * @return List of {@link LogInfo} - Header logs
	 */
	List<LogInfo> queryHeaderLog(LogInfo logInfo, Date dateFrom, Date dateTo, 
			List<Integer> appIdList);

	/**
	 * Query on TB_L_LOGGER table to find total number of Detail logs
	 * 
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
	 * @return List of {@link LogInfo} - Detail logs
	 */
	List<LogInfo> queryDetailLog(LogInfo logInfo, Date dateFrom, Date dateTo, 
			int firstResult, int rowsPerPage);

	/**
	 * Query on TB_L_LOGGER table to get the actual Detail logs to be displayed 
	 * on page
	 * 
	 * @param logInfo
	 * 				Search filter log details
	 * @param dateFrom
	 * 				Search filter date range(from)
	 * @param dateTo
	 * 				Search filter date range(to)
	 * @return Total no. of detail count
	 */
	int queryDetailCount(LogInfo logInfo, Date dateFrom, Date dateTo);
}
