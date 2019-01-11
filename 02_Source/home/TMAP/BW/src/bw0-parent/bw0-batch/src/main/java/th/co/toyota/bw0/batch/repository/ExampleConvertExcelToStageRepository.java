/******************************************************
 * Program History
 * 
 * Project Name	            :  TIM : Toyota Insurance Management
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.repository
 * Program ID 	            :  ExampleConvertExcelToStageRepository.java
 * Program Description	    :  Example Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  10 January 2019
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.repository;

import java.sql.Connection;
import java.util.List;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public interface ExampleConvertExcelToStageRepository {
	public int insertDataToStaging(List<Object[]> dataList,
			String userId) throws CommonErrorException;
	public int insertDataToStaging(Connection conn, List<Object[]> dataList,
			String userId) throws CommonErrorException;
}
