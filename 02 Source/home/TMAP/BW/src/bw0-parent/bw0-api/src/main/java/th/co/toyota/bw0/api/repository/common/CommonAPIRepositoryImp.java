/******************************************************
 * Program History
 *
 * Project Name	            :  GWRDS :
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.bw0.repository.common
 * Program ID 	            :  CBW00000Repository.java
 * Program Description	    :  Common Repository
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  September 08, 2016
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.
 ********************************************************/
package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;

@Repository
public class CommonAPIRepositoryImp implements CommonAPIRepository {
	final Logger logger = LoggerFactory.getLogger(CommonAPIRepositoryImp.class);

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	@Autowired
	private CST32010DocNoGenerator docNoGenerator;

	@Autowired
	private SystemMasterRepository systemMasterRepository;
	
	@Override
	public String genAppId() throws CommonErrorException {
		try {
			return docNoGenerator.generateDocNo(AppConstants.SEQ_CODE_APP_ID, FormatUtil.currentTimestampToOracleDB());
		} catch (Exception e) {
			throw new CommonErrorException(e.getMessage(), AppConstants.ERROR);
		}
	}
	
	@Override
	public Connection getConnection(){
		try{
			SessionImpl session = (SessionImpl) (em.getDelegate());
			return session.getJdbcConnectionAccess().obtainConnection();
		} catch (SQLException e) {
			return null;
		}
	}
	
	@Override
	public Map<String,Object> getTableMeataData(String tableName) throws Exception {
		String sqlCmd = "SELECT * FROM " + tableName + " WHERE 0 = 1";
		
		Connection conn = null;
		PreparedStatement pp = null;
		ResultSet rs = null;
		
		Map<String,Object> hashMetadata = new HashMap<>();
    	ArrayList<String> alPK = new ArrayList<>();
    	String[] arrPK = null;
    	int columnNum = 0;
		try {
			SessionImpl session = (SessionImpl)(em.getDelegate());
        	conn = session.getJdbcConnectionAccess().obtainConnection();	
			
			pp = conn.prepareStatement(sqlCmd);
			rs = pp.executeQuery();

	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	DatabaseMetaData dbmd = conn.getMetaData();

	    	rs.close();
	    	rs = null;
	    	
	    	rs = dbmd.getPrimaryKeys(null, null, tableName);
	    	while(rs.next()){
	    		alPK.add(rs.getString("COLUMN_NAME"));
	    	}

	    	Object objPK = alPK.toArray();
	    	if(objPK instanceof String){
	    		arrPK = new String[1];
	    		arrPK[0] = objPK.toString();
	    	}else if(objPK instanceof Object[]){
	    		Object[] arrObjPK = (Object[])objPK;
	    		arrPK = new String[arrObjPK.length];
	    		for(int i = 0; i < arrObjPK.length; i++){
	    			arrPK[i] = (String)arrObjPK[i];
	    		}
	    	}else if(objPK instanceof String[]){
	    		arrPK = (String[])objPK;
	    	}
	    	
	    	columnNum = rsmd.getColumnCount();
	    	for(int i = 0; i < columnNum; i++){
	    		HashMap<String,Object> colInfo = new HashMap<>();
	    		colInfo.put("TYPE", rsmd.getColumnTypeName(i+1));
	    		colInfo.put("PRECISION", Integer.valueOf(rsmd.getPrecision(i+1)));
	    		colInfo.put("SCALE", Integer.valueOf(rsmd.getScale(i+1)));
	    		colInfo.put("LENGTH", Integer.valueOf(rsmd.getColumnDisplaySize(i+1)));
	    		hashMetadata.put(rsmd.getColumnName(i+1), colInfo);
	    	}

	    	hashMetadata.put("TABLE_NAME", tableName);
	    	hashMetadata.put("PK", arrPK);

		} catch (Exception ex) {
			throw ex;
		} finally {
			if ((conn != null) && !conn.isClosed()) {
				if (rs !=null) {
		            rs.close();
		            rs = null;
		        }
				
				if (pp !=null) {
		            pp.close();
		            pp = null;
		        }
				
				conn.close();
				conn = null;
			}
		}

    	return hashMetadata;
	}
}
