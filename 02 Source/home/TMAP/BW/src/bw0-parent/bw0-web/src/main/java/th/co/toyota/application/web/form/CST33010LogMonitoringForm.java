/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST33010LogMonitoringForm.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 3, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web.form;


/**
 * @author danilo
 * 
 */
public class CST33010LogMonitoringForm implements IST30000Form {

	private String module;
	private String function;
	private String logStatus;

	private String userId;
	private String appId;
	private boolean logDetail;
	private String messageType;
	
	
	//@Length(min = 10, max = 10, message = "{MSTD0043AERR}")
	//@Pattern(regexp = "^[\\d\\/]+$", message = "{MSTD0043AERR}")
	private String dateFrom;

	//@Length(min = 10, max = 10, message = "{MSTD0043AERR}")
	//@Pattern(regexp = "^[\\d\\/]+$", message = "{MSTD0043AERR}")
	private String dateTo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.web.form.common.FriendlyForm#displayFriendlyField(java.lang
	 * .String)
	 */
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;
		if (field != null) {
			switch (field) {
			case "dateFrom":
				friendly = "Date From";
				break;
			case "dateTo":
				friendly = "Date To";
				break;

			default:
				friendly = field;
			}
		}
		return friendly;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getLogStatus() {
		return logStatus;
	}

	public void setLogStatus(String logStatus) {
		this.logStatus = logStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isLogDetail() {
		return logDetail;
	}

	public void setLogDetail(boolean logDetail) {
		this.logDetail = logDetail;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
