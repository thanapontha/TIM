/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.repository
 * Program ID 	            :  IBW02120PreprocessRepository.java
 * Program Description	    :  PAMs Rundown Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  30 August 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.repository;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public interface IBW02120PreprocessRepository {
	
	public int IDX_DUPCNT = 0;
	public int IDX_VEHICLE_PLANT_EXIST =1;
	public int IDX_UNIT_PLANT_EXIST = 2;
	public int IDX_STOCK_EXIST = 3;
	public int IDX_GETSUDO_MONTH = 4;
	public int IDX_TIMING = 5;
	public int IDX_VEHICLE_PLANT = 6;
	public int IDX_VEHICLE_MODEL = 7;
	public int IDX_UNIT_PLANT = 8;
	public int IDX_UNIT_MODEL = 9;
	public int IDX_FILE_ID = 10;
	public int IDX_FILE_NAME = 11;
	public int IDX_IMPORTER = 12;
	public int IDX_RUNDOWN_KEY = 13;	
	public int IDX_VARIATION = 14;
	public int IDX_SS_NO = 15;
	public int IDX_ID_LINE = 16;
	public int IDX_EXPORTER = 17;
	public int IDX_ORDER_DT = 18;
	public int IDX_PROD_DT = 19;
	public int IDX_PROD_VOLUME = 20;
	public int IDX_LOCAL_STOCK = 21;
	public int IDX_STOCK_DAYS = 22;
	public int IDX_UNLOAD = 23;
	public int IDX_TRANSIT = 24;
	public int IDX_LOADING = 25;
	public int IDX_PORT_STOCK = 26;
	public int IDX_PACK_VOLUME = 27;
	public int IDX_LOT_SIZE = 28;
	public int IDX_NO_OF_LOT = 29;
	public int IDX_TOTAL_STOCK = 30;
	public int IDX_UPLOAD_FILE_NAME = 31;
	public int IDX_CREATE_BY = 32;
	public int IDX_APL_ID = 33;
	public int IDX_RUNNING_NO = 34;
	public int IDX_START_PROD_EXIST = 35;
	public int IDX_PAMS_VEHICLE_PLANT_CAL_CHK = 36;
	public int IDX_KOMPO_VEHICLE_PLANT_CAL_CHK = 37;
	public int IDX_VEHICLE_PLANT_CAL_FLAG = 38;
	public int IDX_VEHICLE_PLANT_CAL_DIS = 39;
	public int IDX_UNIT_PLANT_CAL_CHK = 40;
	public int IDX_UNIT_PLANT_CAL_FLAG = 41;
	public int IDX_UNIT_PLANT_CAL_DISY = 42;
	public int IDX_IS_STOCK_ERROR_CHK = 43;
	public int IDX_STOCK_MIN = 44;
	public int IDX_STOCK_MAX = 45;
	public int IDX_START_PROD_DT = 46;
	public int IDX_IS_VEHICLE_PER_UNIT = 47;
	public int IDX_START_EFF_KK_MONTH = 48;
	public int IDX_IS_EFF_IN_RELATION_CHK = 49;
	public int IDX_UNIT_VOLUME_KK = 50;
	public int IDX_PACKING_VOLUME_CHK = 51;
	
	//Total select column from sql query, please change if add new select column.
	public int totalSelectCol = 52;
		
	public int insertLogDetail(Object[] data, String userId, String type)
			throws CommonErrorException;
	public String getDeletePamsRundownSQL();
	public void setSysdate(Timestamp sysdate);
	public String getInsertPamsRundownSQL(String[] paramGetUpdate, String userId,
			String version, String getsudoMonth, String timing,
			String vehiclePlant, String vehicleModel, String appId);
	public int getConcurrencyPamsRundown(Connection conn, String[] paramGetUpdate,
			String appId) throws Exception;
	public String getConcurrencyDate(Connection conn, String[] params,
			String pamsKompoFlag) throws Exception;
	public List<Object[]> getStagingList(Connection conn, String version, String getsudoMonth, String endMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant, String unitModel, String userId, String pamsKompoFlag) throws CommonErrorException;
	public Object[] insertAndCalculateDataToTarget(Connection conn, String userId,
			String appId, String beginMonth, String endMonth,
			String getsudoMonth, String timing, int statusOfValidate,
			String[] paramDel, String[] paramGetUpdate)
			throws Exception;
	public List<Object[]> getVehicleProdVolumeWithWorksheet(Connection conn,
			String pamsKompoFlag, String version, String getsudoMonth, String endMonth,
			String timing, String vehiclePlant, String vehicleModel,
			String unitModel, String unitPlant, String userId) throws CommonErrorException;
	public List<Object[]> getPackingVolumeWorksheetVsPams(Connection conn,
			String version, String getsudoMonth, String endMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant,
			String unitModel, String userId) throws CommonErrorException;
	public List<String> getLastMonthOfWorksheetExistInPams(Connection conn,
			String pamsKompoFlag, String version, String getsudoMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant, String unitModel, String userId,
			String appId) throws CommonErrorException;
	public List<Object[]> getKaikiengUnitVolumeCheck(Connection conn, String version,
			String getsudoMonth, String endMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant,
			String unitModel, String userId, String pamsKompoFlag) throws CommonErrorException;
	public List<Object[]> getPackingVolumeCheck(Connection conn, String version,
			String getsudoMonth, String endMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant,
			String unitModel, String userId, String pamsKompoFlag)
			throws CommonErrorException;
}
