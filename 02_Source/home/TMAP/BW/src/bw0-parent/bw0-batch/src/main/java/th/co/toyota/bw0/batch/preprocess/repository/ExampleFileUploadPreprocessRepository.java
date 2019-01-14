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
import th.co.toyota.bw0.batch.preprocess.vo.UploadParamVO;

public interface ExampleFileUploadPreprocessRepository {
	public class ColumnIndex{
		private ColumnIndex() {
		    throw new IllegalStateException("ColumnIndex class");
		  }
		public static final int DUPCNT = 0;
		public static final int GETSUDO_MONTH = 1;
		public static final int TIMING = 2;
		public static final int VEHICLE_PLANT = 3;
		public static final int VEHICLE_MODEL = 4;
		public static final int UNIT_PLANT = 5;
		public static final int UNIT_MODEL = 6;
		public static final int FILE_ID = 7;
		public static final int FILE_NAME = 8;
		public static final int IMPORTER = 9;
		public static final int RUNDOWN_KEY = 10;	
		public static final int EXPORTER = 11;
		public static final int ORDER_DT = 12;
		public static final int VANNING_VOLUME = 13;
		public static final int VANNING_DT = 14;
		public static final int LOADING_DT = 15;
		public static final int UNLOADING_DT = 16;
		public static final int PROD_DT = 17;
		public static final int PROD_VOLUME = 18;
		public static final int UPLOAD_FILE_NAME = 19;
		public static final int CREATE_BY = 20;
		public static final int APL_ID = 21;
		public static final int RUNNING_NO = 22;
		//Total select column from sql query, please change if add new select column.
		public static final int TOTAL_SELECTED_COLUMN = 23;
	}
	
	List<Object[]> getStagingList(Connection conn, UploadParamVO paramVo) throws CommonErrorException;

	Object[] insertAndCalculateDataToTarget(Connection conn, UploadParamVO paramVo) throws CommonErrorException;

}
