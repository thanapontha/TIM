package th.co.toyota.application.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import th.co.toyota.bw0.common.form.CommonBaseForm;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.st3.api.model.BatchMaster;
import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;
import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;

@XmlRootElement
public class XmlPayload implements Payload, Serializable {
	private static final long serialVersionUID = -6515504143197214112L;

	private String screenId;
	private String screenDescription;
	private ServiceStatus serviceStatus;

	private List<String> errorMessages;
	private List<String> infoMessages;
	private List<String> warningMessages;

	// Log Monitoring
	private List<ModuleHeaderInfo> modulesList;
	private List<LogInfo> logsList;
    private List<List<LogInfo>> groupLogs;
    private List<List<String[]>> groupLogsDisplay;
	private String focusId;

	// System Master
	private List<SystemInfo> listCategory;

	// Batch Master
	private List<BatchMaster> listBatchMaster;

	// Batch Status
	private List<?> listBatchStatus;
	private String appLogURL;

	// Excel Download Monitoring
	private List<ExcelDownloadFile> excelDownloadFileList;
	private List<ExcelDownloadStatus> excelDownloadStatusList;
	private ExcelDownloadStatus excelDownloadStatus;
	private ExcelDownloadFile excelDownloadFile;
	private Map<Character, String> reportStatusData;
	// Excen Download
	private List<SettingInfo> bookmarkList;
	private List<SystemInfo> mandatoryFieldList;
	private int totalRecord;
	// about screen
	private String enhancement;
	// Data Table
	private List<List<String>> dataList;
	
	private List objectsInfoList;
	private List objectsInfo2List;
	private CommonBaseForm objectForm;
	
	//Common Operation Screen
	private List<String> errorHighlight;
	
	/*
	 * Operation Common screen
	 */
	private String screenMode;
	private String jsonProcessCurrent;
	
	// pagination
	private int firstResult;
	private int rowsPerPage;
	private int totalRows;
	
	private String appId;
	private String appId2;
	
	private Map<String, List<ComboValue>> criterias;
	private Map<String, String> fields;

	public XmlPayload() {
		errorMessages = new ArrayList<String>();
		infoMessages = new ArrayList<String>();
		warningMessages = new ArrayList<String>();
		criterias = new HashMap<String, List<ComboValue>>();
		fields = new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getScreenId()
	 */
	@Override
	@XmlElement
	public String getScreenId() {
		return this.screenId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#setScreenId(java.lang.String)
	 */
	@Override
	public void setScreenId(String id) {
		this.screenId = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getScreenDescription()
	 */
	@Override
	@XmlElement
	public String getScreenDescription() {
		return this.screenDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setScreenDescription(java.lang
	 * .String)
	 */
	@Override
	public void setScreenDescription(String desc) {
		this.screenDescription = desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getStatus()
	 */
	@Override
	@XmlElement
	public ServiceStatus getStatus() {
		return this.serviceStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setStatus(th.co.toyota.application
	 * .model.ServiceStatus)
	 */
	@Override
	public void setStatus(ServiceStatus status) {
		this.serviceStatus = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getDateTimeNow()
	 */
	@Override
	@XmlElement
	public String getDateTimeNow() {
		return new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getDateTimeNowSec()
	 */
	@Override
	@XmlElement
	public String getDateTimeNowSec() {
		return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getHaveMessages()
	 */
	@Override
	@XmlElement
	public Boolean getHaveMessages() {
		if (!infoMessages.isEmpty() || !errorMessages.isEmpty()
				|| !warningMessages.isEmpty()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addErrorMessage(java.lang.String)
	 */
	@Override
	public void addErrorMessage(String message) {
		this.errorMessages.add(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addInfoMessage(java.lang.String)
	 */
	@Override
	public void addInfoMessage(String message) {
		this.infoMessages.add(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addWarningMessage(java.lang.String
	 * )
	 */
	@Override
	public void addWarningMessage(String message) {
		this.warningMessages.add(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getErrorMessages()
	 */
	@Override
	@XmlElement
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addErrorMessages(java.util.List)
	 */
	public void addErrorMessages(List<String> messages) {
		if (messages != null) {
			this.errorMessages.addAll(messages);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getInfoMessages()
	 */
	@Override
	@XmlElement
	public List<String> getInfoMessages() {
		return infoMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addInfoMessages(java.util.List)
	 */
	@Override
	public void addInfoMessages(List<String> messages) {
		if (messages != null) {
			this.infoMessages.addAll(messages);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getWarningMessages()
	 */
	@Override
	@XmlElement
	public List<String> getWarningMessages() {
		return warningMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#addWarningMessages(java.util.List)
	 */
	@Override
	public void addWarningMessages(List<String> messages) {
		if (messages != null) {
			this.warningMessages.addAll(messages);
		}
	}

	// Start Log Monitoring
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getModules()
	 */
	@Override
	public List<ModuleHeaderInfo> getModules() {
		return modulesList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#setModules(java.util.List)
	 */
	@Override
	public void setModules(List<ModuleHeaderInfo> modules) {
		this.modulesList = modules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getLogs()
	 */
	@Override
	public List<LogInfo> getLogs() {
		return logsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#setLogs(java.util.List)
	 */
	@Override
	public void setLogs(List<LogInfo> logs) {
		this.logsList = logs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getFocusId()
	 */
	@Override
	public String getFocusId() {
		return this.focusId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#setFocusId(java.lang.String)
	 */
	@Override
	public void setFocusId(String id) {
		this.focusId = id;
	}

	// End Log Monitoring

	// Start System Master
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getListCategory()
	 */
	public List<SystemInfo> getListCategory() {
		return listCategory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setListCategory(java.util.List)
	 */
	public void setListCategory(List<SystemInfo> listCategory) {
		this.listCategory = listCategory;
	}

	// End System Master
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getExcelDownloadFileList()
	 */
	public List<ExcelDownloadFile> getExcelDownloadFileList() {
		return excelDownloadFileList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setExcelDownloadFileList(java.
	 * util.List)
	 */
	public void setExcelDownloadFileList(
			List<ExcelDownloadFile> excelDownloadFileList) {
		this.excelDownloadFileList = excelDownloadFileList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getBookmarksList()
	 */
	@Override
	public List<SettingInfo> getBookmarksList() {
		return this.bookmarkList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setBookmarksList(java.util.List)
	 */
	@Override
	public void setBookmarksList(List<SettingInfo> settingInfo) {
		this.bookmarkList = settingInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getTotalRecord()
	 */
	public int getTotalRecord() {
		return totalRecord;
	}

	@Override
	@XmlElement
	public List<ExcelDownloadStatus> getExcelDownloadStatusList() {
		return excelDownloadStatusList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setExcelDownloadStatusList(java
	 * .util.List)
	 */
	@Override
	public void setExcelDownloadStatusList(
			List<ExcelDownloadStatus> excelDownloadStatusList) {
		this.excelDownloadStatusList = excelDownloadStatusList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getExcelDownloadStatus()
	 */
	@Override
	public ExcelDownloadStatus getExcelDownloadStatus() {
		return excelDownloadStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setExcelDownloadStatus(th.co.toyota
	 * .st3.api.model.ExcelDownloadStatus)
	 */
	@Override
	public void setExcelDownloadStatus(ExcelDownloadStatus excelDownloadStatus) {
		this.excelDownloadStatus = excelDownloadStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#setTotalRecord(int)
	 */
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getReportStatusData()
	 */
	@Override
	public Map<Character, String> getReportStatusData() {
		return reportStatusData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setReportStatusData(java.util.Map)
	 */
	@Override
	public void setReportStatusData(Map<Character, String> reportStatusData) {
		this.reportStatusData = reportStatusData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getExcelDownloadFile()
	 */
	@Override
	public ExcelDownloadFile getExcelDownloadFile() {
		return excelDownloadFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setExcelDownloadFile(th.co.toyota
	 * .st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public void setExcelDownloadFile(ExcelDownloadFile excelDownloadFile) {
		this.excelDownloadFile = excelDownloadFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getMandatoryFieldList()
	 */
	@Override
	public List<SystemInfo> getMandatoryFieldList() {
		return mandatoryFieldList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setMandatoryFieldList(java.util
	 * .List)
	 */
	@Override
	public void setMandatoryFieldList(List<SystemInfo> mandatoryFieldList) {
		this.mandatoryFieldList = mandatoryFieldList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getEnhancement()
	 */
	public String getEnhancement() {
		return enhancement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setEnhancement(java.lang.String)
	 */
	public void setEnhancement(String enhancement) {
		this.enhancement = enhancement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getBatchMasterList()
	 */
	@Override
	public List<BatchMaster> getBatchMasterList() {
		return listBatchMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setBatchMasterList(java.util.List)
	 */
	@Override
	public void setBatchMasterList(List<BatchMaster> listBatchMaster) {
		this.listBatchMaster = listBatchMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getBatchStatusList()
	 */
	@Override
	public List<?> getBatchStatusList() {
		return listBatchStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setBatchStatusList(java.util.List)
	 */
	@Override
	public void setBatchStatusList(List<?> listBatchStatus) {
		this.listBatchStatus = listBatchStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.model.Payload#getAppLogURL()
	 */
	@Override
	public String getAppLogURL() {
		return appLogURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.model.Payload#setAppLogURL(java.lang.String)
	 */
	@Override
	public void setAppLogURL(String appLogURL) {
		this.appLogURL = appLogURL;
	}

	@Override
	public List<List<LogInfo>> getGroupedLogs() {
		return groupLogs;
	}

	@Override
	public void setGroupedLogs(List<List<LogInfo>> logs) {
		this.groupLogs = logs;
	}
	
	@Override
	public List<List<String[]>> getGroupedLogsDisplay() {
		return groupLogsDisplay;
	}

	@Override
	public void setGroupedLogsDisplay(List<List<String[]>> logs) {
		this.groupLogsDisplay = logs;
	}
	
	@Override
	public List<List<String>> getDataList() {
		return dataList;
	}

	@Override
	public void setDataList(List<List<String>> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getFirstResult() {
		return firstResult;
	}

	@Override
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	@Override
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	@Override
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Override
	public int getTotalRows() {
		return totalRows;
	}

	@Override
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	
	@Override
	public void setScreenMode(String screenMode) {
		this.screenMode = screenMode;
	}

	@Override
	public String getScreenMode() {
		return screenMode;
	}

	
	@Override
	public List<String> getErrorHighlight() {
		return errorHighlight;
	}
	
	@Override
	public void setErrorHighlight(List<String> errorHighlight) {
		this.errorHighlight = errorHighlight;
	}

	@Override
	public List getObjectsInfoList() {
		return this.objectsInfoList;
	}

	@Override
	public void setObjectsInfoList(List objectsInfoList) {
		this.objectsInfoList = objectsInfoList;
	}
	
	@Override
	public List getObjectsInfo2List() {
		return this.objectsInfo2List;
	}

	@Override
	public void setObjectsInfo2List(List objectsInfo2List) {
		this.objectsInfo2List = objectsInfo2List;
	}

	public String getJsonProcessCurrent() {
		return jsonProcessCurrent;
	}

	public void setJsonProcessCurrent(String jsonProcessCurrent) {
		this.jsonProcessCurrent = jsonProcessCurrent;
	}

	@Override
	public CommonBaseForm getObjectForm() {
		return this.objectForm;
	}

	@Override
	public void setObjectForm(CommonBaseForm form) {
		this.objectForm = form;		
	}
	
	@Override
	public String getAppId() {
		return this.appId;
	}

	@Override
	public void setAppId(String aplid) {
		this.appId = aplid;
	}

	@Override
	public String getAppId2() {
		return this.appId2;
	}

	@Override
	public void setAppId2(String aplid2) {
		this.appId2 = aplid2;
	}

	/* (non-Javadoc)
	 * @see th.co.toyota.application.model.Payload#setCriterias(java.util.Map)
	 */
	@Override
	public void setCriterias(Map<String, List<ComboValue>> criterias) {
		this.criterias.putAll(criterias);
	}

	/* (non-Javadoc)
	 * @see th.co.toyota.application.model.Payload#getCriterias()
	 */
	@Override
	public Map<String, List<ComboValue>> getCriterias() {
		return this.criterias;
	}

	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	
	
}