/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST30900ExcelDownloadForm.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  April 8, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public class CST30900ExcelDownloadForm implements IST30000Form {

	private String tableName;
	
	private String bookmarks;
	
	private boolean isSaveBookmarks;
	
	@Length(min=0, max=60, message="{MSTD0053AERR}")
	@Pattern(regexp="^[0-9A-Za-z\\s]{0,60}+$", message="{MSTD0043AERR}")
	private String reportName;
	
	@Length(min=0, max=60, message="{MSTD0053AERR}")
	@Pattern(regexp="^[0-9A-Za-z\\s]{0,60}+$", message="{MSTD0043AERR}")
	private String reportTitle;
	
	@Length(min=0, max=4, message="{MSTD0053AERR}")
	@Pattern(regexp="^[0-9]{0,4}+$", message="{MSTD0043AERR}")
	private String startRow;
	
	@Length(min=0, max=4, message="{MSTD0053AERR}")
	@Pattern(regexp="^[0-9]{0,4}+$", message="{MSTD0043AERR}")
	private String startColumn;
	
	private String[] dbFieldName;
	
	private String[] field;
	
	private String[] displayName;

	private String[] orderDisp;
	
	private String[] pk;
	
	private String[] dataType;
	
	private String[] operationLogic;
	
	private String[] criteria;
	
	private String[] ChkDisplayOption;
	
	private String[] sort;
	
	private String modelClassName;
	
	private String generateExcelFlag;
	
	private String WST30900BigData;
	
	private String fileType;
	
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;
		if (field != null)
			switch (field) {
			case "tableName" :
				friendly = "Table/View";
				break;
			case "bookmarks" :
				friendly = "Bookmarks";
				break;
			case "reportName" :
				friendly = "Report Name";
				break;
			case "reportTitle" :
				friendly = "Report Title";
				break;
			case "startRow" :
				friendly = "Start Row";
				break;
			case "startColumn" :
				friendly = "Start Column";
				break;
			case "field" :
				friendly = "Field";
				break;
			case "displayName" :
				friendly = "Display Name";
				break;
			case "orderDisp" :
				friendly = "Order Display";
				break;
			case "pk" :
				friendly = "PK";
				break;
			case "dataType" :
				friendly = "Data Type";
				break;
			case "operationLogic" :
				friendly = "Logical Operator";
				break;
			case "criteria" :
				friendly = "Criteria";
				break;
			case "sort" :
				friendly = "Sort";
				break;
			default:
				friendly = field;
			}
		
		return friendly;
	}
	
	public String displayFriendlyField(String field, String rowOf) {
		String friendly = null;
		if (field != null)
			switch (field) {
			case "tableName" :
				friendly = "Table/View";
				break;
			case "bookmarks" :
				friendly = "Bookmarks";
				break;
			case "reportName" :
				friendly = "Report Name";
				break;
			case "reportTitle" :
				friendly = "Report Title";
				break;
			case "startRow" :
				friendly = "Start Row";
				break;
			case "startColumn" :
				friendly = "Start Column";
				break;
			case "field" :
				friendly = "Field";
				break;
			case "displayName" :
				friendly = "Display Name";
				break;
			case "orderDisp" :
				friendly = "Order Display";
				break;
			case "pk" :
				friendly = "PK";
				break;
			case "dataType" :
				friendly = "Data Type";
				break;
			case "operationLogic" :
				friendly = "Logical Operator";
				break;
			case "criteria" :
				friendly = "Criteria";
				break;
			case "sort" :
				friendly = "Sort";
				break;
			default:
				friendly = field;
			}
		
		return new StringBuffer(friendly).append(" of ").append(rowOf).toString();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(String bookmarks) {
		this.bookmarks = bookmarks;
	}

	public boolean isSaveBookmarks() {
		return isSaveBookmarks;
	}

	public void setSaveBookmarks(boolean isSaveBookmarks) {
		this.isSaveBookmarks = isSaveBookmarks;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getStartRow() {
		return startRow;
	}

	public void setStartRow(String startRow) {
		this.startRow = startRow;
	}

	public String getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(String startColumn) {
		this.startColumn = startColumn;
	}

	public String[] getField() {
		return field;
	}

	public void setField(String[] field) {
		this.field = field;
	}

	public String[] getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String[] displayName) {
		this.displayName = displayName;
	}

	public String[] getOrderDisp() {
		return orderDisp;
	}

	public void setOrderDisp(String[] orderDisp) {
		this.orderDisp = orderDisp;
	}

	public String[] getPk() {
		return pk;
	}

	public void setPk(String[] pk) {
		this.pk = pk;
	}

	public String[] getDataType() {
		return dataType;
	}

	public void setDataType(String[] dataType) {
		this.dataType = dataType;
	}

	public String[] getOperationLogic() {
		return operationLogic;
	}

	public void setOperationLogic(String[] operationLogic) {
		this.operationLogic = operationLogic;
	}

	public String[] getCriteria() {
		return criteria;
	}

	public void setCriteria(String[] criteria) {
		this.criteria = criteria;
	}

	public String[] getChkDisplayOption() {
		return ChkDisplayOption;
	}

	public void setChkDisplayOption(String[] chkDisplayOption) {
		ChkDisplayOption = chkDisplayOption;
	}

	public String[] getSort() {
		return sort;
	}

	public void setSort(String[] sort) {
		this.sort = sort;
	}

	public String getModelClassName() {
		return modelClassName;
	}

	public void setModelClassName(String modelClassName) {
		this.modelClassName = modelClassName;
	}

	public String getGenerateExcelFlag() {
		return generateExcelFlag;
	}

	public void setGenerateExcelFlag(String generateExcelFlag) {
		this.generateExcelFlag = generateExcelFlag;
	}

	public String getWST30900BigData() {
		return WST30900BigData;
	}

	public void setWST30900BigData(String WST30900BigData) {
		this.WST30900BigData = WST30900BigData;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String[] getDbFieldName() {
		return dbFieldName;
	}

	public void setDbFieldName(String[] dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
}
