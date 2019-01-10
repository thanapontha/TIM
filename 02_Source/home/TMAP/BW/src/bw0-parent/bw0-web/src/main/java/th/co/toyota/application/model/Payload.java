/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.model
 * Program ID 	            :  Payload.java
 * Program Description	    :  A applcation data load.
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Feb 28, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.model;

import java.util.List;
import java.util.Map;

import th.co.toyota.bw0.common.form.CommonBaseForm;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.st3.api.model.BatchMaster;
import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;
import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;

/**
 * API is load by every controller request to set up or initialize the screen.
 * 
 * @author danilo
 * 
 */
public interface Payload {
	String getDateTimeNow();

	String getDateTimeNowSec();

	String getScreenId();

	void setScreenId(String id);

	String getScreenDescription();

	void setScreenDescription(String desc);

	ServiceStatus getStatus();

	void setStatus(ServiceStatus status);

	List<String> getErrorMessages();

	void addErrorMessage(String message);

	void addErrorMessages(List<String> messages);

	List<String> getInfoMessages();

	void addInfoMessage(String message);

	void addInfoMessages(List<String> messages);

	List<String> getWarningMessages();

	void addWarningMessage(String message);

	void addWarningMessages(List<String> messages);

	Boolean getHaveMessages();

	// Start Log Monitoring
	List<ModuleHeaderInfo> getModules();

	void setModules(List<ModuleHeaderInfo> modules);

	List<LogInfo> getLogs();

	void setLogs(List<LogInfo> logs);

	String getFocusId();

	void setFocusId(String id);
	
	List<List<LogInfo>> getGroupedLogs();
	List<List<String[]>> getGroupedLogsDisplay();

	void setGroupedLogs(List<List<LogInfo>> logs);
	void setGroupedLogsDisplay(List<List<String[]>> logs);

	// End Log Monitoring

	// Start System Master
	List<SystemInfo> getListCategory();

	void setListCategory(List<SystemInfo> listCategory);

	// End System Master

	// Start Batch Master
	List<BatchMaster> getBatchMasterList();

	void setBatchMasterList(List<BatchMaster> listBatchMaster);

	// End System Master

	// Start Batch Status
	List<?> getBatchStatusList();

	void setBatchStatusList(List<?> listBatchStatus);

	String getAppLogURL();

	void setAppLogURL(String appLogURL);

	// End System Status

	// Start Excel Download Monitoring
	List<ExcelDownloadFile> getExcelDownloadFileList();

	void setExcelDownloadFileList(List<ExcelDownloadFile> excelDownloadFileList);

	List<ExcelDownloadStatus> getExcelDownloadStatusList();

	void setExcelDownloadStatusList(
			List<ExcelDownloadStatus> excelDownloadStatusList);

	ExcelDownloadStatus getExcelDownloadStatus();

	void setExcelDownloadStatus(ExcelDownloadStatus excelDownloadStatus);

	ExcelDownloadFile getExcelDownloadFile();

	void setExcelDownloadFile(ExcelDownloadFile excelDownloadFile);

	Map<Character, String> getReportStatusData();

	void setReportStatusData(Map<Character, String> reportStatusData);

	// End Excel Download Monitoring

	// about screen
	String getEnhancement();

	void setEnhancement(String enhancement);

	// end about screen.
	
	// START: data table
	void setDataList(List<List<String>> dataList);
	List<List<String>> getDataList();
	// END: data table

	List<SettingInfo> getBookmarksList();

	void setBookmarksList(List<SettingInfo> settingInfo);

	List<SystemInfo> getMandatoryFieldList();

	void setMandatoryFieldList(List<SystemInfo> mandatoryFieldList);

	int getTotalRecord();

	void setTotalRecord(int totalRecord);

	// START: pagination
	int getFirstResult();

	void setFirstResult(int firstResult);

	int getRowsPerPage();

	void setRowsPerPage(int rowsPerPage);

	int getTotalRows();

	void setTotalRows(int totalRows);
	// END: pagination
	List getObjectsInfoList();
	void setObjectsInfoList(List objectsInfoList);
	
	CommonBaseForm getObjectForm();
	void setObjectForm(CommonBaseForm form);
	
	List getObjectsInfo2List();
	void setObjectsInfo2List(List objectsInfo2List);
	
	/*
	 * For Operation Common screen
	 */
	void setScreenMode(String screenMode);
	String getScreenMode();

	List<String> getErrorHighlight();
	void setErrorHighlight(List<String> errorHighlight);
	
	String getJsonProcessCurrent();
	void setJsonProcessCurrent(String jsonProcessCurrent);
	public String getAppId();
	public void setAppId(String aplid);
	public String getAppId2();
	public void setAppId2(String aplid2);
	
	public void setCriterias(Map<String, List<ComboValue>> criterias);
	public Map<String, List<ComboValue>> getCriterias();
	
	public void setFields(Map<String, String> params);
	public Map<String, String> getFields();
}
