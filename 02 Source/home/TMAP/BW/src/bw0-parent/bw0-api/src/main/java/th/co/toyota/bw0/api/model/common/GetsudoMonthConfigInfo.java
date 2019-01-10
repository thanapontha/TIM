/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.api.model.common
 * Program ID 	            :  GetsudoMonthConfigInfo.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  August 11, 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import th.co.toyota.st3.api.model.BaseEntity;

import javax.persistence.Transient;

@Entity
@Table(name="TB_C_GETSUDO_MONTH")
public class GetsudoMonthConfigInfo extends BaseEntity{
	
	private static final long serialVersionUID = -6390350589877470586L;
	
	@Id
	@Column(name="MONTH")
	private String month;

	@Column(name="DISPLAY_MONTH")
	private Integer displayMonth;
	
	@Column(name="CHECK_MONTH")
	private Integer checkMonth;
	
	@Transient
	private String startMonth;

	@Transient
	private String endMonth;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getDisplayMonth() {
		return displayMonth;
	}

	public void setDisplayMonth(Integer displayMonth) {
		this.displayMonth = displayMonth;
	}

	public Integer getCheckMonth() {
		return checkMonth;
	}

	public void setCheckMonth(Integer checkMonth) {
		this.checkMonth = checkMonth;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	
}
