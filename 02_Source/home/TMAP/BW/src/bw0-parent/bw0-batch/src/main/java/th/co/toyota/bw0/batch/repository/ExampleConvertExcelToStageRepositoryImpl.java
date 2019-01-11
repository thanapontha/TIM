/******************************************************
 * Program History
 * 
 * Project Name	            :  TIM : Toyota Insurance Management
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.repository
 * Program ID 	            :  ExampleConvertExcelToStageRepositoryImp.java
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonSQLAdapter;
import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;

@Repository
public class ExampleConvertExcelToStageRepositoryImpl implements ExampleConvertExcelToStageRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStageRepositoryImpl.class);
	
	@Override
	public int insertDataToStaging(List<Object[]> dataList, String userId) throws CommonErrorException{
		SessionImpl session = (SessionImpl)(em.getDelegate());
		
		try(Connection conn = session.getJdbcConnectionAccess().obtainConnection()){
			
			return this.insertDataToStaging(conn, dataList, userId);
			
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}
	}
	
	@Override
	public int insertDataToStaging(Connection conn, List<Object[]> dataList, String userId) throws CommonErrorException{
		int inserted = 0;
		try{
			StringBuilder insSQL = new StringBuilder();
			insSQL.append("INSERT INTO TB_S_KOMPO  ");
			insSQL.append(" ( GETSUDO_MONTH,  ");
			insSQL.append("   TIMING,  ");
			insSQL.append("   VEHICLE_PLANT,  ");
			insSQL.append("   VEHICLE_MODEL,  ");
			insSQL.append("   UNIT_PLANT,  ");
			insSQL.append("   UNIT_MODEL,  ");
			insSQL.append("   FILE_ID,  ");
			insSQL.append("   FILE_NAME,  ");
			insSQL.append("   IMPORTER,  ");
			insSQL.append("   RUNDOWN_KEY,  ");
			insSQL.append("   EXPORTER,  ");
			insSQL.append("   ORDER_DT,  ");
			insSQL.append("   VANNING_VOLUME,  ");
			insSQL.append("   VANNING_DT,  ");
			insSQL.append("   LOADING_DT,  ");
			insSQL.append("   UNLOADING_DT,  ");
			insSQL.append("   PROD_DT,  ");
			insSQL.append("   PROD_VOLUME,  ");
			insSQL.append("   UPLOAD_FILE_NAME,  ");
			insSQL.append("   CREATE_BY,  ");
			insSQL.append("   RUNNING_NO,  ");
			insSQL.append("   APL_ID  ");
			insSQL.append(" )  ");
			insSQL.append(" VALUES  ");
			insSQL.append(" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ");
			insSQL.append("   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ");
			insSQL.append("   ?, ? ");
			insSQL.append(" )  ");
			
			conn.setAutoCommit(false);
			
			//insert data to staging
			CommonSQLAdapter adapter = new CommonSQLAdapter();
			if(dataList!=null && !dataList.isEmpty()){
				inserted = adapter.execute(conn, insSQL.toString() , dataList.toArray());
			}
			return inserted;
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}
	}

}
