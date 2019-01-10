/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.repository
 * Program ID 	            :  CBW02120PreprocessRepository.java
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;

import com.google.common.base.Strings;

@Repository
public class CBW02120PreprocessRepository implements IBW02120PreprocessRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	@Autowired
	private IBW00000Repository commonRepository;
	
	final Logger logger = LoggerFactory.getLogger(CBW02120PreprocessRepository.class);
	
	private Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();
	
	static final int IDX_L_GETSUDO_MONTH = 0;
	static final int IDX_L_TIMING = 1;
	static final int IDX_L_VEHICLE_PLANT = 2;
	static final int IDX_L_VEHICLE_MODEL = 3;
	static final int IDX_L_UNIT_PLANT = 4;
	static final int IDX_L_UNIT_MODEL = 5;
	static final int IDX_L_ERROR_SHEET = 6;
	static final int IDX_L_ERROR_DATE = 7;
	static final int IDX_L_ERROR_MONTH = 8;
	static final int IDX_L_ERROR_RUNDOWN = 9;
	static final int IDX_L_ERROR_CALENDAR = 10;
	static final int IDX_L_ERROR_WORKSHEET = 11;
	static final int IDX_L_ERROR_STOCK_MIN = 12;
	static final int IDX_L_ERROR_STOCK_MAX = 13;
	
	@Override
	public void setSysdate(Timestamp sysdate){
		this.sysdate = sysdate;
	}
	@Override
	public List<Object[]> getStagingList(Connection conn, String version, String getsudoMonth, String endMonth, String timing,
			String vehiclePlant, String vehicleModel, String unitPlant, String unitModel, String userId, String pamsKompoFlag) throws CommonErrorException{
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT COUNT(*) OVER(PARTITION BY S.PROD_DT) AS DUPCN, ");//INDEX 0
		sql.append("       NVL2(CV.PLANT, 'YES', 'NO') VEHICLE_PLANT_EXIST, "); //INDEX 1
		sql.append("       NVL2(CU.PLANT, 'YES', 'NO') UNIT_PLANT_EXIST, ");
		sql.append("       NVL2(STD.GETSUDO_MONTH, 'YES', 'NO') STOCK_EXIST, ");
		sql.append("       S.GETSUDO_MONTH, ");
		sql.append("       S.TIMING, ");
		sql.append("       S.VEHICLE_PLANT, ");
		sql.append("       S.VEHICLE_MODEL, ");
		sql.append("       S.UNIT_PLANT, ");
		sql.append("       S.UNIT_MODEL, ");
		sql.append("       S.FILE_ID, ");//10
		sql.append("       S.FILE_NAME, ");
		sql.append("       S.IMPORTER, ");
		sql.append("       S.RUNDOWN_KEY, ");
		sql.append("       S.VARIATION, ");
		sql.append("       S.SS_NO, ");
		sql.append("       S.ID_LINE, ");
		sql.append("       S.EXPORTER, ");
		sql.append("       S.ORDER_DT, ");
		sql.append("       S.PROD_DT, "); //INDEX 19
		sql.append("       S.PROD_VOLUME, ");
		sql.append("       S.LOCAL_STOCK, ");
		sql.append("       S.STOCK_DAYS, ");
		sql.append("       S.UNLOAD, ");
		sql.append("       S.TRANSIT, ");
		sql.append("       S.LOADING, ");
		sql.append("       S.PORT_STOCK, ");
		sql.append("       S.PACK_VOLUME, ");
		sql.append("       S.LOT_SIZE, ");
		sql.append("       S.NO_OF_LOT, ");
		sql.append("       S.TOTAL_STOCK, ");
		sql.append("       S.UPLOAD_FILE_NAME, ");
		sql.append("       S.CREATE_BY, ");
		sql.append("       S.APL_ID, ");
		sql.append("       S.RUNNING_NO, ");//34
		
		//PAMS
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			sql.append("      (CASE WHEN (SELECT TO_CHAR(MIN(PROD_DT),'Mon-YY') FROM TB_S_PAMS_RUNDOWN WHERE CREATE_BY = '"+userId+"' GROUP BY CREATE_BY) =  ");
			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
			sql.append("        FROM TB_R_KAIKIENG_D D ");
			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("         AND D.TIMING = S.TIMING ");
			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("         AND D.UNIT_MODEL = S.UNIT_MODEL ");
			sql.append("         AND D.UNIT_VOLUME IS NOT NULL) ");
			sql.append("       THEN 'YES' ELSE 'NO' END) START_PROD_EXIST, ");
		}else{
			sql.append("      (CASE WHEN (SELECT TO_CHAR(MIN(PROD_DT),'Mon-YY') FROM TB_S_PAMS_RUNDOWN WHERE CREATE_BY = '"+userId+"' GROUP BY CREATE_BY) =  ");
			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
			sql.append("        FROM TB_R_KAIKIENG_D D ");
			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("         AND D.TIMING = S.TIMING ");
			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("         AND D.UNIT_VOLUME IS NOT NULL) ");
			sql.append("       THEN 'YES' ELSE 'NO' END) START_PROD_EXIST, ");
		}
		
		//2. Check Vehicle Plant Calendar
		//PAMS
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			sql.append("      (SELECT CASE ");
			sql.append("                WHEN COUNT(1) > 0 THEN ");
			sql.append("                 'YES' ");
			sql.append("                ELSE ");
			sql.append("                 'NO' ");
			sql.append("              END ");
			sql.append("         FROM TB_M_VEHICLE_UNIT_RELATION T ");
			sql.append("        WHERE T.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("          AND T.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("          AND T.UNIT_PLANT = S.UNIT_PLANT ");
			sql.append("          AND T.UNIT_MODEL = S.UNIT_MODEL ");
			sql.append("          AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY')  ");
			sql.append("          AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
			sql.append("              TO_DATE('"+getsudoMonth+"', 'Mon-YY')   ");
			sql.append("          AND C.CALENDAR_DATE BETWEEN T.TC_FROM AND ");
			sql.append("              NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
			sql.append("          AND C.CALENDAR_DATE <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY'))) AS PAMS_VEHICLE_PLANT_CAL_CHK, ");
		}else{
			sql.append("           'NO' AS PAMS_VEHICLE_PLANT_CAL_CHK, ");
		}
		
		//KOMPO
		if(AppConstants.UPLOAD_KOMPO_FLAG.equals(pamsKompoFlag)){
			sql.append("        (SELECT CASE ");
			sql.append("                  WHEN COUNT(1) > 0 THEN ");
			sql.append("                   'YES' ");
			sql.append("                  ELSE ");
			sql.append("                   'NO' ");
			sql.append("                END ");
			sql.append("           FROM TB_M_VEHICLE_UNIT_RELATION T ");
			sql.append("          WHERE T.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("            AND T.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("            AND T.UNIT_PLANT = S.UNIT_PLANT ");
			sql.append("            AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"','Mon-YY') ");
			sql.append("            AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
			sql.append("                TO_DATE('"+getsudoMonth+"','Mon-YY') ");
			sql.append("            AND C.CALENDAR_DATE BETWEEN T.TC_FROM AND ");
			sql.append("                NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
			sql.append("            AND C.CALENDAR_DATE <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY'))) AS KOMPO_VEHICLE_PLANT_CAL_CHK, ");
		}else{
			sql.append("           'NO' AS KOMPO_VEHICLE_PLANT_CAL_CHK, ");
		}
		sql.append("       C.CALENDAR_FLAG AS VEHICLE_PLANT_CAL_FLAG, ");
		sql.append("       (SELECT T.VALUE ");
		sql.append("          FROM TB_M_SYSTEM T ");
		sql.append("         WHERE T.CATEGORY = 'COMMON' ");
		sql.append("           AND T.SUB_CATEGORY = 'CALENDAR_FLAG' ");
		sql.append("           AND T.STATUS = 'Y' ");
		sql.append("           AND T.CD = DECODE(C.CALENDAR_FLAG, 'F', 'F', 'W')) VEHICLE_PLANT_CAL_DIS, ");
		
		//3. Check Unit Plant Calendar
		sql.append("         (SELECT CASE ");
		sql.append("                 WHEN COUNT(1) > 0 THEN ");
		sql.append("                  'YES' ");
		sql.append("                 ELSE  ");
		sql.append("                  'NO' ");
		sql.append("               END ");
//		sql.append("          FROM TB_M_VEHICLE_UNIT_RELATION T ");
//		sql.append("         WHERE T.VEHICLE_PLANT = S.VEHICLE_PLANT ");
//		sql.append("           AND T.VEHICLE_MODEL = S.VEHICLE_MODEL ");
//		sql.append("           AND T.UNIT_PLANT = S.UNIT_PLANT ");
		sql.append("          FROM TB_M_UNIT_PLANT T ");
		sql.append("         WHERE T.UNIT_PLANT = S.UNIT_PLANT ");
		sql.append("           AND T.UNIT_MODEL = S.UNIT_MODEL ");
		sql.append("           AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"','Mon-YY') ");
		sql.append("           AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
		sql.append("               TO_DATE('"+getsudoMonth+"','Mon-YY') ");
		sql.append("           AND CK.CALENDAR_DATE BETWEEN T.TC_FROM AND ");
		sql.append("               NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		sql.append("           AND CK.CALENDAR_DATE <= LAST_DAY(add_months(TO_DATE('"+endMonth+"', 'Mon-YY'), (SELECT NVL(-LT.OFFSET_LT,0) FROM TB_C_PACK_LT LT WHERE LT.VEHICLE_PLANT = S.VEHICLE_PLANT AND LT.UNIT_PLANT = S.UNIT_PLANT))) ");
//		if(AppConstants.UPLOAD_KOMPO_FLAG.equals(pamsKompoFlag)){
//			sql.append(" 		AND EXISTS (SELECT A2.UNIT_PLANT ");
//			sql.append("                              FROM (SELECT A.VEHICLE_MODEL, A.UNIT_PLANT ");
//			sql.append("                                      FROM TB_M_VEHICLE_UNIT_RELATION A ");
//			sql.append("                                     GROUP BY A.VEHICLE_MODEL, A.UNIT_PLANT) A2 ");
//			sql.append("                                     WHERE A2.UNIT_PLANT = T.UNIT_PLANT ");
//			sql.append("                            GROUP BY A2.UNIT_PLANT ");
//			sql.append("                            HAVING COUNT(1) = 1) ");
//		}
		sql.append("		) AS KOMPO_UNIT_PLANT_CAL_CHK, ");
		sql.append("           CK.CALENDAR_FLAG KOMPO_UNIT_PLANT_CAL_FLG, ");
		sql.append("           (SELECT VALUE ");
		sql.append("          FROM TB_M_SYSTEM ");
		sql.append("         WHERE CATEGORY = 'COMMON' ");
		sql.append("           AND SUB_CATEGORY = 'CALENDAR_FLAG' ");
		sql.append("           AND STATUS = 'Y' ");
		sql.append("           AND CD = DECODE(CK.CALENDAR_FLAG, 'F', 'F', 'W')) KOMPO_UNIT_PLANT_CAL_DIS, ");
		
		//4. Check Stock Value
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
		sql.append("       (SELECT CASE ");
		sql.append("                 WHEN COUNT(1) > 0 THEN ");
		sql.append("                  'YES' ");
		sql.append("                 ELSE ");
		sql.append("                  'NO' ");
		sql.append("               END ");
		sql.append("          FROM TB_M_VEHICLE_UNIT_RELATION T ");
		sql.append("         WHERE T.VEHICLE_PLANT = S.VEHICLE_PLANT ");
		sql.append("           AND T.VEHICLE_MODEL = S.VEHICLE_MODEL ");
		sql.append("           AND T.UNIT_PLANT = S.UNIT_PLANT ");
		sql.append("           AND T.UNIT_MODEL = S.UNIT_MODEL ");
		sql.append("           AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		sql.append("           AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
		sql.append("               TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		sql.append("         AND to_date(std.STOCK_MONTH,'Mon-YY') BETWEEN T.TC_FROM AND ");
		sql.append("            NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		sql.append("        AND to_date(std.STOCK_MONTH,'Mon-YY') <= LAST_DAY(add_months(TO_DATE('"+endMonth+"', 'Mon-YY'), (SELECT NVL(-LT.OFFSET_LT,0) FROM TB_C_PACK_LT LT WHERE LT.VEHICLE_PLANT = S.VEHICLE_PLANT AND LT.UNIT_PLANT = S.UNIT_PLANT))) ");
		sql.append("        AND S.STOCK_DAYS NOT BETWEEN std.STOCK_MIN AND std.STOCK_MAX ");
		sql.append("        ) IS_STOCK_ERROR_CHK, ");
		sql.append("        STD.STOCK_MIN, ");
		sql.append("        STD.STOCK_MAX, ");
		}else{
			sql.append("    'NO' IS_STOCK_ERROR_CHK, ");
			sql.append("    0 STOCK_MIN, ");
			sql.append("    0 STOCK_MAX, ");
		}
		
		sql.append("    MIN(S.PROD_DT) OVER (PARTITION BY S.CREATE_BY) AS START_PROD_DT, ");
		
		sql.append("   (SELECT (CASE WHEN COUNT(1) = 1 THEN 'YES' ELSE 'NO' END) ");
	    sql.append("      FROM (SELECT V.VEHICLE_PLANT, V.VEHICLE_MODEL, V.UNIT_PLANT ");
	    sql.append("              FROM TB_M_VEHICLE_UNIT_RELATION V ");
	    sql.append("             WHERE V.VEHICLE_PLANT = S.VEHICLE_PLANT ");
	    sql.append("               AND V.VEHICLE_MODEL = S.VEHICLE_MODEL ");
	    sql.append("               AND V.TC_FROM <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY')) ");
	    sql.append("               AND NVL(V.TC_TO, TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
	    sql.append("             GROUP BY V.VEHICLE_PLANT, V.VEHICLE_MODEL, V.UNIT_PLANT)) IS_VEHICLE_PER_UNIT, ");
		
	    //PAMS - Start Effective Month in Kaikieng Detail Table
//	    if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
//			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
//			sql.append("        FROM TB_R_KAIKIENG_D D, TB_M_VEHICLE_UNIT_RELATION M ");
//			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
//			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
//			sql.append("         AND D.TIMING = S.TIMING ");
//			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
//			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
//			sql.append("         AND D.UNIT_MODEL = S.UNIT_MODEL ");
//			sql.append("         AND D.VEHICLE_PLANT = M.VEHICLE_PLANT ");
//			sql.append("         AND D.VEHICLE_MODEL = M.VEHICLE_MODEL ");
//			sql.append("         AND D.UNIT_MODEL = M.UNIT_MODEL ");
//			sql.append("         AND M.UNIT_PLANT = '"+unitPlant+"' ");
//			sql.append("         AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
//			sql.append("         AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
//			sql.append("         AND TO_DATE(D.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
//			sql.append("         ) START_EFF_KK_MONTH, ");
//		}else{
//			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
//			sql.append("        FROM TB_R_KAIKIENG_D D, TB_M_VEHICLE_UNIT_RELATION M ");
//			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
//			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
//			sql.append("         AND D.TIMING = S.TIMING ");
//			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
//			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
//			sql.append("         AND D.VEHICLE_PLANT = M.VEHICLE_PLANT ");
//			sql.append("         AND D.VEHICLE_MODEL = M.VEHICLE_MODEL ");
//			sql.append("         AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
//			sql.append("         AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
//			sql.append("         AND TO_DATE(D.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
//			sql.append("         ) START_EFF_KK_MONTH, ");
//		}
	    if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
			sql.append("        FROM TB_R_KAIKIENG_D D ");
			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("         AND D.TIMING = S.TIMING ");
			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("         AND D.UNIT_MODEL = S.UNIT_MODEL ");
			sql.append("         AND D.UNIT_VOLUME IS NOT NULL) START_EFF_KK_MONTH, ");
		}else{
			sql.append("		(SELECT TO_CHAR(MIN(TO_DATE(D.VOLUME_MONTH, 'Mon-YY')), 'Mon-YY') ");
			sql.append("        FROM TB_R_KAIKIENG_D D ");
			sql.append("        WHERE D.VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
			sql.append("         AND D.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("         AND D.TIMING = S.TIMING ");
			sql.append("         AND D.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("         AND D.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("         AND D.UNIT_VOLUME IS NOT NULL) START_EFF_KK_MONTH, ");
		}
	    
	    
	    sql.append("(SELECT CASE ");
	    sql.append("        WHEN COUNT(1) > 0 THEN ");
	    sql.append("         'YES' ");
	    sql.append("        ELSE ");
	    sql.append("         'NO' ");
	    sql.append("      END ");
	    sql.append(" FROM TB_M_VEHICLE_UNIT_RELATION T ");
	    sql.append("WHERE T.VEHICLE_PLANT = S.VEHICLE_PLANT ");
	    sql.append("  AND T.VEHICLE_MODEL = S.VEHICLE_MODEL ");
	    sql.append("  AND T.UNIT_PLANT = S.UNIT_PLANT ");
	    sql.append("  AND T.UNIT_MODEL = S.UNIT_MODEL ");
	    sql.append("  AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
	    sql.append("  AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
	    sql.append("  AND S.PROD_DT BETWEEN T.TC_FROM AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
	    sql.append("  AND S.PROD_DT <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY'))) IS_EFF_IN_RELATION_CHK ");
	    
	    //CR 2018/02/01 Thanawut T. - UT phase inc no.3
	    sql.append(", ");
	    if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
		    sql.append("       (SELECT ");
		    sql.append("       		(CASE ");
		    sql.append("               WHEN T.MULTI_SOURCE_FLAG = 'Y' THEN ");
		    sql.append("                MS.UNIT_VOLUME ");
		    sql.append("               ELSE ");
		    sql.append("                T.UNIT_VOLUME ");
		    sql.append("             END) UNIT_VOLUME ");
		    sql.append("          FROM TB_M_VEHICLE_UNIT_RELATION M, TB_R_KAIKIENG_D T ");
		    sql.append("          LEFT JOIN TB_R_WS_MULTI_SOURCE_UNITS MS ");
		    sql.append("          ON T.GETSUDO_MONTH = MS.GETSUDO_MONTH ");
		    sql.append("          AND T.TIMING = MS.TIMING ");
		    sql.append("          AND T.VEHICLE_PLANT = MS.VEHICLE_PLANT ");
		    sql.append("          AND T.VEHICLE_MODEL = MS.VEHICLE_MODEL ");
		    sql.append("          AND T.UNIT_MODEL = MS.UNIT_MODEL ");
		    sql.append("          AND T.VOLUME_MONTH = MS.VOLUME_MONTH ");
		    sql.append("          AND MS.UNIT_PLANT = '"+unitPlant+"' ");
		    sql.append("         WHERE M.VEHICLE_PLANT = T.VEHICLE_PLANT ");
		    sql.append("           AND M.VEHICLE_MODEL = T.VEHICLE_MODEL ");
		    sql.append("           AND M.UNIT_MODEL = T.UNIT_MODEL ");
		    sql.append("           AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		    sql.append("           AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		    sql.append("           AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		    sql.append("           AND T.VERSION = '"+version+"' ");
		    sql.append("           AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		    sql.append("           AND T.TIMING = '"+timing+"' ");
		    sql.append("           AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		    sql.append("           AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
		    sql.append("           AND M.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		    sql.append("           AND M.VEHICLE_MODEL = '"+vehicleModel+"' ");
		    sql.append("           AND M.UNIT_PLANT = '"+unitPlant+"' ");
		    sql.append("           AND M.UNIT_MODEL = '"+unitModel+"' ");
		    sql.append("           AND S.GETSUDO_MONTH = T.GETSUDO_MONTH ");
		    sql.append("           AND S.TIMING = T.TIMING ");
		    sql.append("           AND S.VEHICLE_PLANT = M.VEHICLE_PLANT ");
		    sql.append("           AND S.VEHICLE_MODEL = M.VEHICLE_MODEL ");
		    sql.append("           AND S.UNIT_PLANT = M.UNIT_PLANT ");
		    sql.append("           AND S.UNIT_MODEL = M.UNIT_MODEL ");
		    sql.append("           AND TO_CHAR(S.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH) UNIT_VOLUME_KK ");
	    }else{
	    	sql.append("       (SELECT T.VEHICLE_VOLUME ");
		    sql.append("          FROM TB_M_VEHICLE_PLANT M, TB_R_KAIKIENG_H T ");
		    sql.append("         WHERE M.VEHICLE_PLANT = T.VEHICLE_PLANT ");
		    sql.append("           AND M.VEHICLE_MODEL = T.VEHICLE_MODEL ");
		    sql.append("           AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		    sql.append("           AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		    sql.append("           AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		    sql.append("           AND T.VERSION = '"+version+"' ");
		    sql.append("           AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		    sql.append("           AND T.TIMING = '"+timing+"' ");
		    sql.append("           AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		    sql.append("           AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
		    sql.append("           AND M.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		    sql.append("           AND M.VEHICLE_MODEL = '"+vehicleModel+"' ");
		    sql.append("           AND S.GETSUDO_MONTH = T.GETSUDO_MONTH ");
		    sql.append("           AND S.TIMING = T.TIMING ");
		    sql.append("           AND S.VEHICLE_PLANT = M.VEHICLE_PLANT ");
		    sql.append("           AND S.VEHICLE_MODEL = M.VEHICLE_MODEL ");
		    sql.append("           AND TO_CHAR(S.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH) UNIT_VOLUME_KK ");
	    }
	    if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
		    sql.append(" ,(SELECT MAX(T.PACKING_VOLUME) PACKING_VOLUME ");
		    sql.append("          FROM TB_R_WS_PACKING_MOVEMENT T, TB_M_UNIT_PLANT M ");
		    sql.append("         WHERE M.UNIT_PLANT = T.UNIT_PLANT ");
		    sql.append("           AND M.UNIT_MODEL = T.UNIT_MODEL ");
		    sql.append("           AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		    sql.append("           AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		    sql.append("           AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		    sql.append("           AND T.VERSION = '"+version+"' ");
		    sql.append("           AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		    sql.append("           AND T.TIMING = '"+timing+"' ");
		    sql.append("           AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		    sql.append("           AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
		    sql.append("           AND M.UNIT_PLANT = '"+unitPlant+"' ");
		    sql.append("           AND M.UNIT_MODEL = '"+unitModel+"' ");
		    sql.append("           AND S.GETSUDO_MONTH = T.GETSUDO_MONTH ");
		    sql.append("           AND S.TIMING = T.TIMING ");
		    sql.append("           AND S.UNIT_PLANT = M.UNIT_PLANT ");
		    sql.append("           AND S.UNIT_MODEL = M.UNIT_MODEL ");
		    sql.append("           AND TO_CHAR(S.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH ");
		    sql.append("           GROUP BY T.VERSION,T.TIMING,T.VEHICLE_PLANT,T.VEHICLE_MODEL,T.VOLUME_MONTH ");
		    sql.append("           ) PACKING_VOLUME");
	    }else{
	    	sql.append(",(SELECT MAX(T.PACKING_VOLUME) ");
	    	sql.append("          FROM (SELECT GETSUDO_MONTH, TIMING, VEHICLE_PLANT, VEHICLE_MODEL, UNIT_PLANT, UNIT_MODEL, ");
	    	sql.append("                       TO_CHAR(PROD_DT, 'Mon-YY') VOLUME_MONTH, ");
	    	sql.append("                       SUM(PACK_VOLUME) PACKING_VOLUME ");
	    	sql.append("                  FROM TB_S_PAMS_RUNDOWN ");
	    	sql.append("                 WHERE CREATE_BY = '"+userId+"' ");
	    	sql.append("                 AND GETSUDO_MONTH = '"+getsudoMonth+"' ");
	    	sql.append("          	 	 AND TIMING = '"+timing+"' ");
	    	sql.append("           		 AND VEHICLE_PLANT = '"+vehiclePlant+"' ");
	    	sql.append("           		 AND VEHICLE_MODEL = '"+vehicleModel+"' ");
	    	sql.append("                 GROUP BY GETSUDO_MONTH, TIMING, VEHICLE_PLANT, VEHICLE_MODEL, UNIT_PLANT, UNIT_MODEL, TO_CHAR(PROD_DT, 'Mon-YY')) T ");
	    	sql.append(" , TB_M_UNIT_PLANT M ");
	    	sql.append("         WHERE M.UNIT_PLANT = T.UNIT_PLANT ");
	    	sql.append("           AND M.UNIT_MODEL = T.UNIT_MODEL ");
	    	sql.append("           AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
	    	sql.append("           AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
	    	sql.append("               TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
	    	sql.append("           AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND ");
	    	sql.append("               NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
	    	sql.append("           AND S.GETSUDO_MONTH = T.GETSUDO_MONTH ");
	    	sql.append("           AND S.TIMING = T.TIMING ");
	    	sql.append("           AND S.UNIT_PLANT = M.UNIT_PLANT ");
	    	sql.append("           AND S.UNIT_MODEL = M.UNIT_MODEL ");
	    	sql.append("           AND TO_CHAR(S.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH ");
	    	sql.append("         GROUP BY T.GETSUDO_MONTH, ");
	    	sql.append("                  T.TIMING, ");
	    	sql.append("                  T.VEHICLE_PLANT, ");
	    	sql.append("                  T.VEHICLE_MODEL, ");
	    	sql.append("                  T.VOLUME_MONTH) PACKING_VOLUME ");
	    	
//	    	sql.append(" ,(SELECT MAX(T.PACKING_VOLUME) PACKING_VOLUME ");
//	    	sql.append("  		   FROM TB_R_WS_PACKING_MOVEMENT T, TB_M_UNIT_PLANT M ");
//	    	sql.append(" 		   WHERE M.UNIT_PLANT = T.UNIT_PLANT ");
//	    	sql.append("   		   AND M.UNIT_MODEL = T.UNIT_MODEL ");
//	    	sql.append("   		   AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
//	    	sql.append("   		   AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= ");
//	    	sql.append("   		       TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
//	    	sql.append("   		   AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND ");
//	    	sql.append("   		       NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
//	    	sql.append("   		   AND T.VERSION = '"+version+"' ");
//	    	sql.append("   		   AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
//	    	sql.append("   		   AND T.TIMING = '"+timing+"' ");
//	    	sql.append("   		   AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
//	    	sql.append("   		   AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
//	    	
//		    sql.append("           AND S.GETSUDO_MONTH = T.GETSUDO_MONTH ");
//		    sql.append("           AND S.TIMING = T.TIMING ");
//		    sql.append("           AND S.UNIT_PLANT = M.UNIT_PLANT ");
//		    sql.append("           AND S.UNIT_MODEL = M.UNIT_MODEL ");
//		    sql.append("           AND TO_CHAR(S.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH ");
//		    sql.append("           GROUP BY T.VERSION,T.TIMING,T.VEHICLE_PLANT,T.VEHICLE_MODEL,T.VOLUME_MONTH ");
//		    sql.append("           ) PACKING_VOLUME");
	    }
	    //END CR 2018/02/01 Thanawut T. - UT phase inc no.3
		
	    sql.append("  FROM TB_S_PAMS_RUNDOWN S ");
		
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
		
		sql.append("  LEFT JOIN TB_M_STD_STOCK STD ");
		sql.append("    ON S.GETSUDO_MONTH = STD.GETSUDO_MONTH ");
		sql.append("   AND S.TIMING = STD.TIMING ");
		sql.append("   AND S.VEHICLE_PLANT = STD.VEHICLE_PLANT ");
		sql.append("   AND S.VEHICLE_MODEL = STD.VEHICLE_MODEL ");
		sql.append("   AND S.UNIT_PLANT = STD.UNIT_PLANT ");
		sql.append("   AND S.UNIT_MODEL = STD.UNIT_MODEL ");
		sql.append("   AND TO_CHAR(S.PROD_DT, 'Mon-YY') = STD.STOCK_MONTH ");
		
		sql.append("  LEFT JOIN TB_M_CALENDAR C ");
		sql.append("    ON C.GETSUDO_MONTH = S.GETSUDO_MONTH ");
		sql.append("   AND C.TIMING = S.TIMING ");
		sql.append("   AND (C.CALENDAR_DATE) = (S.PROD_DT) ");
		sql.append("   AND C.PLANT = S.VEHICLE_PLANT ");
		sql.append("   AND C.PLANT_TYPE = 'V' ");
		
		sql.append("   LEFT JOIN TB_M_CALENDAR CK ");
		sql.append("    ON CK.GETSUDO_MONTH = S.GETSUDO_MONTH ");
		sql.append("   AND CK.TIMING = S.TIMING ");
		sql.append("   AND CK.CALENDAR_DATE = S.PROD_DT ");
		sql.append("   AND CK.PLANT = S.UNIT_PLANT ");
		sql.append("   AND CK.PLANT_TYPE = 'U' ");
		
		sql.append(" WHERE S.CREATE_BY = '"+userId+"' ");
		sql.append("   AND S.PROD_DT <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY')) ");
		sql.append(" ORDER BY S.RUNNING_NO ");
		
		System.out.println(sql.toString());
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
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	
	//5. Check Vehicle Production Volume between Worksheet 
	@Override
	public List<Object[]> getVehicleProdVolumeWithWorksheet(Connection conn, String pamsKompoFlag, String version, String getsudoMonth, String endMonth, String timing, String vehiclePlant,
			String vehicleModel, String unitModel, String unitPlant, String userId) throws CommonErrorException{
		StringBuilder sql = new StringBuilder();
		int totalSelectColum = 0;
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			totalSelectColum = 9;
			sql.append("SELECT K.VOLUME_MONTH, ");
			sql.append("       NVL(S.PROD_VOLUME_MONTH, 0) PROD_VOLUME_MONTH, ");
			sql.append("       K.UNIT_VOLUME, ");
			sql.append("       K.GETSUDO_MONTH, ");
			sql.append("       K.TIMING, ");
			sql.append("       K.VEHICLE_PLANT, ");
			sql.append("       K.VEHICLE_MODEL, ");
			sql.append("       K.UNIT_MODEL, ");
			sql.append("       '' UNIT_PLANT ");
			sql.append("  FROM TB_R_KAIKIENG_D K ");
			sql.append("  LEFT JOIN (SELECT SP.GETSUDO_MONTH, ");
			sql.append("                    SP.TIMING, ");
			sql.append("                    SP.VEHICLE_PLANT, ");
			sql.append("                    SP.VEHICLE_MODEL, ");
			sql.append("                    SP.UNIT_MODEL, ");
			sql.append("                    TO_CHAR(SP.PROD_DT, 'Mon-YY') PROD_MONTH, ");
			sql.append("                    SUM(SP.PROD_VOLUME) PROD_VOLUME_MONTH ");
			sql.append("               FROM TB_S_PAMS_RUNDOWN SP, TB_M_VEHICLE_UNIT_RELATION T ");
			sql.append("              WHERE T.VEHICLE_PLANT = SP.VEHICLE_PLANT ");
			sql.append("                AND T.VEHICLE_MODEL = SP.VEHICLE_MODEL ");
			sql.append("                AND T.UNIT_PLANT = SP.UNIT_PLANT ");
			sql.append("                AND T.UNIT_MODEL = SP.UNIT_MODEL ");
			sql.append("                AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
			sql.append("                AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
			sql.append("                AND SP.PROD_DT BETWEEN T.TC_FROM AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
			sql.append("                AND SP.PROD_DT <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY')) ");
			sql.append("                AND SP.CREATE_BY = '"+userId+"' ");
			sql.append("              GROUP BY SP.GETSUDO_MONTH, ");
			sql.append("                       SP.TIMING, ");
			sql.append("                       SP.VEHICLE_PLANT, ");
			sql.append("                       SP.VEHICLE_MODEL, ");
			sql.append("                       SP.UNIT_MODEL, ");
			sql.append("                       TO_CHAR(SP.PROD_DT, 'Mon-YY')) S ");
			sql.append("    ON K.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("   AND K.TIMING = S.TIMING ");
			sql.append("   AND K.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("   AND K.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("   AND K.UNIT_MODEL = S.UNIT_MODEL ");
			sql.append("   AND K.VOLUME_MONTH = S.PROD_MONTH ");
			sql.append(" WHERE K.VERSION = '"+version+"' ");
			sql.append("   AND K.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("   AND K.TIMING = '"+timing+"' ");
			sql.append("   AND K.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("   AND K.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("   AND K.UNIT_MODEL = '"+unitModel+"' ");
			sql.append("   AND NVL(K.UNIT_VOLUME, 0) <> NVL(S.PROD_VOLUME_MONTH, 0) ");
			//CR 2018/02/01 Thanawut T. - UT phase inc no.3
			sql.append("   AND K.UNIT_VOLUME > 0 ");
			//END CR 2018/02/01 Thanawut T. - UT phase inc no.3
			sql.append("   AND K.MULTI_SOURCE_FLAG = 'N' ");
			sql.append(" ");
			sql.append("UNION ALL ");
			sql.append(" ");
			sql.append("SELECT K.VOLUME_MONTH, ");
			sql.append("       NVL(S.PROD_VOLUME_MONTH, 0) PROD_VOLUME_MONTH, ");
			sql.append("       MS.UNIT_VOLUME, ");
			sql.append("       K.GETSUDO_MONTH, ");
			sql.append("       K.TIMING, ");
			sql.append("       K.VEHICLE_PLANT, ");
			sql.append("       K.VEHICLE_MODEL, ");
			sql.append("       K.UNIT_MODEL, ");
			sql.append("       MS.UNIT_PLANT ");
			sql.append("  FROM TB_R_KAIKIENG_D K ");
			sql.append("  LEFT JOIN TB_R_WS_MULTI_SOURCE_UNITS MS ");
			sql.append("    ON K.GETSUDO_MONTH = MS.GETSUDO_MONTH ");
			sql.append("   AND K.TIMING = MS.TIMING ");
			sql.append("   AND K.VEHICLE_PLANT = MS.VEHICLE_PLANT ");
			sql.append("   AND K.VEHICLE_MODEL = MS.VEHICLE_MODEL ");
			sql.append("   AND K.UNIT_MODEL = MS.UNIT_MODEL ");
			sql.append("   AND K.VOLUME_MONTH = MS.VOLUME_MONTH ");
			sql.append("  LEFT JOIN (SELECT SP.GETSUDO_MONTH, ");
			sql.append("                    SP.TIMING, ");
			sql.append("                    SP.VEHICLE_PLANT, ");
			sql.append("                    SP.VEHICLE_MODEL, ");
			sql.append("                    SP.UNIT_PLANT, ");
			sql.append("                    SP.UNIT_MODEL, ");
			sql.append("                    TO_CHAR(SP.PROD_DT, 'Mon-YY') PROD_MONTH, ");
			sql.append("                    SUM(SP.PROD_VOLUME) PROD_VOLUME_MONTH ");
			sql.append("               FROM TB_S_PAMS_RUNDOWN SP, TB_M_VEHICLE_UNIT_RELATION T ");
			sql.append("              WHERE T.VEHICLE_PLANT = SP.VEHICLE_PLANT ");
			sql.append("                AND T.VEHICLE_MODEL = SP.VEHICLE_MODEL ");
			sql.append("                AND T.UNIT_PLANT = SP.UNIT_PLANT ");
			sql.append("                AND T.UNIT_MODEL = SP.UNIT_MODEL ");
			sql.append("                AND TRUNC(T.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
			sql.append("                AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
			sql.append("                AND SP.PROD_DT BETWEEN T.TC_FROM AND NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
			sql.append("                AND SP.PROD_DT <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY')) ");
			sql.append("                AND SP.CREATE_BY = '"+userId+"' ");
			sql.append("              GROUP BY SP.GETSUDO_MONTH, ");
			sql.append("                       SP.TIMING, ");
			sql.append("                       SP.VEHICLE_PLANT, ");
			sql.append("                       SP.VEHICLE_MODEL, ");
			sql.append("                       SP.UNIT_PLANT, ");
			sql.append("                       SP.UNIT_MODEL, ");
			sql.append("                       TO_CHAR(SP.PROD_DT, 'Mon-YY')) S ");
			sql.append("    ON K.GETSUDO_MONTH = S.GETSUDO_MONTH ");
			sql.append("   AND K.TIMING = S.TIMING ");
			sql.append("   AND K.VEHICLE_PLANT = S.VEHICLE_PLANT ");
			sql.append("   AND K.VEHICLE_MODEL = S.VEHICLE_MODEL ");
			sql.append("   AND K.UNIT_MODEL = S.UNIT_MODEL ");
			sql.append("   AND MS.UNIT_PLANT = S.UNIT_PLANT ");
			sql.append("   AND K.VOLUME_MONTH = S.PROD_MONTH ");
			sql.append(" WHERE K.VERSION = '"+version+"' ");
			sql.append("   AND K.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("   AND K.TIMING = '"+timing+"' ");
			sql.append("   AND K.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("   AND K.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("   AND K.UNIT_MODEL = '"+unitModel+"' ");
			sql.append("   AND K.MULTI_SOURCE_FLAG = 'Y' ");
			//CR 2018/02/01 Thanawut T. - UT phase inc no.3
			sql.append("   AND K.UNIT_VOLUME > 0 ");
			//END CR 2018/02/01 Thanawut T. - UT phase inc no.3
			sql.append("   AND NVL(MS.UNIT_VOLUME, 0) <> NVL(S.PROD_VOLUME_MONTH, 0) ");
			sql.append("   AND MS.VERSION = '"+version+"' ");
			sql.append("   AND MS.UNIT_PLANT = '"+unitPlant+"' ");
			
		}else if(AppConstants.UPLOAD_KOMPO_FLAG.equals(pamsKompoFlag)){
			totalSelectColum = 7;
			sql.append("SELECT K.VOLUME_MONTH, ");
			sql.append("       NVL(SU.PROD_VOLUME_MONTH, 0) PROD_VOLUME_MONTH, ");
			sql.append("       K.VEHICLE_VOLUME, ");
			sql.append("       K.GETSUDO_MONTH, ");
			sql.append("       K.TIMING, ");
			sql.append("       K.VEHICLE_PLANT, ");
			sql.append("       K.VEHICLE_MODEL ");
			sql.append("  FROM (SELECT D.VERSION, ");
			sql.append("               D.GETSUDO_MONTH, ");
			sql.append("               D.TIMING, ");
			sql.append("               D.VEHICLE_PLANT, ");
			sql.append("               D.VEHICLE_MODEL, ");
			sql.append("               D.VOLUME_MONTH, ");
			sql.append("               (SELECT H.VEHICLE_VOLUME ");
			sql.append("                  FROM TB_R_KAIKIENG_H H ");
			sql.append("                 WHERE H.VERSION = D.VERSION ");
			sql.append("                   AND H.GETSUDO_MONTH = D.GETSUDO_MONTH ");
			sql.append("                   AND H.TIMING = D.TIMING ");
			sql.append("                   AND H.VEHICLE_PLANT = D.VEHICLE_PLANT ");
			sql.append("                   AND H.VEHICLE_MODEL = D.VEHICLE_MODEL ");
			sql.append("                   AND H.VOLUME_MONTH = D.VOLUME_MONTH) VEHICLE_VOLUME ");
			sql.append("          FROM TB_R_KAIKIENG_D D ");
			sql.append("         WHERE D.VERSION = '"+version+"' ");
			sql.append("           AND D.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("           AND D.TIMING = '"+timing+"' ");
			sql.append("           AND D.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND D.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("           AND D.UNIT_VOLUME IS NOT NULL ");
			sql.append("         GROUP BY D.VERSION, ");
			sql.append("                  D.GETSUDO_MONTH, ");
			sql.append("                  D.TIMING, ");
			sql.append("                  D.VEHICLE_PLANT, ");
			sql.append("                  D.VEHICLE_MODEL, ");
			sql.append("                  D.VOLUME_MONTH) K ");
			sql.append("  LEFT JOIN (SELECT SP.GETSUDO_MONTH, ");
			sql.append("       SP.TIMING, ");
			sql.append("       SP.VEHICLE_PLANT, ");
			sql.append("       SP.VEHICLE_MODEL, ");
			sql.append("       TO_CHAR(SP.PROD_DT, 'Mon-YY') PROD_MONTH, ");
			sql.append("       SUM(SP.PROD_VOLUME) PROD_VOLUME_MONTH ");
			sql.append("  FROM TB_S_PAMS_RUNDOWN SP, ");
			sql.append("       (SELECT V.VEHICLE_PLANT, ");
			sql.append("               V.VEHICLE_MODEL, ");
			sql.append("               MIN(V.TC_FROM) TC_FROM, ");
			sql.append("               MAX(NVL(V.TC_TO, to_date('"+AppConstants.DEFULAT_DATE+"','DD/MM/YYYY'))) TC_TO ");
			sql.append("          FROM TB_M_VEHICLE_UNIT_RELATION V ");
			sql.append("         WHERE V.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND V.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("           AND TRUNC(V.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
			sql.append("           AND NVL(LAST_DAY(V.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
			sql.append("         GROUP BY V.VEHICLE_PLANT, V.VEHICLE_MODEL) T ");
			sql.append(" WHERE T.VEHICLE_PLANT = SP.VEHICLE_PLANT ");
			sql.append("   AND T.VEHICLE_MODEL = SP.VEHICLE_MODEL ");
			sql.append("   AND SP.PROD_DT BETWEEN T.TC_FROM AND ");
			sql.append("       NVL(LAST_DAY(T.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
			sql.append("   AND SP.PROD_DT <= LAST_DAY(TO_DATE('"+endMonth+"', 'Mon-YY')) ");
			sql.append("   AND SP.CREATE_BY = '"+userId+"' ");
			sql.append(" GROUP BY SP.GETSUDO_MONTH, ");
			sql.append("          SP.TIMING, ");
			sql.append("          SP.VEHICLE_PLANT, ");
			sql.append("          SP.VEHICLE_MODEL, ");
			sql.append("          TO_CHAR(SP.PROD_DT, 'Mon-YY')) SU ");
			sql.append("    ON K.GETSUDO_MONTH = SU.GETSUDO_MONTH ");
			sql.append("   AND K.TIMING = SU.TIMING ");
			sql.append("   AND K.VEHICLE_PLANT = SU.VEHICLE_PLANT ");
			sql.append("   AND K.VEHICLE_MODEL = SU.VEHICLE_MODEL ");
			sql.append("   AND K.VOLUME_MONTH = SU.PROD_MONTH ");
			sql.append(" WHERE NVL(SU.PROD_VOLUME_MONTH, 0) <> NVL(K.VEHICLE_VOLUME, 0) ");
		}else{
			return new ArrayList<>();
		}
		
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
				for(int col=1;col<=totalSelectColum; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	public List<Object[]> getPackingVolumeWorksheetVsPams(Connection conn, String version, String getsudoMonth, String endMonth, String timing, 	
												String vehiclePlant, String vehicleModel, 
												String unitPlant, String unitModel, String userId)  throws CommonErrorException{
		int totalSelectColum = 10;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT S.PROD_MONTH, ");
		sql.append("       S.GETSUDO_MONTH, ");
		sql.append("       S.TIMING, ");
		sql.append("       S.VEHICLE_PLANT, ");
		sql.append("       S.VEHICLE_MODEL, ");
		sql.append("       S.UNIT_MODEL, ");
		sql.append("       S.UNIT_PLANT, ");
		sql.append("       NVL(S.PACK_VOLUME_MONTH, 0) PACK_VOLUME_MONTH, ");
		sql.append("       P.VOLUME_MONTH, ");
		sql.append("       P.PACKING_VOLUME ");
		sql.append("  FROM (SELECT GETSUDO_MONTH, ");
		sql.append("               TIMING, ");
		sql.append("               VEHICLE_PLANT, ");
		sql.append("               VEHICLE_MODEL, ");
		sql.append("               UNIT_PLANT, ");
		sql.append("               UNIT_MODEL, ");
		sql.append("               TO_CHAR(PROD_DT, 'Mon-YY') PROD_MONTH, ");
		sql.append("               SUM(PACK_VOLUME) PACK_VOLUME_MONTH ");
		sql.append("          FROM TB_S_PAMS_RUNDOWN ");
		sql.append("         WHERE CREATE_BY = '"+userId+"' ");
		sql.append("         GROUP BY GETSUDO_MONTH, ");
		sql.append("                  TIMING, ");
		sql.append("                  VEHICLE_PLANT, ");
		sql.append("                  VEHICLE_MODEL, ");
		sql.append("                  UNIT_PLANT, ");
		sql.append("                  UNIT_MODEL, ");
		sql.append("                  TO_CHAR(PROD_DT, 'Mon-YY')) S ");
		//CR - 2018/02/12 Thanawut T. (UT) do not validate packing volume which not effective in unit plant master
		sql.append("  JOIN TB_M_UNIT_PLANT M ");
		sql.append("    ON M.UNIT_PLANT = S.UNIT_PLANT ");
		sql.append("   AND M.UNIT_MODEL = S.UNIT_MODEL ");
		sql.append("   AND TO_DATE(PROD_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		sql.append("   AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		sql.append("   AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		//END CR 2018/02/12
		sql.append("  LEFT JOIN TB_R_WS_PACKING_MOVEMENT P ");
		sql.append("    ON S.GETSUDO_MONTH = P.GETSUDO_MONTH ");
		sql.append("   AND S.TIMING = P.TIMING ");
		sql.append("   AND S.VEHICLE_PLANT = P.VEHICLE_PLANT ");
		sql.append("   AND S.VEHICLE_MODEL = P.VEHICLE_MODEL ");
		sql.append("   AND S.UNIT_PLANT = P.UNIT_PLANT ");
		sql.append("   AND S.UNIT_MODEL = P.UNIT_MODEL ");
		sql.append("   AND S.PROD_MONTH = P.VOLUME_MONTH ");
		sql.append(" WHERE TO_DATE(S.PROD_MONTH, 'Mon-YY') <= TO_DATE(P.VOLUME_MONTH, 'Mon-YY') ");
		sql.append("   AND S.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		sql.append("   AND S.TIMING = '"+timing+"' ");
		sql.append("   AND S.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		sql.append("   AND S.VEHICLE_MODEL = '"+vehicleModel+"' ");
		sql.append("   AND S.UNIT_PLANT = '"+unitPlant+"' ");
		sql.append("   AND S.UNIT_MODEL = '"+unitModel+"' ");
		sql.append("   AND NVL(S.PACK_VOLUME_MONTH, 0) <> NVL(P.PACKING_VOLUME,0) ");
		sql.append("ORDER BY TO_DATE(S.PROD_MONTH,'Mon-YY') ");
		
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
				for(int col=1;col<=totalSelectColum; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	public List<String> getLastMonthOfWorksheetExistInPams(Connection conn, String pamsKompoFlag, String version, String getsudoMonth, String timing, String vehiclePlant,
			String vehicleModel, String unitPlant, String unitModel, String userId, String appId) throws CommonErrorException{
		StringBuilder sql = new StringBuilder();
		
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			sql.append("SELECT TO_CHAR(MAX_MONTH,'Mon-YY') AS MAX_MONTH ");
			sql.append("  FROM (SELECT (CASE ");
			sql.append("                WHEN MAX(K.MULTI_SOURCE_FLAG) = 'Y' THEN ");
			sql.append("                	MAX(TO_DATE(MS.VOLUME_MONTH, 'Mon-YY')) ");
			sql.append("         		ELSE ");
			sql.append("          			MAX(TO_DATE(K.VOLUME_MONTH, 'Mon-YY')) ");
			sql.append("       			END) AS MAX_MONTH ");
			sql.append("          FROM TB_R_KAIKIENG_D K ");
			sql.append("     LEFT JOIN TB_R_WS_MULTI_SOURCE_UNITS MS ");
			sql.append("            ON K.GETSUDO_MONTH = MS.GETSUDO_MONTH ");
			sql.append("           AND K.TIMING = MS.TIMING ");
			sql.append("           AND K.VEHICLE_PLANT = MS.VEHICLE_PLANT ");
			sql.append("           AND K.VEHICLE_MODEL = MS.VEHICLE_MODEL ");
			sql.append("           AND K.UNIT_MODEL = MS.UNIT_MODEL ");
			sql.append("   		   AND K.VOLUME_MONTH = MS.VOLUME_MONTH ");
			sql.append("   		   AND MS.UNIT_PLANT = '"+unitPlant+"' ");
			sql.append("   		   AND MS.UNIT_VOLUME IS NOT NULL  ");
			sql.append("         WHERE K.VERSION = '"+version+"' ");
			sql.append("           AND K.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("           AND K.TIMING = '"+timing+"' ");
			sql.append("           AND K.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND K.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("           AND K.UNIT_MODEL = '"+unitModel+"' ");
			sql.append("           AND K.UNIT_VOLUME IS NOT NULL) T ");
			sql.append(" WHERE NOT EXISTS (SELECT 'X' ");
			sql.append("          FROM TB_S_PAMS_RUNDOWN S ");
			sql.append("         WHERE TO_CHAR(S.PROD_DT, 'Mon-YY') = TO_CHAR(T.MAX_MONTH,'Mon-YY') ");
			sql.append("           AND S.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("           AND S.TIMING = '"+timing+"' ");
			sql.append("           AND S.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND S.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("           AND S.CREATE_BY = '"+userId+"' ");
			sql.append("           AND S.APL_ID = '"+appId+"' ) ");
		}else{ //KOMPO
			sql.append("SELECT TO_CHAR(MAX_MONTH,'Mon-YY') AS MAX_MONTH ");
			sql.append("  FROM (SELECT (CASE ");
			sql.append("                WHEN MAX(TO_DATE(MS.VOLUME_MONTH, 'Mon-YY')) > MAX(TO_DATE(K.VOLUME_MONTH, 'Mon-YY')) THEN  ");
			sql.append("                	MAX(TO_DATE(MS.VOLUME_MONTH, 'Mon-YY')) ");
			sql.append("         		ELSE ");
			sql.append("          			MAX(TO_DATE(K.VOLUME_MONTH, 'Mon-YY')) ");
			sql.append("       			END) AS MAX_MONTH ");
			sql.append("          FROM TB_R_KAIKIENG_D K ");
			sql.append("     LEFT JOIN TB_R_WS_MULTI_SOURCE_UNITS MS ");
			sql.append("            ON K.GETSUDO_MONTH = MS.GETSUDO_MONTH ");
			sql.append("           AND K.TIMING = MS.TIMING ");
			sql.append("           AND K.VEHICLE_PLANT = MS.VEHICLE_PLANT ");
			sql.append("           AND K.VEHICLE_MODEL = MS.VEHICLE_MODEL ");
			sql.append("           AND K.UNIT_MODEL = MS.UNIT_MODEL ");
			sql.append("   		   AND K.VOLUME_MONTH = MS.VOLUME_MONTH ");
			sql.append("   		   AND MS.UNIT_VOLUME IS NOT NULL  ");
			sql.append("         WHERE K.VERSION = '"+version+"' ");
			sql.append("           AND K.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("           AND K.TIMING = '"+timing+"' ");
			sql.append("           AND K.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND K.UNIT_VOLUME IS NOT NULL ");
			sql.append("           AND K.VEHICLE_MODEL = '"+vehicleModel+"') T ");
			sql.append(" WHERE NOT EXISTS (SELECT 'X' ");
			sql.append("          FROM TB_S_PAMS_RUNDOWN S ");
			sql.append("         WHERE TO_CHAR(S.PROD_DT, 'Mon-YY') = TO_CHAR(T.MAX_MONTH,'Mon-YY') ");
			sql.append("           AND S.GETSUDO_MONTH = '"+getsudoMonth+"' ");
			sql.append("           AND S.TIMING = '"+timing+"' ");
			sql.append("           AND S.VEHICLE_PLANT = '"+vehiclePlant+"' ");
			sql.append("           AND S.VEHICLE_MODEL = '"+vehicleModel+"' ");
			sql.append("           AND S.CREATE_BY = '"+userId+"' ");
			sql.append("           AND S.APL_ID = '"+appId+"' ) ");
		}
		
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
		List<String> ls = new ArrayList<>();
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
				ls.add(rs.getString("MAX_MONTH"));
			}
			return ls;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	public int insertLogDetail(Object[] data,String userId, String type) throws CommonErrorException {
		int insertedCnt = 0;
		boolean completed = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {

			StringBuilder sql = new StringBuilder();
			
			sql.append("INSERT INTO TB_L_UPLOAD_DETAIL ");
			sql.append("  (GETSUDO_MONTH, "); //param 1
			sql.append("   TIMING, "); //param 2
			sql.append("   VEHICLE_PLANT, "); //param 3
			sql.append("   VEHICLE_MODEL, "); //param 4
			sql.append("   UNIT_PLANT, "); //param 5
			sql.append("   UNIT_MODEL, "); //param 6
			sql.append("   ERROR_SHEET, "); //param 7
			sql.append("   RUNNING_NO, "); 
			sql.append("   ERROR_DATE, "); //param 8
			sql.append("   ERROR_MONTH, "); //param 9
			sql.append("   ERROR_RUNDOWN, "); //param 10
			sql.append("   ERROR_CALENDAR, "); //param 11
			sql.append("   ERROR_WORKSHEET, ");// param 12
			sql.append("   ERROR_STOCK_MIN, "); //param 13
			sql.append("   ERROR_STOCK_MAX, "); //param 14
			sql.append("   CREATE_BY, "); 
			sql.append("   CREATE_DT, "); 
			sql.append("   UPDATE_BY, "); 
			sql.append("   UPDATE_DT) ");
			sql.append("VALUES ");
			
			sql.append("  (?, ?, ?, ?, ?, ?, ?, (SELECT NVL(MAX(RUNNING_NO),1)+1 FROM TB_L_UPLOAD_DETAIL), "); //Key
			if(AppConstants.LOG_DETAIL_CALENDAR.equals(type)){
				sql.append("   to_char(?,'DD-Mon-YY'), NULL, ");
				sql.append("   (SELECT T.VALUE FROM TB_M_SYSTEM T ");
				sql.append(" 	WHERE T.CATEGORY = 'COMMON' ");
				sql.append("      AND T.SUB_CATEGORY = 'CALENDAR_FLAG' AND T.STATUS = 'Y' "); 
				sql.append("      AND T.CD = DECODE(?, 'F', 'F', 'W')), ");
				sql.append("   ?, NULL, NULL, NULL, ");
			}else if(AppConstants.LOG_DETAIL_STOCK.equals(type)){
				sql.append("   to_char(?,'DD-Mon-YY'), NULL, NULL, NULL, NULL, ?, ?, ");
			}else if(AppConstants.LOG_DETAIL_PROD_VOL.equals(type) || AppConstants.LOG_DETAIL_PACK_VOL.equals(type)){
				sql.append("   NULL, ?, ?, NULL, ?, NULL, NULL, ");
			}else if(AppConstants.LOG_DETAIL_OTHER.equals(type)){
				//Do nothing
			}else{
				//Default
				sql.append("  (?, ?, ?, ?, ?, ?, ?, ");
				sql.append("   (SELECT NVL(MAX(RUNNING_NO),1)+1 FROM TB_L_UPLOAD_DETAIL), ");
				sql.append("   to_char(?,'DD-Mon-YY'), ?, ?, ?, ?, ?, ?, ");
			}
			sql.append("   ?, ?, ?, ?) "); //Create/Update

			
			SessionImpl session = (SessionImpl) (em.getDelegate());
			
			conn = session.getJdbcConnectionAccess().obtainConnection();
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(sql.toString());
			int index = 1;
			ps.setObject(index++, data[IDX_L_GETSUDO_MONTH]);
			ps.setObject(index++, data[IDX_L_TIMING]);
			ps.setObject(index++, data[IDX_L_VEHICLE_PLANT]);
			ps.setObject(index++, data[IDX_L_VEHICLE_MODEL]);
			ps.setObject(index++, data[IDX_L_UNIT_PLANT]);
			ps.setObject(index++, data[IDX_L_UNIT_MODEL]);
			ps.setObject(index++, data[IDX_L_ERROR_SHEET]);
			
			if(AppConstants.LOG_DETAIL_CALENDAR.equals(type)){
				ps.setDate(index++, FormatUtil.convert((Date)data[IDX_L_ERROR_DATE]));
				ps.setObject(index++, data[IDX_L_ERROR_RUNDOWN]);
				ps.setObject(index++, data[IDX_L_ERROR_CALENDAR]);
			}else if(AppConstants.LOG_DETAIL_STOCK.equals(type)){
				ps.setDate(index++, FormatUtil.convert((Date)data[IDX_L_ERROR_DATE]));
				ps.setObject(index++, data[IDX_L_ERROR_STOCK_MIN]);
				ps.setObject(index++, data[IDX_L_ERROR_STOCK_MAX]);
			}else if(AppConstants.LOG_DETAIL_PROD_VOL.equals(type) || AppConstants.LOG_DETAIL_PACK_VOL.equals(type)){
				ps.setObject(index++, data[IDX_L_ERROR_MONTH]);
				ps.setObject(index++, data[IDX_L_ERROR_RUNDOWN]);
				ps.setObject(index++, data[IDX_L_ERROR_WORKSHEET]);
			}else if(AppConstants.LOG_DETAIL_STOCK.equals(type)){
				//Do nothing
			}else{
				ps.setDate(index++, FormatUtil.convert((Date)data[IDX_L_ERROR_DATE]));
				ps.setObject(index++, data[IDX_L_ERROR_MONTH]);
				ps.setObject(index++, data[IDX_L_ERROR_RUNDOWN]);
				ps.setObject(index++, data[IDX_L_ERROR_CALENDAR]);
				ps.setObject(index++, data[IDX_L_ERROR_WORKSHEET]);
				ps.setObject(index++, data[IDX_L_ERROR_STOCK_MIN]);
				ps.setObject(index++, data[IDX_L_ERROR_STOCK_MAX]);
			}
			
			ps.setObject(index++, userId);
			ps.setDate(index++, FormatUtil.convert(this.sysdate));
			ps.setObject(index++, userId);
			ps.setDate(index++, FormatUtil.convert(this.sysdate));

			
			insertedCnt = ps.executeUpdate();

			completed = true;
			return insertedCnt;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (completed) {
						conn.commit();
					} else {
						conn.rollback();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public Object[] insertAndCalculateDataToTarget(Connection conn, String userId, 
			String appId, String beginMonth, String endMonth, 
			String getsudoMonth, String timing,
			int statusOfValidate, String[] paramDel,String[] paramGetUpdate) throws Exception{
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		int insertCnt = 0;
		boolean warning = false;
		int idx;
		try {
			
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);	
			
			String version = paramDel[0];
			String vehiclePlant = paramDel[3];
			String vehicleModel = paramDel[4];
			String pamsKompoFlag = paramGetUpdate[1];
			
			ps = conn.prepareStatement(this.getDeletePamsRundownSQL());
			idx = 1;
			for(String param : paramDel){
				ps.setString(idx++, param);
			}
			ps.executeUpdate();
			
			if(ps!=null){
				ps.close();
				ps = null;
			}
			
			String insSQL = this.getInsertPamsRundownSQL(paramGetUpdate, userId, version, getsudoMonth, timing, vehiclePlant, vehicleModel, appId);
			
			ps = conn.prepareStatement(insSQL);
			ps.setDate(1, FormatUtil.convert(this.sysdate));
			ps.setDate(2, FormatUtil.convert(this.sysdate));
			
			insertCnt = ps.executeUpdate();
			
			String statusUpload = this.commonRepository.getStatusOfLogUpload(appId, null);
			if(AppConstants.STATUS_INTERRUPT.equalsIgnoreCase(statusUpload)){
				completed = false;
				String arg1 = "update";
				String arg2 = "this operation was interupted by user";
				throw new CommonErrorException(MessagesConstants.B_ERROR_CONCURRENTCY_INTERRUPT, new String[]{arg1,arg2 }, AppConstants.ERROR);
			}else if(Strings.isNullOrEmpty(statusUpload)){
				completed = false;
				throw new CommonErrorException(MessagesConstants.B_ERROR_CONCURRENTCY, new String[]{}, AppConstants.ERROR);
			}
			
			String prvUpdateDt = this.commonRepository.getUpdateDateFromUploadtatusLog(appId, conn);
			String currentUpdatedDate = this.getConcurrencyDate(conn, paramDel, pamsKompoFlag);
			prvUpdateDt = Strings.nullToEmpty(prvUpdateDt);
			currentUpdatedDate = Strings.nullToEmpty(currentUpdatedDate);
			if(!prvUpdateDt.equals(currentUpdatedDate)){
				completed = false;
				throw new CommonErrorException(MessagesConstants.B_ERROR_CONCURRENTCY, new String[]{}, AppConstants.ERROR);
			}else{
				completed = true;
			}	
		
			
		}catch (Exception e) {
			completed = false;
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
						insertCnt = 0;
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
		resultMap.put("INSERT_TB_R_PAMS_RUNDOWN", Integer.toString(insertCnt));
		if(completed){
			return new Object[]{warning?CST30000Constants.WARNING:CST30000Constants.SUCCESS, resultMap};
		}else{
			return new Object[]{CST30000Constants.ERROR, resultMap};
		}
	}

	@Override
	public String getDeletePamsRundownSQL(){
		StringBuilder delSQL = new StringBuilder();
		delSQL.append(" DELETE FROM TB_R_PAMS_RUNDOWN T ");
		delSQL.append("  WHERE  T.VERSION = ? ");
		delSQL.append("     AND T.GETSUDO_MONTH = ? ");
		delSQL.append("     AND T.TIMING = ? ");
		delSQL.append("     AND T.VEHICLE_PLANT = ? ");
		delSQL.append("     AND T.VEHICLE_MODEL = ? ");
		delSQL.append("     AND T.UNIT_PLANT = ? ");
		delSQL.append("     AND T.UNIT_MODEL = ? ");
		
		return delSQL.toString();
	}
	
	@Override
	public String getInsertPamsRundownSQL(String[] paramGetUpdate, String userId, 
			String version, String getsudoMonth, 
			String timing, String vehiclePlant, 
			String vehicleModel,String appId){
		StringBuilder insHeader = new StringBuilder();

		insHeader.append("INSERT INTO TB_R_PAMS_RUNDOWN ");
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
		insHeader.append("   VARIATION, ");
		insHeader.append("   SS_NO, ");
		insHeader.append("   ID_LINE, ");
		insHeader.append("   EXPORTER, ");
		insHeader.append("   ORDER_DT, ");
		insHeader.append("   PROD_DT, ");
		insHeader.append("   PROD_VOLUME, ");
		insHeader.append("   LOCAL_STOCK, ");
		insHeader.append("   STOCK_DAYS, ");
		insHeader.append("   UNLOAD, ");
		insHeader.append("   TRANSIT, ");
		insHeader.append("   LOADING, ");
		insHeader.append("   PORT_STOCK, ");
		insHeader.append("   PACK_VOLUME, ");
		insHeader.append("   LOT_SIZE, ");
		insHeader.append("   NO_OF_LOT, ");
		insHeader.append("   TOTAL_STOCK, ");
		insHeader.append("   CREATE_BY, ");
		insHeader.append("   CREATE_DT, ");
		insHeader.append("   UPDATE_BY, ");
		insHeader.append("   UPDATE_DT) ");
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
		insHeader.append("          S.VARIATION, ");
		insHeader.append("          S.SS_NO, ");
		insHeader.append("          S.ID_LINE, ");
		insHeader.append("          S.EXPORTER, ");
		insHeader.append("          S.ORDER_DT, ");
		insHeader.append("          S.PROD_DT, ");
		insHeader.append("          S.PROD_VOLUME, ");
		insHeader.append("          S.LOCAL_STOCK, ");
		insHeader.append("          S.STOCK_DAYS, ");
		insHeader.append("          S.UNLOAD, ");
		insHeader.append("          S.TRANSIT, ");
		insHeader.append("          S.LOADING, ");
		insHeader.append("          S.PORT_STOCK, ");
		insHeader.append("          S.PACK_VOLUME, ");
		insHeader.append("          S.LOT_SIZE, ");
		insHeader.append("          S.NO_OF_LOT, ");
		insHeader.append("          S.TOTAL_STOCK, ");
		insHeader.append("          S.CREATE_BY, ");
		insHeader.append("          ? , ");
		insHeader.append("          S.CREATE_BY, ");
		insHeader.append("          ? ");
		insHeader.append("     FROM TB_S_PAMS_RUNDOWN S ");
		insHeader.append("     WHERE to_date(to_char(S.PROD_DT ,'Mon-YY'),'Mon-YY') >= to_date( '"+getsudoMonth+"' ,'Mon-YY') ");
		insHeader.append("     AND to_date(to_char(S.PROD_DT ,'Mon-YY'),'Mon-YY') <= (SELECT MAX(to_date(t.VOLUME_MONTH,'Mon-YY')) ");
		insHeader.append("     														    FROM TB_R_KAIKIENG_H T ");
		insHeader.append("     														   WHERE T.VERSION = '"+version+"' ");
		insHeader.append("     														     AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		insHeader.append("     														     AND T.TIMING = '"+timing+"' ");
		insHeader.append("     														     AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		insHeader.append("     														     AND T.VEHICLE_MODEL = '"+vehicleModel+"' ) ");
		insHeader.append("     AND S.CREATE_BY = '"+userId+"' ");
		insHeader.append(") ");
		
		return insHeader.toString();
	}
	
	
	@Override
	public int getConcurrencyPamsRundown(Connection conn, String[] paramGetUpdate, String appId) throws Exception{
		//Concurrency check
		StringBuilder concurrentSQL = new StringBuilder();
		concurrentSQL.append("SELECT COUNT(1) CRES ");
		concurrentSQL.append("  FROM (SELECT NVL2(L.GETSUDO_MONTH, 'YES', 'NO') L_EXIST, ");
		concurrentSQL.append("               L.TRANS_UPDATE_DT, ");
		concurrentSQL.append("               NVL2(R.GETSUDO_MONTH, 'YES', 'NO') RD_EXIST, ");
		concurrentSQL.append("               R.UPDATE_DT ");
		concurrentSQL.append("          FROM (SELECT '"+paramGetUpdate[2]+"' GETSUDO_MONTH, ");
		concurrentSQL.append("                       '"+paramGetUpdate[3]+"' TIMING, ");
		concurrentSQL.append("                       '"+paramGetUpdate[4]+"' VEHICLE_PLANT, ");
		concurrentSQL.append("                       '"+paramGetUpdate[5]+"' VEHICLE_MODEL, ");
		concurrentSQL.append("                       '"+paramGetUpdate[6]+"' UNIT_PLANT, ");
		concurrentSQL.append("                       '"+paramGetUpdate[7]+"' UNIT_TYPE, ");
		concurrentSQL.append("                       '"+paramGetUpdate[8]+"' UNIT_MODEL, ");
		concurrentSQL.append("                       '"+paramGetUpdate[1]+"' UPLOAD_TYPE ");
		concurrentSQL.append("                  FROM DUAL) T ");
		concurrentSQL.append("          LEFT JOIN (SELECT * ");
		concurrentSQL.append("                      FROM TB_R_RUNDOWN_KOMPO_STS ");
		concurrentSQL.append("                     WHERE VERSION = '"+paramGetUpdate[0]+"' ");
		concurrentSQL.append("                       AND UPLOAD_TYPE = '"+paramGetUpdate[1]+"' ");
		concurrentSQL.append("                       AND UPLOAD_STS <> 'ER') R ");
		concurrentSQL.append("            ON T.GETSUDO_MONTH = R.GETSUDO_MONTH ");
		concurrentSQL.append("           AND T.TIMING = R.TIMING ");
		concurrentSQL.append("           AND T.VEHICLE_PLANT = R.VEHICLE_PLANT ");
		concurrentSQL.append("           AND T.VEHICLE_MODEL = R.VEHICLE_MODEL ");
		concurrentSQL.append("           AND T.UNIT_PLANT = R.UNIT_PLANT ");
		concurrentSQL.append("           AND T.UNIT_MODEL = R.UNIT_MODEL ");
		concurrentSQL.append("          LEFT JOIN TB_L_UPLOAD_STS L ");
		concurrentSQL.append("            ON (CASE ");
		concurrentSQL.append("                 WHEN T.UPLOAD_TYPE = '"+AppConstants.UPLOAD_PAMS_FLAG+"' THEN ");
		concurrentSQL.append("                  '"+AppConstants.UPLOAD_TYPE_RUNDOWN+"' ");
		concurrentSQL.append("                 WHEN T.UPLOAD_TYPE = '"+AppConstants.UPLOAD_KOMPO_FLAG+"' THEN ");
		concurrentSQL.append("                  '"+AppConstants.UPLOAD_TYPE_KOMPOKUNG+"' ");
		concurrentSQL.append("                 ELSE ");
		concurrentSQL.append("                  '' ");
		concurrentSQL.append("               END) = L.UPLOAD_TYPE ");
		concurrentSQL.append("           AND T.GETSUDO_MONTH = L.GETSUDO_MONTH ");
		concurrentSQL.append("           AND T.TIMING = L.TIMING ");
		concurrentSQL.append("           AND T.VEHICLE_PLANT = L.VEHICLE_PLANT ");
		concurrentSQL.append("           AND T.VEHICLE_MODEL = L.VEHICLE_MODEL ");
		concurrentSQL.append("           AND T.UNIT_PLANT = L.UNIT_PLANT ");
		concurrentSQL.append("           AND T.UNIT_MODEL = L.UNIT_MODEL ");
		concurrentSQL.append("           AND L.APL_ID = '"+appId+"') A ");
		concurrentSQL.append(" WHERE (CASE ");
		concurrentSQL.append("         WHEN A.L_EXIST = 'YES' AND A.RD_EXIST = 'NO' AND ");
		concurrentSQL.append("              A.TRANS_UPDATE_DT IS NULL THEN ");
		concurrentSQL.append("          'OK' ");
		concurrentSQL.append("         WHEN A.L_EXIST = 'YES' AND A.RD_EXIST = 'YES' AND ");
		concurrentSQL.append("              A.UPDATE_DT = A.TRANS_UPDATE_DT THEN ");
		concurrentSQL.append("          'OK' ");
		concurrentSQL.append("         ELSE ");
		concurrentSQL.append("          'ERROR' ");
		concurrentSQL.append("       END) = 'OK' "); //Count >= 1 : OK, Count = 0 : Error concurrency
			
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
        try{
			if(conn==null){
	    		SessionImpl session = (SessionImpl)(em.getDelegate());
	    		conn = session.getJdbcConnectionAccess().obtainConnection();
	    	}else{
	    		closeConnection = false;
	    	}
			
			logger.debug(concurrentSQL.toString());
			pp = conn.prepareStatement(concurrentSQL.toString());
			rs = pp.executeQuery();
			int res = 0;
			while(rs.next()){
				res = rs.getBigDecimal("CRES").intValue();
				
			}
			return res;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	public String getConcurrencyDate(Connection conn, String[] params, String pamsKompoFlag) throws Exception{
		//Concurrency check
		String currentUpdatedDate = "";
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT MAX(UPDATE_DT) AS UPDATE_DT ");
		sql.append(" FROM   TB_R_RUNDOWN_KOMPO_STS H ");
		sql.append("  WHERE  H.VERSION = '"+params[0]+"' ");
		sql.append("     AND H.GETSUDO_MONTH = '"+params[1]+"' ");
		sql.append("     AND H.TIMING = '"+params[2]+"' ");
		sql.append("     AND H.VEHICLE_PLANT = '"+params[3]+"' ");
		sql.append("     AND H.VEHICLE_MODEL = '"+params[4]+"' ");
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
			sql.append("     AND H.UNIT_PLANT = '"+params[5]+"' ");
			sql.append("     AND H.UNIT_MODEL = '"+params[6]+"' ");
		}
		sql.append("     AND H.UPLOAD_STS <> 'ER' ");
			
		boolean closeConnection = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
        try{
			if(conn==null){
	    		SessionImpl session = (SessionImpl)(em.getDelegate());
	    		conn = session.getJdbcConnectionAccess().obtainConnection();
	    	}else{
	    		closeConnection = false;
	    	}
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				currentUpdatedDate = String.valueOf(rs.getTimestamp("UPDATE_DT").getTime());
				
			}
			return currentUpdatedDate;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
		} finally {
			try{
				if(conn!=null && !conn.isClosed()){
					if (rs !=null) {
						rs.close();					
			            rs = null;
			        }
					
					if (ps !=null) {
			            ps.close();
			            ps = null;
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
	public List<Object[]> getKaikiengUnitVolumeCheck(Connection conn, String version, String getsudoMonth, String endMonth, String timing, 	
												String vehiclePlant, String vehicleModel, 
												String unitPlant, String unitModel, String userId, String pamsKompoFlag)  throws CommonErrorException{
		int totalSelectColum = 7;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.VERSION, ");
		sql.append("       T.GETSUDO_MONTH, ");
		sql.append("       T.TIMING, ");
		sql.append("       T.VEHICLE_PLANT, ");
		sql.append("       T.VEHICLE_MODEL, ");
		sql.append("       T.VOLUME_MONTH, ");
		sql.append("       T.UNIT_VOLUME ");
		sql.append("  FROM TB_R_KAIKIENG_D T, TB_M_VEHICLE_UNIT_RELATION M ");
		sql.append(" WHERE M.VEHICLE_PLANT = T.VEHICLE_PLANT ");
		sql.append("   AND M.VEHICLE_MODEL = T.VEHICLE_MODEL ");
		sql.append("   AND M.UNIT_MODEL = T.UNIT_MODEL ");
		sql.append("   AND TRUNC(M.TC_FROM, 'MONTH') <= TO_DATE('"+endMonth+"', 'Mon-YY') ");
		sql.append("   AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) >= TO_DATE('"+getsudoMonth+"', 'Mon-YY') ");
		sql.append("   AND TO_DATE(T.VOLUME_MONTH, 'Mon-YY') BETWEEN M.TC_FROM AND NVL(LAST_DAY(M.TC_TO), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) ");
		sql.append("   AND T.VERSION = '"+version+"' ");
		sql.append("   AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		sql.append("   AND T.TIMING = '"+timing+"' ");
		sql.append("   AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		sql.append("   AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
		sql.append("   AND M.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		sql.append("   AND M.VEHICLE_MODEL = '"+vehicleModel+"' ");
		sql.append("   AND M.UNIT_PLANT = '"+unitPlant+"' ");
		sql.append("   AND M.UNIT_MODEL = '"+unitModel+"' ");
		sql.append("   AND T.MULTI_SOURCE_FLAG = 'N' "); //Check case non multi-source only 
		sql.append("   AND EXISTS (SELECT 'x' ");
		sql.append("          FROM TB_S_PAMS_RUNDOWN P ");
		sql.append("         WHERE P.GETSUDO_MONTH = T.GETSUDO_MONTH ");
		sql.append("           AND P.TIMING = T.TIMING ");
		sql.append("           AND P.VEHICLE_PLANT = M.VEHICLE_PLANT ");
		sql.append("           AND P.VEHICLE_MODEL = M.VEHICLE_MODEL ");
		sql.append("           AND P.UNIT_PLANT = M.UNIT_PLANT ");
		sql.append("           AND P.UNIT_MODEL = M.UNIT_MODEL ");
		sql.append("           AND TO_CHAR(P.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH ");
		sql.append("           AND P.CREATE_BY = '"+userId+"') ");
		sql.append("ORDER BY TO_DATE(T.VOLUME_MONTH,'Mon-YY') ");
		
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
				for(int col=1;col<=totalSelectColum; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
	public List<Object[]> getPackingVolumeCheck(Connection conn, String version, String getsudoMonth, String endMonth, String timing, 	
												String vehiclePlant, String vehicleModel, 
												String unitPlant, String unitModel, String userId, String pamsKompoFlag)  throws CommonErrorException{
		int totalSelectColum = 2;
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT T.VOLUME_MONTH, T.PACKING_VOLUME ");
		sql.append("  FROM TB_R_WS_PACKING_MOVEMENT T ");
		sql.append(" WHERE T.VERSION = '"+version+"' ");
		sql.append("   AND T.GETSUDO_MONTH = '"+getsudoMonth+"' ");
		sql.append("   AND T.TIMING = '"+timing+"' ");
		sql.append("   AND T.VEHICLE_PLANT = '"+vehiclePlant+"' ");
		sql.append("   AND T.VEHICLE_MODEL = '"+vehicleModel+"' ");
		sql.append("   AND T.UNIT_PLANT = '"+unitPlant+"' ");
		sql.append("   AND T.UNIT_MODEL = '"+unitModel+"' ");
		sql.append("   AND EXISTS (SELECT 'x' ");
		sql.append("          FROM TB_S_PAMS_RUNDOWN P ");
		sql.append("         WHERE P.GETSUDO_MONTH = T.GETSUDO_MONTH ");
		sql.append("           AND P.TIMING = T.TIMING ");
		sql.append("           AND P.VEHICLE_PLANT = T.VEHICLE_PLANT ");
		sql.append("           AND P.VEHICLE_MODEL = T.VEHICLE_MODEL ");
		sql.append("           AND P.UNIT_PLANT = T.UNIT_PLANT ");
		sql.append("           AND P.UNIT_MODEL = T.UNIT_MODEL ");
		sql.append("           AND TO_CHAR(P.PROD_DT, 'Mon-YY') = T.VOLUME_MONTH ");
		sql.append("           AND P.CREATE_BY = '"+userId+"') ");
		sql.append("ORDER BY TO_DATE(T.VOLUME_MONTH,'Mon-YY') ");
		
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
				for(int col=1;col<=totalSelectColum; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
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
}