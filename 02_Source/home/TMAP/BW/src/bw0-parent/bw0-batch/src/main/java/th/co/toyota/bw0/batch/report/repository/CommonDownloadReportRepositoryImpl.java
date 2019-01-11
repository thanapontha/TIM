/******************************************************
 * Program History
 * 
 * Project Name	            :  
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.web.report.main
 * Program ID 	            :  CommonDownloadReportRepositoryImpl.java
 * Program Description	    :  Common Download Report Repository Implement
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  January 11, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2017-Toyota Daihatsu Engineering & Manufacturing Co., Ltd. All Rights Reserved.    
 ********************************************************/
package th.co.toyota.bw0.batch.report.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonUtility;

@Repository
public class CommonDownloadReportRepositoryImpl implements CommonDownloadReportRepository {
	final Logger logger = LoggerFactory.getLogger(CommonDownloadReportRepositoryImpl.class);
	
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	
	@Override
	public List<Object[]> searchObject(Connection conn, String getsudoMonth, String timing, String plantCondition) throws Exception {
		
		StringBuilder SQL = new StringBuilder();
		SQL.append("SELECT CAL.CAL_DATE, T2.* ");
		SQL.append("  FROM (SELECT T.PLANT, TO_CHAR(T.CALENDAR_DATE,'DD-Mon-YYYY') CALENDAR_DATE, T.CALENDAR_FLAG ");
		SQL.append("          FROM TB_M_CALENDAR T ");
		SQL.append("         WHERE T.GETSUDO_MONTH = '" + getsudoMonth + "' ");
		SQL.append("           AND T.TIMING = '" + timing + "' ");
		SQL.append("           AND T.PLANT IN (" + plantCondition + ") ");
		SQL.append("           AND T.CALENDAR_DATE BETWEEN TRUNC(TO_DATE('" + getsudoMonth + "', 'Mon-YY'),'yyyy') AND ");
		SQL.append("               TO_DATE('3112' || to_char(ADD_MONTHS(TRUNC(TO_DATE('" + getsudoMonth + "', 'Mon-YY'),'yyyy'), 12), 'YYYY'), 'DDMMYYYY')) ");
		SQL.append("PIVOT(MAX(CALENDAR_FLAG) ");
		SQL.append("   FOR PLANT IN(" + plantCondition + ")) T2 ");
		SQL.append("RIGHT JOIN (SELECT TO_CHAR(TO_DATE('0101' || ");
		SQL.append("							  TO_CHAR(TO_DATE('" + getsudoMonth + "', 'Mon-YY'), ");
		SQL.append("									  'YYYY'), ");
		SQL.append("							  'DDMMYYYY') + LEVEL - 1, ");
		SQL.append("					  'DD-Mon-YYYY') CAL_DATE ");
		SQL.append("		 FROM DUAL ");
		SQL.append("	   CONNECT BY LEVEL <= ");
		SQL.append("				  (ADD_MONTHS(TRUNC(TO_DATE('" + getsudoMonth + "', 'Mon-YY'), ");
		SQL.append("									'year'), ");
		SQL.append("							  24) - ");
		SQL.append("				  TRUNC(TO_DATE('" + getsudoMonth + "', 'Mon-YY'), 'year'))) CAL ");
		SQL.append("	ON CAL.CAL_DATE = T2.CALENDAR_DATE ");
		SQL.append(" ORDER BY TO_DATE(CAL.CAL_DATE,'DD-Mon-YYYY') ");
		
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
		List<Object[]> ls = new ArrayList<Object[]>();
        try{
			if(conn==null){
	    		SessionImpl session = (SessionImpl)(em.getDelegate());
	    		conn = session.getJdbcConnectionAccess().obtainConnection();
	    	}else{
	    		closeConnection = false;
	    	}
			
			String[] lenCr = plantCondition.split(",");
			
			int totalSelectCol = 2+lenCr.length;
			
			pp = conn.prepareStatement(SQL.toString());
			rs = pp.executeQuery();
			while(rs.next()){
				List<Object> obj = new ArrayList<Object>();
				for(int col=1;col<=totalSelectCol; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
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

