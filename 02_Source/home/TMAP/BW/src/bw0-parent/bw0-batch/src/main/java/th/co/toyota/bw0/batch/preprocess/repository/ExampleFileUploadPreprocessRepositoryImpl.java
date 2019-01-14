/******************************************************
 * Program History
 * 
 * Project Name	            : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.repository
 * Program ID 	            :  ExampleFileUploadPreprocessRepositoryImpl.java
 * Program Description	    :  Example File Upload Preprocess Repository Implement
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanapon T.
 * Version		    		:  1.0
 * Creation Date            :  January, 14 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.batch.preprocess.vo.UploadParamVO;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;

@Repository
public class ExampleFileUploadPreprocessRepositoryImpl implements ExampleFileUploadPreprocessRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	final Logger logger = LoggerFactory.getLogger(ExampleFileUploadPreprocessRepositoryImpl.class);
	
	@Override
	public List<Object[]> getStagingList(Connection conn, UploadParamVO paramVo) throws CommonErrorException{	
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT COUNT(*) OVER(PARTITION BY S.VANNING_DT, S.PROD_DT) AS DUPCNT, ");//INDEX 0
		sql.append("       S.GETSUDO_MONTH, ");
		sql.append("       S.TIMING, ");
		sql.append("       S.VEHICLE_PLANT, ");
		sql.append("       S.VEHICLE_MODEL, ");
		sql.append("       S.UNIT_PLANT, ");
		sql.append("       S.UNIT_MODEL, ");
		sql.append("       S.FILE_ID, ");
		sql.append("       S.FILE_NAME, ");
		sql.append("       S.IMPORTER, ");
		sql.append("       S.RUNDOWN_KEY, ");
		sql.append("       S.EXPORTER, ");
		sql.append("       S.ORDER_DT, ");
		sql.append("       S.VANNING_VOLUME, ");
		sql.append("       S.VANNING_DT, ");
		sql.append("       S.LOADING_DT, ");
		sql.append("       S.UNLOADING_DT, ");
		sql.append("       S.PROD_DT, ");
		sql.append("       S.PROD_VOLUME, ");
		sql.append("       S.UPLOAD_FILE_NAME, ");
		sql.append("       S.CREATE_BY, ");
		sql.append("       S.APL_ID, ");
		sql.append("       S.RUNNING_NO ");
		sql.append("  FROM TB_S_KOMPO S ");
		sql.append(" WHERE S.CREATE_BY = '"+paramVo.getUserId()+"' ");
		sql.append(" ORDER BY S.RUNNING_NO ");
		
		logger.debug(sql.toString());
		
		List<Object[]> ls = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()){
			while(rs.next()){
				List<Object> obj = new ArrayList<>();
				for(int col=1;col <= ExampleFileUploadPreprocessRepository.ColumnIndex.TOTAL_SELECTED_COLUMN; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}
	}
	
	@Override
	public Object[] insertAndCalculateDataToTarget(Connection conn, UploadParamVO paramVo) throws CommonErrorException{
		Map<String, String> resultMap = new HashMap<>();
		
		boolean completed = false;
		int insertedCnt = 0;
		try {
			conn.setAutoCommit(false);	
			
			StringBuilder delSql = new StringBuilder();
			delSql.append(" DELETE FROM TB_R_KOMPO T ");
			delSql.append("  WHERE  T.CREATE_BY = ? ");

			try(PreparedStatement ps = conn.prepareStatement(delSql.toString())){
				int index = 1;
				ps.setString(index++, paramVo.getUserId());
				ps.executeUpdate();
			}
			
			StringBuilder insHeader = new StringBuilder();

			insHeader.append("INSERT INTO TB_R_KOMPO ");
			insHeader.append("  (VERSION, ");
			insHeader.append("   GETSUDO_MONTH, ");
			insHeader.append("   TIMING, ");
			insHeader.append("   VEHICLE_PLANT, ");
			insHeader.append("   VEHICLE_MODEL, ");
			insHeader.append("   UNIT_PLANT, ");
			insHeader.append("   UNIT_MODEL, ");
			insHeader.append("   FILE_ID, ");
			insHeader.append("   FILE_NAME, ");
			insHeader.append("   IMPORTER, ");
			insHeader.append("   RUNDOWN_KEY, ");
			insHeader.append("   EXPORTER, ");
			insHeader.append("   ORDER_DT, ");
			insHeader.append("   VANNING_VOLUME, ");
			insHeader.append("   VANNING_DT, ");
			insHeader.append("   LOADING_DT, ");
			insHeader.append("   UNLOADING_DT, ");
			insHeader.append("   PROD_DT, ");
			insHeader.append("   PROD_VOLUME, ");
			insHeader.append("   CREATE_BY, ");
			insHeader.append("   CREATE_DT, ");
			insHeader.append("   UPDATE_BY, ");
			insHeader.append("   UPDATE_DT) ");
			insHeader.append(" ");
			insHeader.append("  SELECT ?, ");
			insHeader.append("          S.GETSUDO_MONTH, ");
			insHeader.append("          S.TIMING, ");
			insHeader.append("          S.VEHICLE_PLANT, ");
			insHeader.append("          S.VEHICLE_MODEL, ");
			insHeader.append("          S.UNIT_PLANT, ");
			insHeader.append("          S.UNIT_MODEL, ");
			insHeader.append("          S.FILE_ID, ");
			insHeader.append("          S.FILE_NAME, ");
			insHeader.append("          S.IMPORTER, ");
			insHeader.append("          S.RUNDOWN_KEY, ");
			insHeader.append("          S.EXPORTER, ");
			insHeader.append("          S.ORDER_DT, ");
			insHeader.append("          S.VANNING_VOLUME, ");
			insHeader.append("          S.VANNING_DT, ");
			insHeader.append("          S.LOADING_DT, ");
			insHeader.append("          S.UNLOADING_DT, ");
			insHeader.append("          S.PROD_DT, ");
			insHeader.append("          S.PROD_VOLUME, ");
			insHeader.append("          S.CREATE_BY, ");
			insHeader.append("          ? , ");
			insHeader.append("          S.CREATE_BY, ");
			insHeader.append("          ? ");
			insHeader.append("     FROM TB_S_KOMPO S ");
			insHeader.append("     WHERE S.CREATE_BY = ? ");


			Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();
			try(PreparedStatement ps = conn.prepareStatement(insHeader.toString())){
				int index = 1;
				ps.setString(index++, "TDEM");
				ps.setTimestamp(index++, sysdate);
				ps.setTimestamp(index++, sysdate);
				ps.setString(index++, paramVo.getUserId());
				
				insertedCnt = ps.executeUpdate();
		
				completed = true;
			}
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			try {
				if(completed){
					conn.commit();
				}else {
					insertedCnt = 0;
					conn.rollback();
				}
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		resultMap.put("INSERT_TB_R_KOMPO", Integer.toString(insertedCnt));
		if(!completed) {
			return new Object[]{CST30000Constants.SUCCESS, resultMap};
		}else {
			return new Object[]{CST30000Constants.ERROR, resultMap};
		}
	}
}