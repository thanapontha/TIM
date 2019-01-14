/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.repository
 * Program ID 	            :  ExampleFileUploadPreprocessRepositoryImpl.java
 * Program Description	    :  Example File Upload Preprocess Repository Implement
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanapon T.
 * Version		    		:  1.0
 * Creation Date            :  January, 11 2018
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.CommonAPIRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;

@Repository
public class ExampleFileUploadPreprocessRepositoryImpl implements ExampleFileUploadPreprocessRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	@Autowired
	private CommonAPIRepository commonRepository;
	
	final Logger logger = LoggerFactory.getLogger(ExampleFileUploadPreprocessRepositoryImpl.class);
	
	@Override
	public List<Object[]> getStagingList(Connection conn, String userId, String vehiclePlant, String vehicleModel, String getsudoMonth, String pamsKompoFlag) throws CommonErrorException{
	
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT COUNT(*) OVER(PARTITION BY S.VANNING_DT, S.PROD_DT) AS DUPCN, ");//INDEX 0
		
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
		sql.append("       S.RUNNING_NO, ");
		sql.append("       NVL2(CV.PLANT, 'YES', 'NO') VEHICLE_PLANT_EXIST, ");
		sql.append("       NVL2(CU.PLANT, 'YES', 'NO') UNIT_PLANT_EXIST, ");
//		sql.append("       (CASE WHEN (SELECT TO_CHAR(MIN(PROD_DT),'Mon-YY') FROM TB_S_KOMPO WHERE CREATE_BY = '"+userId+"' GROUP BY CREATE_BY) = S.GETSUDO_MONTH THEN  ");
//		sql.append("                'YES' ELSE 'NO' END) START_PROD_EXIST, ");
		
		sql.append("      (CASE WHEN (SELECT TO_CHAR(MIN(PROD_DT),'Mon-YY') FROM TB_S_KOMPO WHERE CREATE_BY = '"+userId+"' GROUP BY CREATE_BY) =  ");
		sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
		sql.append("        FROM TB_R_KAIKIENG_D D ");
		sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
		sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
		sql.append("         AND D.TIMING = S.TIMING ");
		sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
		sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
		sql.append("         AND D.UNIT_VOLUME IS NOT NULL) ");
		sql.append("       THEN 'YES' ELSE 'NO' END) START_PROD_EXIST, ");
		
		sql.append("       MIN(S.PROD_DT) OVER (PARTITION BY S.CREATE_BY) AS START_PROD_DT, ");
		
		sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
		sql.append("        FROM TB_R_KAIKIENG_H D ");
		sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
		sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
		sql.append("         AND D.TIMING = S.TIMING ");
		sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
		sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
		sql.append("         AND D.VEHICLE_VOLUME IS NOT NULL) START_EFF_KK_MONTH ");
		
		sql.append("  FROM TB_S_KOMPO S ");
		
		sql.append("  LEFT JOIN (SELECT GETSUDO_MONTH, TIMING, PLANT, PLANT_TYPE ");
		sql.append("               FROM TB_M_CALENDAR ");
		sql.append("              WHERE PLANT_TYPE = 'V' ");
		sql.append("              GROUP BY GETSUDO_MONTH, TIMING, PLANT, PLANT_TYPE) CV ");
		sql.append("    ON S.GETSUDO_MONTH = CV.GETSUDO_MONTH ");
		sql.append("   AND S.TIMING = CV.TIMING ");
		sql.append("   AND S.VEHICLE_PLANT = CV.PLANT ");
		
		sql.append("  LEFT JOIN (SELECT GETSUDO_MONTH, TIMING, PLANT, PLANT_TYPE ");
		sql.append("               FROM TB_M_CALENDAR ");
		sql.append("              WHERE PLANT_TYPE = 'U' ");
		sql.append("              GROUP BY GETSUDO_MONTH, TIMING, PLANT, PLANT_TYPE) CU ");
		sql.append("    ON S.GETSUDO_MONTH = CU.GETSUDO_MONTH ");
		sql.append("   AND S.TIMING = CU.TIMING ");
		sql.append("   AND S.UNIT_PLANT = CU.PLANT ");
		
		sql.append(" WHERE S.CREATE_BY = '"+userId+"' ");
		sql.append(" ORDER BY S.RUNNING_NO ");
		
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
		List<Object[]> ls = new ArrayList<>();
        try{
			if(conn==null){
	    		SessionImpl session = (SessionImpl)(em.getDelegate());
	    		conn = session.getJdbcConnectionAccess().obtainConnection();
	    	}else{
	    		closeConnection = false;
	    	}
			
			logger.debug(sql.toString());
			pp = conn.prepareStatement(sql.toString());
			rs = pp.executeQuery();
			while(rs.next()){
				List<Object> obj = new ArrayList<>();
				for(int col=1;col<=totalSelectCol; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		} finally {
			try{
				if(conn!=null && !conn.isClosed()){
					if (rs !=null) {
						rs.close();					
			            rs = null;
			        }
					
					if (pp !=null) {
			            pp.close();
			            pp = null;
			        }
					
					if(closeConnection){
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Object[] insertAndCalculateDataToTarget(Connection conn, String[] params, int statusOfValidate, String[] unitPlantArr, String[] unitModelArr) throws Exception{
		HashMap<String, String> resultMap = new HashMap<>();
		
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		int insertPamsCnt = 0;
		int insertKompoCnt = 0;
		int insertCalVanCnt = 0;
		boolean warning = false;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);	

			String version = params[0];
			String pamsKompoFlag = params[1];
			String getsudoMonth = params[2];
			String timing = params[3];
			String vehiclePlant = params[4];
			String vehicleModel = params[5];
			String unitPlant = params[6];
			String unitType = params[7];
			String unitModel = params[8];
			String userId = params[9];
			String appId = params[10];
			String beginMonth = params[11];
			String endMonth = params[12];
			
			//Delete and Insert PAMs Rundown
			ps = conn.prepareStatement("");
			
			ps.setString(1, version);
			ps.setString(2, getsudoMonth);
			ps.setString(3, timing);
			ps.setString(4, vehiclePlant);
			ps.setString(5, vehicleModel);
			ps.setString(6, unitPlant);
			ps.setString(7, unitModel);

			ps.executeUpdate();
			
			if(ps!=null){
				ps.close();
				ps = null;
			}
			
			String[] paramGetUpdate = new String[] {version, 
													pamsKompoFlag, 
													getsudoMonth, 
													timing, 
													vehiclePlant, 
													vehicleModel, 
													unitPlant, 
													unitType,
													unitModel};
			
			//String insPams = repositoryPams.getInsertPamsRundownSQL(paramGetUpdate, userId, version, getsudoMonth, timing, vehiclePlant, vehicleModel, appId);
//			int concurrentPams = repositoryPams.getConcurrencyPamsRundown(conn, paramGetUpdate, appId);
			
			Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();
			
			ps = conn.prepareStatement("");
			ps.setTimestamp(1, sysdate);
			ps.setTimestamp(2, sysdate);
			
			insertPamsCnt = ps.executeUpdate();
			
			
			
			//Delete and Insert Kompo
			StringBuilder delSQL = new StringBuilder();
			delSQL.append(" DELETE FROM TB_R_KOMPO T ");
			delSQL.append("  WHERE  T.VERSION = ? ");
			delSQL.append("     AND T.GETSUDO_MONTH = ? ");
			delSQL.append("     AND T.TIMING = ? ");
			delSQL.append("     AND T.VEHICLE_PLANT = ? ");
			delSQL.append("     AND T.VEHICLE_MODEL = ? ");
			delSQL.append("     AND T.UNIT_PLANT = ? ");

			ps = conn.prepareStatement(delSQL.toString());

			ps.setString(1, version);
			ps.setString(2, getsudoMonth);
			ps.setString(3, timing);
			ps.setString(4, vehiclePlant);
			ps.setString(5, vehicleModel);
			ps.setString(6, unitPlant);
			ps.executeUpdate();
			
			if(ps!=null){
				ps.close();
				ps = null;
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
			insHeader.append("  (SELECT '"+version+"', ");
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
			insHeader.append("     WHERE to_date(to_char(S.PROD_DT ,'Mon-YY'),'Mon-YY') >= to_date( '"+getsudoMonth+"' ,'Mon-YY') ");
			insHeader.append("     AND to_date(to_char(S.PROD_DT ,'Mon-YY'),'Mon-YY') <= (SELECT MAX(to_date(T.VOLUME_MONTH,'Mon-YY')) ");
			insHeader.append("     														    FROM TB_R_KAIKIENG_H T ");
			insHeader.append("     														   WHERE T.VERSION = '"+version+"' ");
			insHeader.append("     														     AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			insHeader.append("     														     AND T.TIMING = '"+timing+"' ");
			insHeader.append("     														     AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			insHeader.append("     														     AND T.VEHICLE_MODEL = '"+vehicleModel+"' ) ");

			insHeader.append("     AND S.CREATE_BY = '"+userId+"' ");
			
			//Concurrency check
			insHeader.append(" 	   AND (SELECT COUNT(1) CRES ");
			insHeader.append("  FROM (SELECT NVL2(L.GETSUDO_MONTH, 'YES', 'NO') L_EXIST, ");
			insHeader.append("               L.TRANS_UPDATE_DT, ");
			insHeader.append("               NVL2(R.GETSUDO_MONTH, 'YES', 'NO') RD_EXIST, ");
			insHeader.append("               R.UPDATE_DT ");
			insHeader.append("          FROM (SELECT '"+getsudoMonth+"' GETSUDO_MONTH, ");
			insHeader.append("                       '"+timing+"' TIMING, ");
			insHeader.append("                       '"+vehiclePlant+"' VEHICLE_PLANT, ");
			insHeader.append("                       '"+vehicleModel+"' VEHICLE_MODEL, ");
			insHeader.append("                       '"+pamsKompoFlag+"' UPLOAD_TYPE ");
			insHeader.append("                  FROM DUAL) T ");
			insHeader.append("          LEFT JOIN (SELECT * ");
			insHeader.append("                      FROM TB_R_RUNDOWN_KOMPO_STS ");
			insHeader.append("                     WHERE VERSION = '"+version+"' ");
//			insHeader.append("                       AND UPLOAD_TYPE = '"+pamsKompoFlag+"' ");
			insHeader.append("                       AND UPLOAD_STS <> 'ER') R ");
			insHeader.append("            ON T.GETSUDO_MONTH = R.GETSUDO_MONTH ");
			insHeader.append("           AND T.TIMING = R.TIMING ");
			insHeader.append("           AND T.VEHICLE_PLANT = R.VEHICLE_PLANT ");
			insHeader.append("           AND T.VEHICLE_MODEL = R.VEHICLE_MODEL ");
			insHeader.append("          LEFT JOIN TB_L_UPLOAD_STS L ");
			insHeader.append("            ON (CASE ");
			insHeader.append("                 ELSE ");
			insHeader.append("                  '' ");
			insHeader.append("               END) = L.UPLOAD_TYPE ");
			insHeader.append("           AND T.GETSUDO_MONTH = L.GETSUDO_MONTH ");
			insHeader.append("           AND T.TIMING = L.TIMING ");
			insHeader.append("           AND T.VEHICLE_PLANT = L.VEHICLE_PLANT ");
			insHeader.append("           AND T.VEHICLE_MODEL = L.VEHICLE_MODEL ");
			insHeader.append("           AND L.APL_ID = '"+appId+"') A ");
			insHeader.append(" WHERE (CASE ");
			insHeader.append("         WHEN A.L_EXIST = 'YES' AND A.RD_EXIST = 'NO' AND ");
			insHeader.append("              A.TRANS_UPDATE_DT IS NULL THEN ");
			insHeader.append("          'OK' ");
			insHeader.append("         WHEN A.L_EXIST = 'YES' AND A.RD_EXIST = 'YES' AND ");
			insHeader.append("              A.UPDATE_DT = A.TRANS_UPDATE_DT THEN ");
			insHeader.append("          'OK' ");
			insHeader.append("         ELSE ");
			insHeader.append("          'ERROR' ");
			insHeader.append("       END) = 'OK') > 0 "); //Count = 1 : OK, Count = 0 : Error concurrency
			
			insHeader.append(") ");

			ps = conn.prepareStatement(insHeader.toString());
			ps.setTimestamp(1, sysdate);
			ps.setTimestamp(2, sysdate);
			
			insertKompoCnt = ps.executeUpdate();
			
			if(insertPamsCnt > 0 /*&& concurrentPams > 0*/ && insertKompoCnt > 0){

				//Calculate Vanning volume
				StringBuilder delCalVanSQL = new StringBuilder();
				delCalVanSQL.append(" DELETE FROM TB_R_KOMPO_VAN_RESULT T ");
				delCalVanSQL.append("  WHERE  T.VERSION = ? ");
				delCalVanSQL.append("     AND T.GETSUDO_MONTH = ? ");
				delCalVanSQL.append("     AND T.TIMING = ? ");
				delCalVanSQL.append("     AND T.VEHICLE_PLANT = ? ");
				delCalVanSQL.append("     AND T.VEHICLE_MODEL = ? ");
				
				//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
				if(unitPlantArr != null && unitPlantArr.length > 0
						&& unitModelArr != null && unitModelArr.length > 0
						&& unitPlantArr.length == unitModelArr.length){
					delCalVanSQL.append("     AND (");
					for(int i=0; i<unitModelArr.length;i++){
						if(i==0){
							delCalVanSQL.append("     (T.UNIT_PLANT = '"+unitPlantArr[i]+"' AND T.UNIT_MODEL = '"+unitModelArr[i]+"') ");
						}else{
							delCalVanSQL.append("    OR (T.UNIT_PLANT = '"+unitPlantArr[i]+"' AND T.UNIT_MODEL = '"+unitModelArr[i]+"') ");
						}
					}
					delCalVanSQL.append("     )");
				}
				//END CR UT-002 2018/02/16
				
				ps = conn.prepareStatement(delCalVanSQL.toString());
				
				ps.setString(1, version);
				ps.setString(2, getsudoMonth);
				ps.setString(3, timing);
				ps.setString(4, vehiclePlant);
				ps.setString(5, vehicleModel);
				
				ps.executeUpdate();
				
				if(ps!=null){
					ps.close();
					ps = null;
				}
						
				StringBuilder insCalVanSQL = new StringBuilder();
				insCalVanSQL.append("INSERT INTO TB_R_KOMPO_VAN_RESULT ");
				insCalVanSQL.append("  (VERSION, ");
				insCalVanSQL.append("   GETSUDO_MONTH, ");
				insCalVanSQL.append("   TIMING, ");
				insCalVanSQL.append("   VEHICLE_PLANT, ");
				insCalVanSQL.append("   VEHICLE_MODEL, ");
				insCalVanSQL.append("   UNIT_PLANT, ");
				insCalVanSQL.append("   UNIT_MODEL, ");
				insCalVanSQL.append("   LOT_SIZE, ");
				insCalVanSQL.append("   VANNING_MONTH, ");
				insCalVanSQL.append("   VANNING_VOLUME, ");
				insCalVanSQL.append("   ROUNDING_VOLUME, ");
				insCalVanSQL.append("   ADJUST_VOLUME, ");
				insCalVanSQL.append("   CREATE_BY, ");
				insCalVanSQL.append("   CREATE_DT, ");
				insCalVanSQL.append("   UPDATE_BY, ");
				insCalVanSQL.append("   UPDATE_DT) ");
				insCalVanSQL.append("  WITH TB_T_VANNING_MONTH AS ");
				insCalVanSQL.append("   (SELECT T1.GETSUDO_MONTH, ");
				insCalVanSQL.append("           T1.TIMING, ");
				insCalVanSQL.append("           T1.VEHICLE_PLANT, ");
				insCalVanSQL.append("           T1.VEHICLE_MODEL, ");
				insCalVanSQL.append("           TO_CHAR(T1.VANNING_DT, 'Mon-YY') VANNING_MONTH, ");
				insCalVanSQL.append("           TO_CHAR(T1.PROD_DT, 'Mon-YY') PROD_MONTH, ");
				insCalVanSQL.append("           SUM(T1.PROD_VOLUME) VANNING_VOL_MONTH ");
				insCalVanSQL.append("      FROM TB_S_KOMPO T1 ");
				insCalVanSQL.append("     WHERE T1.GETSUDO_MONTH = '"+getsudoMonth+"' ");
				insCalVanSQL.append("       AND T1.TIMING = '"+timing+"' ");
				insCalVanSQL.append("       AND T1.VEHICLE_PLANT = '"+vehiclePlant+"' ");
				insCalVanSQL.append("       AND T1.VEHICLE_MODEL = '"+vehicleModel+"' ");
				insCalVanSQL.append("     GROUP BY T1.GETSUDO_MONTH, ");
				insCalVanSQL.append("              T1.TIMING, ");
				insCalVanSQL.append("              T1.VEHICLE_PLANT, ");
				insCalVanSQL.append("              T1.VEHICLE_MODEL, ");
				insCalVanSQL.append("              TO_CHAR(T1.VANNING_DT, 'Mon-YY'), ");
				insCalVanSQL.append("              TO_CHAR(T1.PROD_DT, 'Mon-YY')), ");
				insCalVanSQL.append("  TB_T_CAL_RATIO AS ");
				insCalVanSQL.append("   (SELECT T.VANNING_MONTH, ");
				insCalVanSQL.append("           T.PROD_MONTH, ");
				insCalVanSQL.append("           T.VANNING_VOL_MONTH, ");
				insCalVanSQL.append("           NVL(D.VEHICLE_VOLUME, 0) PROD_VOL, ");
				insCalVanSQL.append("           (CASE ");
				insCalVanSQL.append("             WHEN NVL(D.VEHICLE_VOLUME, 0) = 0 THEN ");
				insCalVanSQL.append("              0 ");
				insCalVanSQL.append("             ELSE ");
				insCalVanSQL.append("              (T.VANNING_VOL_MONTH / NVL(D.VEHICLE_VOLUME, 0)) * 100 ");
				insCalVanSQL.append("           END) AS RATIO_VOL ");
				insCalVanSQL.append("      FROM TB_T_VANNING_MONTH T ");
				insCalVanSQL.append("      LEFT JOIN (SELECT * ");
				insCalVanSQL.append("                  FROM TB_R_KAIKIENG_H T ");
				insCalVanSQL.append("                 WHERE T.VERSION = '"+version+"' ");
				insCalVanSQL.append("                   AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
				insCalVanSQL.append("                   AND T.TIMING = '"+timing+"' ");
				insCalVanSQL.append("                   AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
				insCalVanSQL.append("                   AND T.VEHICLE_MODEL = '"+vehicleModel+"') D ");
				insCalVanSQL.append("        ON T.GETSUDO_MONTH = D.GETSUDO_MONTH ");
				insCalVanSQL.append("       AND T.TIMING = D.TIMING ");
				insCalVanSQL.append("       AND T.VEHICLE_PLANT = D.VEHICLE_PLANT ");
				insCalVanSQL.append("       AND T.VEHICLE_MODEL = D.VEHICLE_MODEL ");
				insCalVanSQL.append("       AND T.PROD_MONTH = D.VOLUME_MONTH ");
				insCalVanSQL.append("     ORDER BY T.PROD_MONTH, T.VANNING_MONTH), ");
				insCalVanSQL.append("  TB_T_VANNING_MON_GETSUDO AS ");
				insCalVanSQL.append("   (SELECT VANNING_MONTH ");
				insCalVanSQL.append("      FROM (SELECT VM.VANNING_MONTH ");
				insCalVanSQL.append("              FROM TB_T_VANNING_MONTH VM ");
				insCalVanSQL.append("             WHERE TO_DATE(VM.VANNING_MONTH, 'Mon-YY') >= ");
				insCalVanSQL.append("                   TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
				insCalVanSQL.append("               AND TO_DATE(VM.VANNING_MONTH, 'Mon-YY') <= ");
				insCalVanSQL.append("                   (SELECT MAX(TO_DATE(T.VOLUME_MONTH, 'Mon-YY')) ");
				insCalVanSQL.append("                      FROM TB_R_KAIKIENG_H T ");
				insCalVanSQL.append("                     WHERE T.VERSION = '"+version+"' ");
				insCalVanSQL.append("                       AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
				insCalVanSQL.append("                       AND T.TIMING = '"+timing+"' ");
				insCalVanSQL.append("                       AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
				insCalVanSQL.append("                       AND T.VEHICLE_MODEL = '"+vehicleModel+"') ");
				insCalVanSQL.append("             GROUP BY VM.VANNING_MONTH)) ");
				insCalVanSQL.append("   ");
				insCalVanSQL.append("  SELECT '"+version+"' VERSION, ");
				insCalVanSQL.append("         TB.GETSUDO_MONTH, ");
				insCalVanSQL.append("         TB.TIMING, ");
				insCalVanSQL.append("         TB.VEHICLE_PLANT, ");
				insCalVanSQL.append("         TB.VEHICLE_MODEL, ");
				insCalVanSQL.append("         TB.UNIT_PLANT, ");
				insCalVanSQL.append("         TB.UNIT_MODEL, ");
				insCalVanSQL.append("         NULL LOT_SIZE, ");
				insCalVanSQL.append("         TB.VANNING_MONTH, ");
				insCalVanSQL.append("         NVL(ROUND(SUM(TB.CAL_VANNING_VOLUME),0),0) VANNING_VOLUME, ");
				insCalVanSQL.append("         NULL ROUNDING_VOLUME, ");
				insCalVanSQL.append("         NULL ADJUST_VOLUME, ");
				insCalVanSQL.append("         '"+userId+"', ");
				insCalVanSQL.append("         ?, ");
				insCalVanSQL.append("         '"+userId+"', ");
				insCalVanSQL.append("         ? ");
				insCalVanSQL.append("    FROM (SELECT T.GETSUDO_MONTH, ");
				insCalVanSQL.append("                 T.TIMING, ");
				insCalVanSQL.append("                 T.VEHICLE_PLANT, ");
				insCalVanSQL.append("                 T.VEHICLE_MODEL, ");
				insCalVanSQL.append("                 MV.UNIT_PLANT, ");
				insCalVanSQL.append("                 T.UNIT_MODEL, ");
				insCalVanSQL.append("                 V.VANNING_MONTH, ");
				insCalVanSQL.append("                 T.VOLUME_MONTH PROD_MONTH, ");
				insCalVanSQL.append("                 T.UNIT_VOLUME PROD_VOLUME, ");
				insCalVanSQL.append("                 NVL(R.RATIO_VOL, 0) RATIO_VOL, ");
				
				//Modify by Thanawut T. 2017/01/10 BCT-IS : In case Multi-source use volume in worksheet Multi-source for calculate
//				insCalVanSQL.append("                 (T.UNIT_VOLUME * NVL(R.RATIO_VOL, 0))/100 CAL_VANNING_VOLUME ");
				insCalVanSQL.append("                 (CASE ");
				insCalVanSQL.append("                          WHEN T.MULTI_SOURCE_FLAG = 'Y' THEN ");
				insCalVanSQL.append("                           (SELECT (MS.UNIT_VOLUME * NVL(R.RATIO_VOL, 0)) / 100 ");
				insCalVanSQL.append("                              FROM TB_R_WS_MULTI_SOURCE_UNITS MS ");
				insCalVanSQL.append("                             WHERE MS.VERSION = T.VERSION ");
				insCalVanSQL.append("                               AND MS.GETSUDO_MONTH = T.GETSUDO_MONTH ");
				insCalVanSQL.append("                               AND MS.TIMING = T.TIMING ");
				insCalVanSQL.append("                               AND MS.VEHICLE_PLANT = T.VEHICLE_PLANT ");
				insCalVanSQL.append("                               AND MS.VEHICLE_MODEL = T.VEHICLE_MODEL ");
				insCalVanSQL.append("                               AND MS.UNIT_PLANT = MV.UNIT_PLANT ");
				insCalVanSQL.append("                               AND MS.UNIT_MODEL = T.UNIT_MODEL ");
				insCalVanSQL.append("                               AND MS.VOLUME_MONTH = T.VOLUME_MONTH) ");
				insCalVanSQL.append("                          ELSE ");
				insCalVanSQL.append("                           (T.UNIT_VOLUME * NVL(R.RATIO_VOL, 0)) / 100 ");
				insCalVanSQL.append("                        END) CAL_VANNING_VOLUME ");
				
				insCalVanSQL.append("            FROM TB_R_KAIKIENG_D T ");
				insCalVanSQL.append("            JOIN (SELECT M.VEHICLE_PLANT, ");
				insCalVanSQL.append("                        M.VEHICLE_MODEL, ");
				insCalVanSQL.append("                        M.UNIT_PLANT, ");
				insCalVanSQL.append("                        M.UNIT_MODEL ");
				insCalVanSQL.append("                   FROM TB_M_VEHICLE_UNIT_RELATION M ");
				insCalVanSQL.append("                  WHERE M.VEHICLE_PLANT = '"+vehiclePlant+"' ");
				insCalVanSQL.append("                    AND M.VEHICLE_MODEL = '"+vehicleModel+"' ");
				insCalVanSQL.append("                    AND TRUNC(M.TC_FROM, 'MONTH') <= ");
				insCalVanSQL.append("                        TO_DATE('"+endMonth+"', 'Mon-YY') ");
				insCalVanSQL.append("                    AND NVL(LAST_DAY(M.TC_TO), ");
				insCalVanSQL.append("                            TO_DATE('31/12/2999', 'DD/MM/YYYY')) >= ");
				insCalVanSQL.append("                        TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
				insCalVanSQL.append("                  GROUP BY M.VEHICLE_PLANT, ");
				insCalVanSQL.append("                           M.VEHICLE_MODEL, ");
				insCalVanSQL.append("                           M.UNIT_PLANT, ");
				insCalVanSQL.append("                           M.UNIT_MODEL) MV ");
				insCalVanSQL.append("              ON MV.VEHICLE_PLANT = T.VEHICLE_PLANT ");
				insCalVanSQL.append("             AND MV.VEHICLE_MODEL = T.VEHICLE_MODEL ");
				insCalVanSQL.append("             AND MV.UNIT_MODEL = T.UNIT_MODEL ");
				insCalVanSQL.append("            JOIN TB_T_VANNING_MON_GETSUDO V ");
				insCalVanSQL.append("              ON V.VANNING_MONTH IS NOT NULL ");
				
				//Add by Thanawut T. in BCT 2017/11/29 Request by Suthida
				insCalVanSQL.append("            JOIN TB_C_PACK_LT LT ");
				insCalVanSQL.append("              ON LT.VEHICLE_PLANT = T.VEHICLE_PLANT ");
				insCalVanSQL.append("            AND LT.UNIT_PLANT = MV.UNIT_PLANT ");
				//End Add
				
				insCalVanSQL.append("            LEFT JOIN TB_T_CAL_RATIO R ");
				insCalVanSQL.append("              ON R.VANNING_MONTH = V.VANNING_MONTH ");
				insCalVanSQL.append("             AND R.PROD_MONTH = T.VOLUME_MONTH ");
				insCalVanSQL.append("           WHERE T.VERSION = '"+version+"' ");
				insCalVanSQL.append("             AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
				insCalVanSQL.append("             AND T.TIMING = '"+timing+"' ");
				insCalVanSQL.append("             AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
				insCalVanSQL.append("             AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
				insCalVanSQL.append("             AND LT.OFFSET_LT > 0 "); //Add by Thanawut T. in BCT 2017/11/29 Request by Suthida
				
				//Add by Thanawut T. in BCT 2017/12/25 issue#5
				insCalVanSQL.append("             AND TO_DATE(V.VANNING_MONTH, 'Mon-YY') <= ADD_MONTHS(to_date('"+endMonth+"', 'Mon-YY'),-LT.OFFSET_LT) "); 
				// End Add
				
				
				insCalVanSQL.append("             ) TB ");
				
				//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
				insCalVanSQL.append("             WHERE TB.GETSUDO_MONTH IS NOT NULL "); // No meaning but add for avoid SQL cause "WHERE 1=1"
				if(unitPlantArr != null && unitPlantArr.length > 0
						&& unitModelArr != null && unitModelArr.length > 0
						&& unitPlantArr.length == unitModelArr.length){
					insCalVanSQL.append("     AND (");
					for(int i=0; i<unitModelArr.length;i++){
						if(i==0){
							insCalVanSQL.append("     (TB.UNIT_PLANT = '"+unitPlantArr[i]+"' AND TB.UNIT_MODEL = '"+unitModelArr[i]+"') ");
						}else{
							insCalVanSQL.append("    OR (TB.UNIT_PLANT = '"+unitPlantArr[i]+"' AND TB.UNIT_MODEL = '"+unitModelArr[i]+"') ");
						}
					}
					insCalVanSQL.append("     )");
				}
				//END CR UT-002 2018/02/16
				
				insCalVanSQL.append("   GROUP BY TB.GETSUDO_MONTH, ");
				insCalVanSQL.append("            TB.TIMING, ");
				insCalVanSQL.append("            TB.VEHICLE_PLANT, ");
				insCalVanSQL.append("            TB.VEHICLE_MODEL, ");
				insCalVanSQL.append("            TB.UNIT_PLANT, ");
				insCalVanSQL.append("            TB.UNIT_MODEL, ");
				insCalVanSQL.append("            TB.VANNING_MONTH ");
				insCalVanSQL.append("   ORDER BY TO_DATE(TB.VANNING_MONTH, 'Mon-YY') ");
				
				ps = conn.prepareStatement(insCalVanSQL.toString());
				ps.setTimestamp(1, sysdate);
				ps.setTimestamp(2, sysdate);
				
				insertCalVanCnt = ps.executeUpdate();
				
				completed = true;
			}else{
				completed = false;
				throw new CommonErrorException(MessagesConstants.B_ERROR_CONCURRENTCY, new String[]{}, AppConstants.ERROR);
			}
	
			completed = true;
		}catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(ps!=null){
						ps.close();
						ps = null;
					}
					
					if(completed){
						conn.commit();
					}else{
						insertPamsCnt = 0;
						insertKompoCnt = 0;
						insertCalVanCnt = 0;
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
		resultMap.put("INSERT_TB_R_PAMS_RUNDOWN", Integer.toString(insertPamsCnt));
		resultMap.put("INSERT_TB_R_KOMPO", Integer.toString(insertKompoCnt));
		resultMap.put("INSERT_TB_R_KOMPO_VAN_RESULT", Integer.toString(insertCalVanCnt));
		if(completed){
			return new Object[]{warning?CST30000Constants.WARNING:CST30000Constants.SUCCESS, resultMap};
		}else{
			return new Object[]{CST30000Constants.ERROR, resultMap};
		}
	}
}