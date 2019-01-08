/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST33020ExcelDownloadMonitoringForm.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T,
 * Version					:  1.0
 * Creation Date            :  April 07, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web.form;

import javax.validation.constraints.Pattern;

public class CST33020ExcelDownloadMonitoringForm implements IST30000Form {
	
//	@NotEmpty(message = "{MSTD0031AERR}")
//	@DateTimeFormat(pattern="dd/mm/yyyy", message="{MSTD0045AERR}")
//	@Pattern(message="MSTD0043AERR")
	private String requestDate;
	private String reportStatus;
	
	@Pattern(regexp="^(?!\\*)[0-9a-zA-Z| _,.]*\\*?$", message="{MSTD0043AERR}")
	private String reportName;
	
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;
		if (field != null) {
			switch (field) {
			case "requestDate":
				friendly = "Request Date";
				break;
			case "reportStatus":
				friendly = "Report Status";
				break;
			case "reportName":
				friendly = "Report Name";
				break;
				
			default:
				friendly = field;
			}
		}
		return friendly;
	}
	
	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
