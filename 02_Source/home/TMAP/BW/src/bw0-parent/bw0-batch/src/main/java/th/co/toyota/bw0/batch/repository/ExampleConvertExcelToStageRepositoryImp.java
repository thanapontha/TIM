/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.repository
 * Program ID 	            :  CBW02130Repository.java
 * Program Description	    :  Kompo Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  07 September 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CBW00000SQLAdapter;
import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;

@Repository
public class ExampleConvertExcelToStageRepositoryImp implements ExampleConvertExcelToStageRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStageRepositoryImp.class);
	
	@Override
	public int insertDataToStaging(Connection conn, List<Object[]> dataList, String userId) throws CommonErrorException{
		int inserted = 0;
		boolean completed = false;
		boolean closeConnection = true;
		try{
//			StringBuilder deleteSQL = new StringBuilder();
//			deleteSQL.append(" DELETE FROM TB_S_KOMPO WHERE CREATE_BY = ?");
			
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
			
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
//			//delete data from staging by user id
//			PreparedStatement ps = conn.prepareStatement(deleteSQL.toString());
//			ps.setString(1, userId);
//			ps.executeUpdate();
//			ps.close();
			
			//insert data to staging
			CBW00000SQLAdapter adapter = new CBW00000SQLAdapter();
			if(dataList!=null && !dataList.isEmpty()){
				inserted = adapter.execute(conn, insSQL.toString() , dataList.toArray());
			}
			completed = true;
			return inserted;
		}catch (Exception e) {
			completed = false;
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(completed){
						conn.commit();
					}else{
						conn.rollback();
					}
					
					if(closeConnection){
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
