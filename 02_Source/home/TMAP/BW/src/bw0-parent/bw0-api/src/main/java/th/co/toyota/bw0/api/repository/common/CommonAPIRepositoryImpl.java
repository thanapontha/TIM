/******************************************************
 * Program History
 *
 * Project Name	            :  
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.bw0.repository.common
 * Program ID 	            :  CommonAPIRepositoryImpl.java
 * Program Description	    :  Common API Repository
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  January 08, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.
 ********************************************************/
package th.co.toyota.bw0.api.repository.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;

import th.co.toyota.bw0.api.common.CommonSQLAdapter;
import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.common.EmailNotification;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.model.ModuleDetailInfo;
import th.co.toyota.st3.api.model.ModuleDetailInfo_;
import th.co.toyota.st3.api.model.ModuleId_;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;

@Repository
public class CommonAPIRepositoryImpl implements CommonAPIRepository {
	int IDX_L_GETSUDO_MONTH = 0;
	int IDX_L_TIMING = 1;
	int IDX_L_VEHICLE_PLANT = 2;
	int IDX_L_VEHICLE_MODEL = 3;
	int IDX_L_UNIT_PLANT = 4;
	int IDX_L_UNIT_MODEL = 5;
	int IDX_L_ERROR_SHEET = 6;
	int IDX_L_ERROR_DATE = 7;
	int IDX_L_ERROR_MONTH = 8;
	int IDX_L_ERROR_RUNDOWN = 9;
	int IDX_L_ERROR_CALENDAR = 10;
	int IDX_L_ERROR_WORKSHEET = 11;
	int IDX_L_ERROR_STOCK_MIN = 12;
	int IDX_L_ERROR_STOCK_MAX = 13;
	
	final Logger logger = LoggerFactory.getLogger(CommonAPIRepositoryImpl.class);

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	@Autowired
	private CST32010DocNoGenerator docNoGenerator;

	@Override
	public ModuleDetailInfo findModuleDetailInfo(String functionId) throws CommonErrorException{

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ModuleDetailInfo> cq = cb.createQuery(ModuleDetailInfo.class);
		Root<ModuleDetailInfo> root = cq.from(ModuleDetailInfo.class);

		cq.select(root);

		List<Predicate> preds = new ArrayList<Predicate>();

		preds.add(cb.equal(root.get(ModuleDetailInfo_.id).get(ModuleId_.functionId), functionId));

		cq.where(preds.toArray(new Predicate[] {}));

		List<ModuleDetailInfo> objectList = em.createQuery(cq).getResultList();
		if ((objectList != null) && (!objectList.isEmpty())) {
			return objectList.get(0);
		} else {
			String[] params = new String[5];
			params[0] = "Function ID:" + functionId;
			params[1] = "TB_M_MODULE_D";
			params[2] = "";
			params[3] = "";
			params[4] = "";
			throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params, AppConstants.ERROR);
		}
	}

	@Override
	public String genAppId() throws CommonErrorException {
		try {
			String docNo = docNoGenerator.generateDocNo(AppConstants.SEQ_CODE_APP_ID, FormatUtil.currentTimestampToOracleDB());
			return docNo;
		} catch (Exception e) {
			throw new CommonErrorException(e.getMessage(), AppConstants.ERROR);
		}
	}
	
	@Override
	public List<ComboValue> loadCombobox(Connection conn, String tableName, String[] selectField, String criteria, String orderBy)
			throws CommonErrorException {
		return loadComboboxWithAuth(conn, tableName, selectField, criteria, orderBy, null);
	}
	
	public List<ComboValue> loadComboboxWithAuth(Connection conn, String tableName, String[] selectField, String criteria, String orderBy, StringBuilder existConditionAuth)
			throws CommonErrorException {
		List<ComboValue> comboLs = new ArrayList<ComboValue>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean closeConnection = true;
		try {

			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			
			StringBuilder SQL = new StringBuilder();

			SQL.append("SELECT DISTINCT ").append(selectField[0]).append(" AS ST_VALUE, ").append(selectField[1]).append(" AS ST_LABEL ");
			SQL.append(" FROM ").append(tableName).append(" M ");
			SQL.append(" WHERE   1 = 1 ");
			if (!Strings.isNullOrEmpty(criteria)) {
				SQL.append(" AND ").append(criteria);
			}
			if(existConditionAuth!=null && existConditionAuth.toString().length()>0){
				SQL.append(existConditionAuth.toString());
			}
			if (!Strings.isNullOrEmpty(orderBy)) {
				SQL.append(" ORDER BY ").append(orderBy);
			}
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				comboLs.add(new ComboValue(Strings.nullToEmpty(rs.getString("ST_VALUE").trim()), Strings.nullToEmpty(rs.getString("ST_LABEL").trim())));
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if(conn!=null && !conn.isClosed()){
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (ps != null) {
						ps.close();
						ps = null;
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
		return comboLs;
	}

	@Override
	public EmailNotification getEmailNotification(Connection conn, String vehiclePlant, String vehicleModel, String sendToCompany) throws CommonErrorException {
		EmailNotification emn = new EmailNotification();
		emn.setEmailList(new ArrayList<String>());
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean closeConnection = true;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
				
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT VALUE FROM TB_M_SYSTEM ");
			SQL.append(" WHERE CATEGORY = ?  AND upper(SUB_CATEGORY) = upper(?) ");
			SQL.append(" AND ( upper(VALUE) LIKE upper(?) ");
			SQL.append("       OR upper(VALUE) LIKE upper(?) ");
			SQL.append("       OR upper(VALUE) LIKE upper(?) ");
			SQL.append("       OR upper(VALUE) LIKE upper(?) ");
			SQL.append("   ) ");
			SQL.append(" AND STATUS = 'Y' ");
			SQL.append(" ORDER BY UPPER(VALUE) ASC ");

			ps = conn.prepareStatement(SQL.toString());
			ps.setString(1, AppConstants.SYS_CATEGORY_PIC);
			ps.setString(2, sendToCompany);
			ps.setString(3, vehiclePlant + ":" + vehicleModel + ":%");
			ps.setString(4, AppConstants.ALL + ":" + vehicleModel + ":%");
			ps.setString(5, vehiclePlant + ":" + AppConstants.ALL + ":%");
			ps.setString(6, AppConstants.ALL + ":" + AppConstants.ALL + ":%");
			
			HashMap<String, String> emailMap = null;
			
			rs = ps.executeQuery();
			while (rs.next()) {
				String value = rs.getString("VALUE");
				if (!Strings.isNullOrEmpty(value)) {
					if(emailMap == null){
						emailMap = new HashMap<String, String>();
					}
					String[] values = value.split("\\" + AppConstants.COLON);
					if ((values != null) && (values.length >= 4)) {
						String email = Strings.nullToEmpty(values[3]);
						if (emailMap.containsKey(email) == false && email.isEmpty() == false) {
							emn.getEmailList().add(email);
							emailMap.put(email, email);
						}
					}
				}
			}	

			if(emn.getEmailList().isEmpty()){
				String[] params = new String[5];
				params[0] = "PIC email";
				params[1] = "TB_M_SYSTEM";
				params[2] = "CATEGORY="+AppConstants.SYS_CATEGORY_PIC;
				params[3] = "SUB_CATEGORY="+sendToCompany;
				params[4] = "VALUE LIKE ('"+vehiclePlant + ":" + vehicleModel + ":%' OR '"+
							AppConstants.ALL + ":" + vehicleModel + ":%' OR '"+
							vehiclePlant + ":" + AppConstants.ALL + ":%' OR '"+
							AppConstants.ALL + ":" + AppConstants.ALL + ":%'), STATUS=Y";
				throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM
						, params
						, AppConstants.ERROR);
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if(conn!=null && !conn.isClosed()){
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (ps != null) {
						ps.close();
						ps = null;
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

		return emn;
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
	public HashMap<String,Object> getTableMeataData(String tableName) throws Exception {
		String sqlCmd = "SELECT * FROM " + tableName + " WHERE 0 = 1";
		
		Connection conn = null;
		PreparedStatement pp = null;
		ResultSet rs = null;
		
		HashMap<String,Object> hashMetadata = new HashMap<String,Object>();
    	ArrayList<String> alPK = new ArrayList<String>();
    	String[] arrPK = null;
    	int columnNum = 0;
		try {
			SessionImpl session = (SessionImpl)(em.getDelegate());
        	conn = session.getJdbcConnectionAccess().obtainConnection();	
			
			pp = conn.prepareStatement(sqlCmd);
			rs = pp.executeQuery();

	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	DatabaseMetaData dbmd = conn.getMetaData();

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
	    		HashMap<String,Object> colInfo = new HashMap<String,Object>();
	    		colInfo.put("TYPE", rsmd.getColumnTypeName(i+1));
	    		colInfo.put("PRECISION", new Integer(rsmd.getPrecision(i+1)));
	    		colInfo.put("SCALE", new Integer(rsmd.getScale(i+1)));
	    		colInfo.put("LENGTH", new Integer(rsmd.getColumnDisplaySize(i+1)));
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
	
	@Override
	public int getRowPerPage(String screenId) {
		BigDecimal rowsPerPage = new BigDecimal(10);
        try{
        	StringBuilder sql = new StringBuilder();
			sql.append(" SELECT VALUE ");
			sql.append(" FROM TB_M_SYSTEM ");
			sql.append(" WHERE CATEGORY = 'SCREEN' ");
			sql.append(" AND SUB_CATEGORY = 'ROWS_PER_PAGE' ");
			sql.append(" AND CD = '"+screenId+"' ");
			Query query = em.createNativeQuery(sql.toString());
            String result = (String) query.getSingleResult();
            rowsPerPage = new BigDecimal(result);
        }catch(Exception e){
        	rowsPerPage = new BigDecimal(10);
        }
        return rowsPerPage.intValue();
	}
	
	


	@Override
	public HashMap<String, String> getUserInfoForTestOnFTH(String ipAddress) {
		HashMap<String, String> userInfoMap = new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement pp = null;
		ResultSet rs = null;
        try{
        	StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CD, VALUE ");
			sql.append(" FROM TB_M_SYSTEM ");
			sql.append(" WHERE CATEGORY = '"+AppConstants.SYS_CATEGORY_GWRDS+"' ");
			sql.append(" AND SUB_CATEGORY = '"+ipAddress+"' ");
			
			SessionImpl session = (SessionImpl) (em.getDelegate());
			conn = session.getJdbcConnectionAccess().obtainConnection();
			pp = conn.prepareStatement(sql.toString());
			rs = pp.executeQuery();
			while(rs.next()){
				userInfoMap.put(rs.getString("CD"), rs.getString("VALUE"));
			}
        }catch(Exception e){
        	e.printStackTrace();
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
					
					conn.close();
					conn = null;
        		}
        	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        return userInfoMap;
	}
	
	@Override
	public Object executeQuery(Connection conn, String sql, int totalSelectCol) throws Exception {
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
		Object result = null;
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
			if(totalSelectCol==1){				
				while(rs.next()){
					result = rs.getObject(1);
				}
			}else{
				while(rs.next()){
					List<Object> obj = new ArrayList<Object>();
					for(int col=1;col<=totalSelectCol; col++){
						obj.add(rs.getObject(col));
					}
					result = obj.toArray();
				}
			}
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
		return result;
	}
	
	@Override
	public int getTotalActiveRecordSize(Connection conn, StringBuilder sql, List<Object> parameter) throws Exception {
		BigDecimal totalRows = new BigDecimal(0);
		boolean closeConnection = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
        	if(conn==null){
        		SessionImpl session = (SessionImpl)(em.getDelegate());
        		conn = session.getJdbcConnectionAccess().obtainConnection();
        	}else{
        		closeConnection = false;
        	}
        	
			String sqlCount = "SELECT COUNT(1) AS TOTAL_ROW FROM ( " + sql.toString() + " ) ";
			
			ps = conn.prepareStatement(sqlCount);
			if (parameter != null) {
				int index = 1;
				for (Object obj : parameter) {
					ps.setObject(index, obj);
					index++;
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				totalRows = rs.getBigDecimal("TOTAL_ROW");
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (rs !=null) {
						rs.close();					
			            rs = null;
			        }
					
					if (ps != null) {
						ps.close();
						ps = null;
					}
					if(closeConnection){
						conn.close();
						conn = null;
					}
				}
			} catch (Exception e1) {
				logger.error(ExceptionUtils.getStackTrace(e1));
			}
		}
		return totalRows.intValue();
	}
}
