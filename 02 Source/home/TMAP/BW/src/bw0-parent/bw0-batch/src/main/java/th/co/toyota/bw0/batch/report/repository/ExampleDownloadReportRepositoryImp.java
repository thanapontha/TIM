/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : Getsudo Worksheet Rundown System
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.web.report.main
 * Program ID 	            :  CBW04221Repository.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  August 28, 2017
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
public class ExampleDownloadReportRepositoryImp implements ExampleDownloadReportRepository {
	final Logger logger = LoggerFactory.getLogger(ExampleDownloadReportRepositoryImp.class);
	
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	
	@Override
	public List<Object[]> searchObject(Connection conn, String getsudoMonth, String timing, String plantCondition) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("");
		
		boolean closeConnection = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Object[]> ls = new ArrayList<>();
        try{
			if(conn==null){
	    		SessionImpl session = (SessionImpl)(em.getDelegate());
	    		conn = session.getJdbcConnectionAccess().obtainConnection();
	    	}else{
	    		closeConnection = false;
	    	}
			
			String[] lenCr = plantCondition.split(",");
			
			int totalSelectCol = 2+lenCr.length;
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				List<Object> obj = new ArrayList<>();
				for(int col=1;col<=totalSelectCol; col++){
					obj.add(rs.getObject(col));
				}
				ls.add(obj.toArray());
			}
			return ls;
		}catch(Exception e){
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			CommonUtility.closeConnection(conn, rs, ps, closeConnection);
		}
	}
}

