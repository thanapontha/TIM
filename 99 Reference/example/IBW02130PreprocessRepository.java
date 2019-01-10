/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.repository
 * Program ID 	            :  IBW02130PreprocessRepository.java
 * Program Description	    :  KOMPO Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  11 September 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.repository;

import java.sql.Connection;
import java.util.List;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public interface IBW02130PreprocessRepository {
	
	public int IDX_L_GETSUDO_MONTH = 0;
	public int IDX_L_TIMING = 1;
	public int IDX_L_VEHICLE_PLANT = 2;
	public int IDX_L_VEHICLE_MODEL = 3;
	public int IDX_L_UNIT_PLANT = 4;
	public int IDX_L_UNIT_MODEL = 5;
	public int IDX_L_ERROR_SHEET = 6;
	public int IDX_L_ERROR_DATE = 7;
	public int IDX_L_ERROR_MONTH = 8;
	public int IDX_L_ERROR_RUNDOWN = 9;
	public int IDX_L_ERROR_CALENDAR = 10;
	public int IDX_L_ERROR_WORKSHEET = 11;
	public int IDX_L_ERROR_STOCK_MIN = 12;
	public int IDX_L_ERROR_STOCK_MAX = 13;
	
	
	public int IDX_DUPCNT = 0;
	public int IDX_GETSUDO_MONTH = 1;
	public int IDX_TIMING = 2;
	public int IDX_VEHICLE_PLANT = 3;
	public int IDX_VEHICLE_MODEL = 4;
	public int IDX_UNIT_PLANT = 5;
	public int IDX_UNIT_MODEL = 6;
	public int IDX_FILE_ID = 7;
	public int IDX_FILE_NAME = 8;
	public int IDX_IMPORTER = 9;
	public int IDX_RUNDOWN_KEY = 10;	
	public int IDX_EXPORTER = 11;
	public int IDX_ORDER_DT = 12;
	public int IDX_VANNING_VOLUME = 13;
	public int IDX_VANNING_DT = 14;
	public int IDX_LOADING_DT = 15;
	public int IDX_UNLOADING_DT = 16;
	public int IDX_PROD_DT = 17;
	public int IDX_PROD_VOLUME = 18;
	public int IDX_UPLOAD_FILE_NAME = 19;
	public int IDX_CREATE_BY = 20;
	public int IDX_APL_ID = 21;
	public int IDX_RUNNING_NO = 22;
	public int IDX_VEHICLE_PLANT_EXIST = 23;
	public int IDX_UNIT_PLANT_EXIST = 24;
	public int IDX_START_PROD_EXIST = 25;
	public int IDX_START_PROD_DT = 26;
	public int IDX_START_EFF_KK_MONTH = 27;
	
	//Total select column from sql query, please change if add new select column.
	public int totalSelectCol = 28;

	public int insertLogDetail(Object[] data, String userId, String type)
			throws CommonErrorException;
	
	public List<Object[]> getStagingList(Connection conn, String userId,
			String vehiclePlant, String vehicleModel, String getsudoMonth,
			String pamsKompoFlag) throws CommonErrorException;

	public Object[] insertAndCalculateDataToTarget(Connection conn, String[] params,
			int statusOfValidate, String[] unitPlantArr, String[] unitModelArr) throws Exception;

	public List<Object[]> getUnitModelRelateList(Connection conn, String getsudoMonth,
			String vehiclePlant, String vehicleModel)
			throws CommonErrorException;

	public List<String> getLastMonthOfWorksheetExistInKompo(Connection conn,
			String version, String getsudoMonth, String timing,
			String vehiclePlant, String vehicleModel, String userId,
			String appId) throws CommonErrorException;

	public List<Object[]> getVehicleProdVolumeDiagramWithWorksheet(Connection conn,
			String version, String getsudoMonth, String timing,
			String vehiclePlant, String vehicleModel, String userId)
			throws CommonErrorException;

}
