/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  IST30900ExcelDownloadRepository.java
 * Program Description	    :  Excel download repository.
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  April 8, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 * 1.1		   13/5/2015   Elaine           N/A			    Remove insertExcelDownloadStatus
 * 															and insertODBRole 
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.TableRoleMap;

/**
 * Repository interface use by excel download screen.
 * 
 * @author Sira
 * 
 */
public interface IST30900ExcelDownloadRepository {
	/**
	 * Table / View combo box: retrieve items from "TB_M_TABLE_ROLE_MAP" Table
	 * according to user role.
	 * 
	 * 
	 * @param roleId
	 *            List of roll id's
	 * @return table role map to display in combo box.
	 */
	List<TableRoleMap> getTableList(List<String> roleId);

	/**
	 * BookMarks combo box : retrieve items from "TB_M_SETTING_INFO" according
	 * to user role.
	 * 
	 * @param roleId
	 *            A user role list.
	 * @return Setting details.
	 */
	List<SettingInfo> getBookmarkList(List<String> roleId);

	/**
	 * Reload BookMarks combo box by retrieving items from "TB_M_SETTING_INFO"
	 * according to user role and selected Table/View
	 * 
	 * @param roleId
	 *            A user role list.
	 * @param selectedTableName
	 *            A selected table name.
	 * @return BookMarks details.
	 */
	// Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	List<SettingInfo> getBookmarkList(List<String> roleId,
			String selectedTableName);

	/**
	 * Reload BookMarks combo box by retrieving items from "TB_M_SETTING_INFO"
	 * according to user role and book-mark id.
	 * 
	 * @param roleId
	 *            A user role.
	 * @param bookmarkId
	 *            A book mark id.
	 * @return BookMarks details.
	 */
	// Get data of table result
	List<SettingInfo> getBookmarkDetails(List<String> roleId, String bookmarkId);

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
	 * 
	 * </ul>
	 * 
	 * @param tableName
	 *            A table/view name.
	 * @return A table/view list meta-data.
	 * @throws ClassNotFoundException
	 *             If model class not found
	 * @throws SQLSyntaxErrorException
	 *             If the table name not found.
	 */
	List<SettingInfo> getMetadata(String tableName)
			throws ClassNotFoundException, SQLSyntaxErrorException;

	/**
	 * Save the list of BookMarks in DB.
	 * 
	 * @param listBookmark
	 *            BookMarks to save.
	 * @return true if save successful, else false.
	 * @throws EntityExistsException
	 *             If the entity try to save already exists.
	 * @throws IllegalArgumentException
	 *             If the BookMark details are not valid.
	 * @throws TransactionRequiredException
	 *             If transaction is required but is not active.
	 */
	boolean saveBookmark(List<SettingInfo> listBookmark)
			throws EntityExistsException, IllegalArgumentException,
			TransactionRequiredException;

	/**
	 * Query for record count use to display on excel downalod load screen in
	 * case of on demand excel downalod.
	 * 
	 * @param strQuery A JPQL query syntax.
	 * @param params A query parameters.
	 * @param entryClassName A entity/model name.
	 * @return Total number of records exists in DB.
	 */
	int getRecordCount(String strQuery, Object[] params, String entryClassName);

	/**
	 * Query to system master to know the application level access to perform
	 * the on demand download or online download.
	 * 
	 * @param category A category.
	 * @param subCategory A sub-category.
	 * @param code A code value.
	 * @return List of {@link SystemInfo}
	 */
	List<SystemInfo> querySystemMaster(String category, String subCategory,
			String code);
}
