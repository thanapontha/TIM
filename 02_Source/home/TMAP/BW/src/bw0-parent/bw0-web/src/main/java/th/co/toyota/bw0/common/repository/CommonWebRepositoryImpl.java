package th.co.toyota.bw0.common.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;

@Repository
public class CommonWebRepositoryImpl implements CommonWebRepository {
	final Logger logger = LoggerFactory.getLogger(CommonWebRepositoryImpl.class);

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	@Autowired
	private CST32010DocNoGenerator docNoGenerator;
	
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
	public Map<String, String> getUserInfoForTestOnDev(String ipAddress) {
		Map<String, String> userInfoMap = new HashMap<>();
        
    	StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CD, VALUE ");
		sql.append(" FROM TB_M_SYSTEM ");
		sql.append(" WHERE CATEGORY = 'CATEGORY' ");
		sql.append(" AND SUB_CATEGORY = '"+ipAddress+"' ");
		
		try(Connection conn = this.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery();){
			while(rs.next()){
				userInfoMap.put(rs.getString("CD"), rs.getString("VALUE"));
			}
        }catch(Exception e){
        	logger.debug(ExceptionUtils.getStackTrace(e));
        }
        return userInfoMap;
	}
	
	@Override
	public Object executeQuery(String sql, int totalSelectCol) throws CommonErrorException {
		try(Connection conn = this.getConnection()){
			return this.executeQuery(conn, sql, totalSelectCol);
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}
	}
	
	@Override
	public Object executeQuery(Connection conn, String sql, int totalSelectCol) throws CommonErrorException {
		Object result = null;
        try(PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery();){
			logger.debug(sql);
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
		}	
		return result;
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
	public int getTotalActiveRecordSize(Connection conn, StringBuilder sql, List<Object> parameter) throws CommonErrorException {
		BigDecimal totalRows = new BigDecimal(0);
		ResultSet rs = null;
		String sqlCount = "SELECT COUNT(1) AS TOTAL_ROW FROM ( " + sql.toString() + " ) ";
		try(PreparedStatement ps = conn.prepareStatement(sqlCount)) {
			if (parameter != null) {
				int index = 1;
				for (Object obj : parameter) {
					ps.setObject(index++, obj);
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
				if(rs != null) {
					rs.close();
				}
			 }catch(Exception e){
	        	logger.debug(ExceptionUtils.getStackTrace(e));
		     }
		}
		return totalRows.intValue();
	}
	
	@Override
	public List<ComboValue> loadCombobox(Connection conn, String tableName, String[] selectField, String criteria, String orderBy)
			throws CommonErrorException {
		return loadComboboxWithAuth(conn, tableName, selectField, criteria, orderBy, null);
	}

	@Override
	public List<ComboValue> loadComboboxWithAuth(Connection conn, String tableName, String[] selectField, String criteria, String orderBy, StringBuilder existConditionAuth)
			throws CommonErrorException {
		List<ComboValue> comboLs = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ").append(selectField[0]).append(" AS ST_VALUE, ").append(selectField[1]).append(" AS ST_LABEL ");
		sql.append(" FROM ").append(tableName).append(" M ");
		sql.append(" WHERE   1 = 1 ");
		if (!Strings.isNullOrEmpty(criteria)) {
			sql.append(" AND ").append(criteria);
		}
		if(existConditionAuth!=null && existConditionAuth.toString().length()>0){
			sql.append(existConditionAuth.toString());
		}
		if (!Strings.isNullOrEmpty(orderBy)) {
			sql.append(" ORDER BY ").append(orderBy);
		}
			
		try(PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery();){
			while (rs.next()) {
				comboLs.add(new ComboValue(Strings.nullToEmpty(rs.getString("ST_VALUE").trim()), Strings.nullToEmpty(rs.getString("ST_LABEL").trim())));
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}
		return comboLs;
	}
}
