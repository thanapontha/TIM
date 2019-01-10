/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : Getsudo Worksheet Rundown System
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.api.model.common
 * Program ID 	            :  UnitCapacityManagementInfo.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  August 18, 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
* Copyright(C) 2017-Toyota Daihatsu Engineering & Manufacturing Co., Ltd. All Rights Reserved.            
 ********************************************************/
package th.co.toyota.bw0.api.model.common;

import th.co.toyota.st3.api.model.BaseEntity;


public class UnitCapacityManagementInfo extends BaseEntity{
	
	private static final long serialVersionUID = -6673671585161245884L;
	
	private String unitParentLine;
	private String vehiclePlant;
	private String vehicleModel;
	private String unitModel;
	private String incomeVolumeStatus;
	private String capacityMasterStatus;
	private String parentLineCapacityResult;
	private String subLineCapacityResult;
	private String confirmStatus;
	private String remark;
	private String updateKeySet;
	private String remainWait;
	
	public String getUnitParentLine() {
		return unitParentLine;
	}
	public void setUnitParentLine(String unitParentLine) {
		this.unitParentLine = unitParentLine;
	}
	public String getVehiclePlant() {
		return vehiclePlant;
	}
	public void setVehiclePlant(String vehiclePlant) {
		this.vehiclePlant = vehiclePlant;
	}
	public String getVehicleModel() {
		return vehicleModel;
	}
	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}
	public String getUnitModel() {
		return unitModel;
	}
	public void setUnitModel(String unitModel) {
		this.unitModel = unitModel;
	}
	public String getIncomeVolumeStatus() {
		return incomeVolumeStatus;
	}
	public void setIncomeVolumeStatus(String incomeVolumeStatus) {
		this.incomeVolumeStatus = incomeVolumeStatus;
	}
	public String getCapacityMasterStatus() {
		return capacityMasterStatus;
	}
	public void setCapacityMasterStatus(String capacityMasterStatus) {
		this.capacityMasterStatus = capacityMasterStatus;
	}
	public String getParentLineCapacityResult() {
		return parentLineCapacityResult;
	}
	public void setParentLineCapacityResult(String parentLineCapacityResult) {
		this.parentLineCapacityResult = parentLineCapacityResult;
	}
	public String getSubLineCapacityResult() {
		return subLineCapacityResult;
	}
	public void setSubLineCapacityResult(String subLineCapacityResult) {
		this.subLineCapacityResult = subLineCapacityResult;
	}
	public String getConfirmStatus() {
		return confirmStatus;
	}
	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUpdateKeySet() {
		return updateKeySet;
	}
	public void setUpdateKeySet(String updateKeySet) {
		this.updateKeySet = updateKeySet;
	}
	public String getRemainWait() {
		return remainWait;
	}
	public void setRemainWait(String remainWait) {
		this.remainWait = remainWait;
	}
	
	
	
}
