/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  CST33060SystemMasterForm.java
 * Program Description	    :  
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Jul 16, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public class CST33060SystemMasterForm implements IST30000Form {
	
	@Length(max=40, message="{MSTD0052AERR}")
	@Pattern(regexp="^[0-9A-Z\\-_]+$", message="{MSTD0043AERR}")
	private String category;
	
	@Length(max=40, message="{MSTD0052AERR}")
	@Pattern(regexp="^[0-9A-Z\\-_]+$", message="{MSTD0043AERR}")
	private String subCategory;
	
	@Length(max=40, message="{MSTD0052AERR}")
//	@Pattern(regexp="^([0-9A-Z\\-_]*|[0-9A-Z\\-_]+\\*?)$", message="{MSTD0043AERR}")
	@Pattern(regexp="^([0-9A-Z\\-_:*]+\\*?)$", message="{MSTD0043AERR}")
	private String code;
	
	@Length(max=200, message="{MSTD0052AERR}")
	private String value;
	
	private String remark;
	
	private String status;
	
	private String updateDate;
	
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;
		if (field != null) {
			switch (field) {
			case "category":
				friendly = "Category";
				break;
			case "subCategory":
				friendly = "Sub Category";
				break;
			case "code":
				friendly = "Code";
				break;
			case "value":
				friendly = "Value";
				break;
			case "remark":
				friendly = "Remark";
				break;
			case "status":
				friendly = "Status";
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

	

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

}
