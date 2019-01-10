/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST33040BatchMasterForm.java
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

public class CST33040BatchMasterForm implements IST30000Form {

	@Length(max = 3, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Z\\-_]{0,3}+$", message = "{MSTD0043AERR}")
	private String projectCode;

	@Length(max = 10, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Z\\-_]{0,10}+$", message = "{MSTD0043AERR}")
	private String batchId;

	@Length(max = 100, message = "{MSTD0052AERR}")
	@Pattern(regexp = "^[0-9A-Za-z\\s]{0,100}+$", message = "{MSTD0043AERR}")
	private String batchName;

	@Length(max = 1, message = "{MSTD0052AERR}")
	private String priorityLevel;

	@Length(max = 1, message = "{MSTD0052AERR}")
	private String concurrency;

	private String owner;

	private String shell;

	private String supportId;

	private String updateDate;

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
			case "priorityLevel":
				friendly = "Priority Level";
				break;
			case "concurrency":
				friendly = "Concurrency";
				break;
			case "owner":
				friendly = "Owner";
				break;
			case "shell":
				friendly = "Shell";
				break;
			case "supportId":
				friendly = "Support ID";
				break;
			case "updateDate":
				friendly = "Update Date";
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

	public String getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(String priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public String getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(String concurrency) {
		this.concurrency = concurrency;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getShell() {
		return shell;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}

	public String getSupportId() {
		return supportId;
	}

	public void setSupportId(String supportId) {
		this.supportId = supportId;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
}
