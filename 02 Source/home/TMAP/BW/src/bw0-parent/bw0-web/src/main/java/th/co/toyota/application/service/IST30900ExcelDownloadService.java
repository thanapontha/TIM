/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  IST30900ExcelDownloadService.java
 * Program Description	    :  Online Excel Download Service.
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  April 8, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import th.co.toyota.application.web.form.CST30900ExcelDownloadForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.OverLimitException;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.TableRoleMap;

/**
 * Service inteface use to serve the Excel download sceen requests.
 * 
 * @author Sira
 * 
 */
public interface IST30900ExcelDownloadService {

	/**
	 * Retrieve items from "TB_M_SETTING_INFO" according to user role.
	 * 
	 * @param roleId
	 *            A role id
	 * @param selectedTableName
	 *            A table or view name
	 * @return List of {@link SettingInfo}
	 */
	// Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	List<SettingInfo> getBookmarkList(List<String> roleId,
			String selectedTableName);

	/**
	 * Retrieve items from "TB_M_TABLE_ROLE_MAP" Table according to user role.
	 * 
	 * @param roleId
	 *            A role list.
	 * @return List of {@link TableRoleMap}
	 */
	List<TableRoleMap> getTableList(List<String> roleId);

	/**
	 * System retrieve meta data of the selected Table / View from database and
	 * display in detail list on the screen.
	 * <ul>
	 * <li>Display all Column names of target Table/View in "Field" column on
	 * the screen.
	 * <li>The Data Type of each column will be displayed in "Data Type" column
	 * on the screen.
	 * <li>For the columns which are primary key of selected Table, system will
	 * display "PK" in "PK" column on the screen.
	 * <li>For more information about items specification, please refer to
	 * "Item Desc" sheet.
	 * </ul>
	 * 
	 * @param tableName
	 *            A table name.
	 * @return {@link SettingInfo}
	 * @throws SQLSyntaxErrorException
	 *             If SQL system not valid
	 * @throws ClassNotFoundException
	 *             If table/model not found.
	 */
	List<SettingInfo> loadTableViewInfo(String tableName)
			throws SQLSyntaxErrorException, ClassNotFoundException;

	/**
	 * Retrieve to find all BookMark details for given user roles and setting
	 * id.
	 * 
	 * @param roleId
	 *            A list of roles
	 * @param settingId
	 *            A setting id.
	 * @return List of {@link SettingInfo}
	 */
	List<SettingInfo> getBookmarkDetails(List<String> roleId, String settingId);

	/**
	 * Retrieve system master to check the online download or on demand download
	 * access for the application. Access can check based on the {@code code}
	 * value.
	 * 
	 * @param mandatoryFlag
	 *            A flag
	 * @param code
	 *            A code
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> querySystemMaster(boolean mandatoryFlag, String code);

	/**
	 * Perform save value on the screen insert into "tb_m_setting_info" table.
	 * <ul>
	 * <li>If "Report Name " is blank , system will set BookMark id =Table
	 * name_yyMMddhhmm
	 * <li>If "Report Name " has values , system will set BookMark id =Values in
	 * Report Name_yyMMddhhmm
	 * </ul>
	 * 
	 * @param settingInfo
	 *            A list of {@link SettingInfo}
	 * @param list 
	 * @return true of saved.
	 */
	boolean saveBookmark(List<SettingInfo> settingInfo, List<String> roles);

	/**
	 * Return the total number of records details for on demand download. The
	 * total records should be display on user screen.
	 * 
	 * @param form
	 *            A {@link CST30900ExcelDownloadForm}
	 * @return A total record count.
	 * @throws ClassNotFoundException
	 *             If the model not found.
	 * @throws OverLimitException
	 *             If it exceeds the maximum row limit(only for online download)
	 * @throws NoDataFoundException
	 *             If no records found.
	 */
	int getRecordCount(CST30900ExcelDownloadForm form)
			throws ClassNotFoundException, OverLimitException,
			NoDataFoundException;

	/**
	 * Generate report in the form of Excel (xls/xlsx) or CSV for online
	 * download request.
	 * 
	 * @param form
	 *            A {@link CST30900ExcelDownloadForm}
	 * @param userInfo
	 *            A {@link CSC22110UserInfo}
	 * @param userRoles
	 *            A user role list.
	 * @return A generated report name.
	 * @throws Exception
	 *             if report generation fails.
	 */
	String generateReport(CST30900ExcelDownloadForm form,
			CSC22110UserInfo userInfo, List<String> userRoles) throws Exception;

	/**
	 * Online application invoke CST30090ExcelGenerator API to post the On
	 * Demand batch. This service operation helps to post the OnDemand batch
	 * request.
	 * 
	 * 
	 * @param form
	 *            A {@link CST30900ExcelDownloadForm}
	 * @param userInfo
	 *            A current user information.
	 * @param userRoles
	 *            A list of current user roles.
	 * @return Generated ODB report name.
	 * @throws Exception
	 *             In case of request fails.
	 */
	String requestODBReport(CST30900ExcelDownloadForm form,
			CSC22110UserInfo userInfo, List<String> userRoles) throws Exception;

	// FIXME: method should not write in service class.
	@Deprecated
	List<String> validateForm(MessageSource messageSource, Locale locale,
			CST30900ExcelDownloadForm form);
}
