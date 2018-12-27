/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST33050BatchStatusForm.java
 * Program Description	    :  
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
package th.co.toyota.application.web.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class CST33050BatchStatusForm implements IST30000Form {

	@Length(max = 3, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Z\\-_]{0,3}+$", message = "{MSTD0043AERR}")
	private String projectCode;

	@Length(max = 10, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Z\\-_]{0,10}+$", message = "{MSTD0043AERR}")
	private String batchId;

	@Length(max = 100, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Za-z\\s]{0,100}+$", message = "{MSTD0043AERR}")
	private String batchName;

	@NotEmpty(message = "{MSTD0031AERR}")
	@Pattern(regexp = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)", message = "MSTD0043AERR")
	private String requestDate;

	private String requestBy;

	private Integer batchNo;

	private String updateDate;
	
	private String appId;
	
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;
		if (field != null) {
			switch (field) {
			case "projectCode":
				friendly = "Project Code";
				break;
			case "batchId":
				friendly = "Batch ID";
				break;
			case "batchName":
				friendly = "Batch Name";
				break;
			case "requestBy":
				friendly = "Requested By";
				break;
			case "batchSeqNo":
				friendly = "Sequence No";
				break;
			case "requestDate":
				friendly = "Request Date";
				break;
			default:
				friendly = field;
			}
		}

		return friendly;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}

	public Integer getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}
