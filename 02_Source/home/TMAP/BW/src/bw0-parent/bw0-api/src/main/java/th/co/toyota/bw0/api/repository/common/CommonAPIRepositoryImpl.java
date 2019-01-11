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
import java.util.Date;
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

import com.google.common.base.Strings;

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

	@Autowired
	private SystemMasterAPIRepository systemMasterRepository;
	
//	@Override
//	public boolean isSeqMasterExist(String seqCode) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<SequenceMaster> cq = cb.createQuery(SequenceMaster.class);
//		Root<SequenceMaster> root = cq.from(SequenceMaster.class);
//		cq.select(root);
//		cq.where(cb.equal(root.get(SequenceMaster_.sequenceCode), seqCode));
//
//		int size = em.createQuery(cq).getResultList().size();
//		boolean isExist = false;
//		if (size > 0) {
//			isExist = true;
//		}
//
//		return isExist;
//	}
//
//	@Override
//	@Transactional
//	public void createSeqMaster(String seqCode, String seqKey, int startValue, String docNoFormat, int startMonth, String userId) {
//		SequenceMaster seq = new SequenceMaster();
//		seq.setSequenceCode(seqCode);
//		seq.setSequenceKey(seqKey);
//		seq.setCurrentValue(0);
//		seq.setStartValue(startValue);
//		seq.setDocNoFormat(docNoFormat);
//		seq.setStartMonth(startMonth);
//		seq.setVersion(0);
//		seq.setCreateBy(userId);
//		seq.setUpdateBy(userId);
//		seq.setCreateDate(FormatUtil.getCurrentDateTime());
//		seq.setUpdateDate(seq.getCreateDate());
//
//		em.persist(seq);
//	}

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
	

	@Override
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
	public List<ComboValue> getTimingComboList(Connection conn) throws CommonErrorException {
		List<ComboValue> comboLs = new ArrayList<ComboValue>();
		
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
			
			StringBuilder SQL = new StringBuilder();

			SQL.append("SELECT DISTINCT VALUE AS ST_VALUE, VALUE AS ST_LABEL, CD ");
			SQL.append(" FROM TB_M_SYSTEM M ");
			SQL.append(" WHERE  CATEGORY='").append(AppConstants.SYS_CATEGORY_MASTER).append("'");
			SQL.append(" AND SUB_CATEGORY = '").append(AppConstants.SYS_SUB_CATEGORY_TIMING).append("'");
			SQL.append(" AND STATUS = '").append(AppConstants.YES_STR).append("'");
			SQL.append(" ORDER BY UPPER(CD) ASC ");
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				comboLs.add(new ComboValue(Strings.nullToEmpty(rs.getString("ST_VALUE")), Strings.nullToEmpty(rs.getString("ST_LABEL"))));
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
	public String getStatusOfLogUpload(String appId, Connection conn) throws CommonErrorException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;
		boolean closeConnection = true;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl) (em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT STATUS ");
			SQL.append(" FROM   TB_L_UPLOAD_STS  ");
			SQL.append(" WHERE  APL_ID = ? ");
			
			ps = conn.prepareStatement(SQL.toString());
			ps.setString(1, appId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("STATUS");
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(ps!=null){
						ps.close();
						ps = null;
					}
					if (closeConnection) {
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}
	
	@Override
	public String getUpdateDateFromUploadtatusLog(String appId, Connection conn) throws CommonErrorException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;
		boolean closeConnection = true;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl) (em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT TRANS_UPDATE_DT ");
			SQL.append(" FROM   TB_L_UPLOAD_STS  ");
			SQL.append(" WHERE  APL_ID = '"+appId+"' ");
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				result = String.valueOf(rs.getTimestamp("TRANS_UPDATE_DT").getTime());
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(ps!=null){
						ps.close();
						ps = null;
					}
					if (closeConnection) {
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public int updateStatusOfLogUpload(String appId, String userId, String status, String message) throws CommonErrorException {
		return updateStatusOfLogUpload(null, appId, userId, status, message, null);
	}
	
	@Override
	public int updateStatusOfLogUpload(Connection conn, String appId, String userId, String status, String message, Timestamp transactionUpdateDt) throws CommonErrorException {
		int updatedCnt = 0;
		boolean completed = false;
		PreparedStatement ps = null;
		boolean closeConnection = true;
		try {			
			Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();

			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE TB_L_UPLOAD_STS   ");
			sql.append(" SET STATUS = ? , MESSAGE = ?, UPDATE_BY= ?, UPDATE_DT = ?  ");
			if(transactionUpdateDt != null){
				sql.append(" , TRANS_UPDATE_DT = ?  ");
			}
			
			sql.append(" WHERE  APL_ID = ? ");

			if(conn==null){
				SessionImpl session = (SessionImpl) (em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(sql.toString());
			int idx = 1;
			ps.setObject(idx++, status);
			ps.setObject(idx++, message);
			ps.setObject(idx++, userId);
			if(transactionUpdateDt != null){
				ps.setTimestamp(idx++, transactionUpdateDt);
				ps.setTimestamp(idx++, transactionUpdateDt);
			}else{
				ps.setTimestamp(idx++, sysdate);
			}
			ps.setObject(idx++, appId);
			
			updatedCnt = ps.executeUpdate();
			ps.close();
			ps = null;
			
			completed = true;
			return updatedCnt;
		} catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (completed) {
						conn.commit();
					} else {
						conn.rollback();
					}
					if (closeConnection) {
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public int insertLogUploadStatus(String uploadType, String userId, List<Object[]> dataList, String conditionDeleteUploadSts) throws CommonErrorException {
		int insertedCnt = 0;
		boolean completed = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			SessionImpl session = (SessionImpl) (em.getDelegate());
			conn = session.getJdbcConnectionAccess().obtainConnection();
			conn.setAutoCommit(false);
			
			StringBuilder sql = new StringBuilder();
			if(!Strings.isNullOrEmpty(conditionDeleteUploadSts)){
				sql.append(" DELETE FROM TB_L_UPLOAD_STS  ");
				sql.append("  WHERE ").append(conditionDeleteUploadSts);
				
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
				ps.close();
				ps = null;
			}
			
			sql = new StringBuilder();
			sql.append(" UPDATE TB_L_UPLOAD_STS  ");
			sql.append("    SET STATUS = 'I', UPDATE_DT = ? , UPDATE_BY = '"+userId+"' ");
			sql.append("  WHERE UPLOAD_TYPE = '"+uploadType+"' ");
			sql.append("  AND CREATE_BY = '"+userId+"' ");			
			sql.append("  AND STATUS IN ('Q','P') ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, FormatUtil.currentTimestampToOracleDB());
			ps.executeUpdate();
			ps.close();
			ps = null;
			
			sql = new StringBuilder();
			sql.append(" INSERT INTO TB_L_UPLOAD_STS ");
			sql.append(" 			  (UPLOAD_TYPE, ");
			sql.append(" 			   GETSUDO_MONTH, ");
			sql.append(" 			   TIMING, ");
			sql.append(" 			   VEHICLE_PLANT, ");
			sql.append(" 			   UNIT_PLANT, ");
			sql.append(" 			   VEHICLE_MODEL, ");
			sql.append(" 			   UNIT_MODEL, ");
			sql.append(" 			   UNIT_PARENT_LINE, ");
			sql.append(" 			   UNIT_SUB_LINE, ");
			sql.append(" 			   FILE_NAME, ");
			sql.append(" 			   MODULE_ID, ");
			sql.append(" 			   FUNCTION_ID, ");
			sql.append(" 			   APL_ID, ");
			sql.append(" 			   STATUS, ");
			sql.append(" 			   MESSAGE, ");
			sql.append(" 			   TRANS_UPDATE_DT, ");
			sql.append(" 			   CREATE_BY, ");
			sql.append(" 			   CREATE_DT, ");
			sql.append(" 			   UPDATE_BY, ");
			sql.append(" 			   UPDATE_DT) ");
			sql.append(" 			VALUES ");
			sql.append(" 			  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

			CommonSQLAdapter adapter = new CommonSQLAdapter();
			if ((dataList != null) && (!dataList.isEmpty())) {
				insertedCnt = adapter.execute(conn, sql.toString(), dataList.toArray());
			}

			completed = true;
			return insertedCnt;
		} catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (completed) {
						conn.commit();
					} else {
						conn.rollback();
					}
					conn.close();
					conn = null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public Object[] getGetsudoMonthDisplay(Connection conn, String getsudoMonth) throws CommonErrorException {
		List<String> result = new ArrayList<String>();
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
			
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT INITCAP(TO_CHAR(ADD_MONTHS(TRUNC(TO_DATE(?, 'Mon-YY'), 'yyyy'), ");
			SQL.append("     LEVEL + (TO_NUMBER(TO_CHAR(TO_DATE(?, 'Mon-YY'), 'mm')) - 2)), 'Mon-YY')) MONTHYEAR ");
			SQL.append(" FROM DUAL ");
			SQL.append(" CONNECT BY LEVEL <= (SELECT T.DISPLAY_MONTH FROM TB_C_GETSUDO_MONTH T ");
			SQL.append(" 					 WHERE T.MONTH = TRIM(TO_CHAR(TO_DATE(?, 'Mon-YY'), 'Month'))) ");
			
			ps = conn.prepareStatement(SQL.toString());
			ps.setString(1, getsudoMonth);
			ps.setString(2, getsudoMonth);
			ps.setString(3, getsudoMonth);
			rs = ps.executeQuery();
			while(rs.next()){
				result.add(rs.getString("MONTHYEAR"));
			}
			
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(ps!=null){
						ps.close();
						ps = null;
					}
					if(closeConnection){
						conn.close();
						conn = null;
					}
				}
				if(result.size()==0){
					String[] params = new String[5];
					params[0] = "Getsudo Month:" + getsudoMonth;
					params[1] = "TB_C_GETSUDO_MONTH";
					params[2] = "";
					params[3] = "";
					params[4] = "";
					throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params, AppConstants.ERROR);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		return result.toArray();
	}
	
	@Override
	public String getGetsudoMonthDisplayStr(Connection conn, String getsudoMonth) throws CommonErrorException {
		String inGmStr = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean closeConnection = true;
		try{
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT LISTAGG('''' || MONTHYEAR || '''', ',') WITHIN GROUP(ORDER BY TO_DATE(MONTHYEAR, 'Mon-YY')) AS IN_GETSUDO_MONTH ");
					SQL.append("   FROM ( ");
			SQL.append(" SELECT INITCAP(TO_CHAR(ADD_MONTHS(TRUNC(TO_DATE(?, 'Mon-YY'), 'yyyy'), ");
			SQL.append("     LEVEL + (TO_NUMBER(TO_CHAR(TO_DATE(?, 'Mon-YY'), 'mm')) - 2)), 'Mon-YY')) MONTHYEAR ");
			SQL.append(" FROM DUAL ");
			SQL.append(" CONNECT BY LEVEL <= (SELECT T.DISPLAY_MONTH FROM TB_C_GETSUDO_MONTH T ");
			SQL.append(" 					 WHERE T.MONTH = TRIM(TO_CHAR(TO_DATE(?, 'Mon-YY'), 'Month'))) ");
			SQL.append(" ) ");
			
			ps = conn.prepareStatement(SQL.toString());
			ps.setString(1, getsudoMonth);
			ps.setString(2, getsudoMonth);
			ps.setString(3, getsudoMonth);
			rs = ps.executeQuery();
			while(rs.next()){
				inGmStr = rs.getString("IN_GETSUDO_MONTH");
			}
			
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(ps!=null){
						ps.close();
						ps = null;
					}
					if(closeConnection){
						conn.close();
						conn = null;
					}
				}
				if(inGmStr==null){
					String[] params = new String[5];
					params[0] = "Getsudo Month:" + getsudoMonth;
					params[1] = "TB_C_GETSUDO_MONTH";
					params[2] = "";
					params[3] = "";
					params[4] = "";
					throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params, AppConstants.ERROR);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		return inGmStr;
	}

	@Override
	public List<String> getUnitPlantMaster(Connection conn, String getsudoMonth) throws CommonErrorException {
		List<String> ls = new ArrayList<String>();
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
			
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT 	");
			SQL.append(" 	UNIT_PLANT  ");
			SQL.append(" FROM TB_M_UNIT_PLANT ");
			SQL.append(" WHERE ");
			SQL.append(" GROUP BY UNIT_PLANT ");
			SQL.append(" ORDER BY UPPER(UNIT_PLANT) ");
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				ls.add(rs.getString("UNIT_PLANT"));
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally {
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
		return ls;
	}
	
	@Override
	public List<String> getVehiclePlantMaster(Connection conn, String getsudoMonth) throws CommonErrorException {
		List<String> ls = new ArrayList<String>();
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
			
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT	");
			SQL.append(" 	VEHICLE_PLANT  ");
			SQL.append(" FROM TB_M_VEHICLE_PLANT ");
			SQL.append(" WHERE ");
			SQL.append(" GROUP BY VEHICLE_PLANT ");
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				ls.add(rs.getString("VEHICLE_PLANT"));
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally {
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
		return ls;
	}
	
	@Override
	public List<String> getVehicleUnitRelationMaster(Connection conn, String getsudoMonth) throws CommonErrorException {
		List<String> ls = new ArrayList<String>();
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
			
			StringBuilder SQL = new StringBuilder();
			SQL.append(" SELECT	");
			SQL.append(" 	VEHICLE_PLANT  ");
			SQL.append(" FROM TB_M_VEHICLE_UNIT_RELATION ");
			SQL.append(" WHERE ");
			SQL.append(" GROUP BY VEHICLE_PLANT ORDER BY UPPER(VEHICLE_PLANT) ASC");
			
			ps = conn.prepareStatement(SQL.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				ls.add(rs.getString("VEHICLE_PLANT"));
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally {
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
		return ls;
	}

	@Override
	public void deleteLogDetail(Connection conn, String[] params) throws CommonErrorException {
		PreparedStatement ps = null;
		try {
			StringBuilder delSQL = new StringBuilder();
			delSQL.append(" DELETE FROM TB_L_UPLOAD_DETAIL ");
			delSQL.append(" WHERE GETSUDO_MONTH = ? ");
			delSQL.append("   AND TIMING = ? ");
			delSQL.append("   AND VEHICLE_PLANT = ? ");
			delSQL.append("   AND VEHICLE_MODEL = ? ");
			if(params.length == 6){
				delSQL.append("   AND UNIT_PLANT = ? ");
				delSQL.append("   AND UNIT_MODEL = ? ");
			}

			ps = conn.prepareStatement(delSQL.toString());
			int idx = 1;
			for(String param : params){
				ps.setString(idx++, param);
			}
			
			ps.executeUpdate();
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(ps!=null){
						ps.close();
						ps = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
	public int insertLogDetail(Connection conn, List<Object[]> dataList, String type) throws CommonErrorException {
		int insertedCnt = 0;
		try {
			StringBuilder sql = new StringBuilder();			
			sql.append("INSERT INTO TB_L_UPLOAD_DETAIL ");
			sql.append("  (APL_ID, ");
			sql.append("   GETSUDO_MONTH, ");
			sql.append("   TIMING, ");
			sql.append("   VEHICLE_PLANT, ");
			sql.append("   VEHICLE_MODEL, ");
			sql.append("   UNIT_PLANT, ");
			sql.append("   UNIT_MODEL, ");
			sql.append("   ERROR_SHEET, ");
			sql.append("   RUNNING_NO, "); 
			sql.append("   ERROR_DATE, ");
			sql.append("   ERROR_MONTH, ");
			sql.append("   ERROR_RUNDOWN, ");
			sql.append("   ERROR_CALENDAR, ");
			sql.append("   ERROR_WORKSHEET, ");
			sql.append("   ERROR_STOCK_MIN, ");
			sql.append("   ERROR_STOCK_MAX, ");
			sql.append("   CREATE_BY, "); 
			sql.append("   CREATE_DT, "); 
			sql.append("   UPDATE_BY, "); 
			sql.append("   UPDATE_DT) ");
			sql.append("VALUES ");
			
			sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, "); //Key
			if(AppConstants.LOG_DETAIL_CALENDAR.equals(type)){
				sql.append("   ?, ?, ");
				sql.append("   (SELECT T.VALUE FROM TB_M_SYSTEM T ");
				sql.append(" 	WHERE T.CATEGORY = 'COMMON' ");
				sql.append("      AND T.SUB_CATEGORY = 'CALENDAR_FLAG' AND T.STATUS = 'Y' "); 
				sql.append("      AND T.CD = DECODE(?, 'F', 'F', 'W')), ");
				sql.append("   ?, ?, ?, ?, ");
			}else if(AppConstants.LOG_DETAIL_STOCK.equals(type) 
					|| AppConstants.LOG_DETAIL_PROD_VOL.equals(type) 
					|| AppConstants.LOG_DETAIL_PACK_VOL.equals(type)
					|| AppConstants.LOG_DETAIL_OTHER.equals(type)
					|| AppConstants.LOG_DETAIL_PROD_VOL_DIAGRAM.equals(type)){
				sql.append("   ?, ?, ?, ?, ?, ?, ?, ");
			}else{
				//Default
				sql.append("   ?, ?, ?, ?, ?, ?, ?, ");
			}
			sql.append("   ?, ?, ?, ?) "); //Create/Update
			
			CommonSQLAdapter adapter = new CommonSQLAdapter();
			if(dataList!=null && !dataList.isEmpty()){
			
				insertedCnt = adapter.execute(conn, sql.toString() , dataList.toArray());
			}
			return insertedCnt;
		}catch (CommonErrorException e){
			throw e;
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
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
	public int insertRundownKompoSts(Connection conn, String[] paramDel, Object[] data) throws Exception{
		int insertedCnt = 0;
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
			StringBuilder delSQL = new StringBuilder();
			delSQL.append(" DELETE FROM TB_R_RUNDOWN_KOMPO_STS T ");
			delSQL.append("  WHERE  T.VERSION = ? ");
			delSQL.append("     AND T.GETSUDO_MONTH = ? ");
			delSQL.append("     AND T.TIMING = ? ");
			delSQL.append("     AND T.VEHICLE_PLANT = ? ");
			delSQL.append("     AND T.VEHICLE_MODEL = ? ");
			delSQL.append("     AND T.UNIT_PLANT = ? ");
			delSQL.append("     AND T.UNIT_MODEL = ? ");

			ps = conn.prepareStatement(delSQL.toString());
			int idx = 1;
			for(String param : paramDel){
				ps.setString(idx++, param);
			}
			ps.executeUpdate();
			
			if(ps!=null){
				ps.close();
				ps = null;
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO TB_R_RUNDOWN_KOMPO_STS ");
			sql.append("  (VERSION, ");
			sql.append("   GETSUDO_MONTH, ");
			sql.append("   TIMING, ");
			sql.append("   VEHICLE_PLANT, ");
			sql.append("   VEHICLE_MODEL, "); // param 5
			sql.append("   UNIT_PLANT, ");
			sql.append("   UNIT_TYPE, ");
			sql.append("   UNIT_MODEL, ");
			sql.append("   UPLOAD_TYPE, "); // param 9
			sql.append("   UPLOAD_STS, ");
			sql.append("   ROUND_FLAG, ");
			sql.append("   UPLOAD_FILE_NAME, ");
			sql.append("   APL_ID, ");
			sql.append("   CREATE_BY, ");
			sql.append("   CREATE_DT, ");
			sql.append("   UPDATE_BY, ");
			sql.append("   UPDATE_DT) ");
			sql.append("VALUES ");
			sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sql.append("   ?, ?, ?, ?, ?, ?, ?) ");

			ps = conn.prepareStatement(sql.toString());
			
			idx = 1;
			for(Object param : data){
				ps.setObject(idx++, param);
			}
			
			insertedCnt = ps.executeUpdate();
			completed = true;
			return insertedCnt;
		} catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (completed) {
						conn.commit();
					} else {
						conn.rollback();
					}
					if (closeConnection) {
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public int insertRundownKompoSts(Connection conn, String[] paramDel, List<Object[]> datas) throws Exception{
		int insertedCnt = 0;
		boolean closeConnection = true;
		boolean completed = false;
		PreparedStatement ps = null;
		try {
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
			StringBuilder delSQL = new StringBuilder();
			delSQL.append(" DELETE FROM TB_R_RUNDOWN_KOMPO_STS T ");
			delSQL.append("  WHERE  T.VERSION = ? ");
			delSQL.append("     AND T.GETSUDO_MONTH = ? ");
			delSQL.append("     AND T.TIMING = ? ");
			delSQL.append("     AND T.VEHICLE_PLANT = ? ");
			delSQL.append("     AND T.VEHICLE_MODEL = ? ");
			
			//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
			if(datas != null && datas.size() > 0){
				int i = 0;
				delSQL.append("     AND (");
				for(Object[] data : datas){
					if(i==0){
						delSQL.append("     (T.UNIT_PLANT = '"+data[5]+"' AND T.UNIT_MODEL = '"+data[7]+"') ");
					}else{
						delSQL.append("    OR (T.UNIT_PLANT = '"+data[5]+"' AND T.UNIT_MODEL = '"+data[7]+"') ");
					}
					i++;
				}
				delSQL.append("     )");
			}
			//END CR UT-002 2018/02/16

			ps = conn.prepareStatement(delSQL.toString());
			int idx = 1;
			for(String param : paramDel){
				ps.setString(idx++, param);
			}
			ps.executeUpdate();
			
			if(ps!=null){
				ps.close();
				ps = null;
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO TB_R_RUNDOWN_KOMPO_STS ");
			sql.append("  (VERSION, ");
			sql.append("   GETSUDO_MONTH, ");
			sql.append("   TIMING, ");
			sql.append("   VEHICLE_PLANT, ");
			sql.append("   VEHICLE_MODEL, "); // param 5
			sql.append("   UNIT_PLANT, ");
			sql.append("   UNIT_TYPE, ");
			sql.append("   UNIT_MODEL, ");
			sql.append("   UPLOAD_TYPE, "); // param 9
			sql.append("   UPLOAD_STS, ");
			sql.append("   ROUND_FLAG, ");
			sql.append("   UPLOAD_FILE_NAME, ");
			sql.append("   APL_ID, ");
			sql.append("   CREATE_BY, ");
			sql.append("   CREATE_DT, ");
			sql.append("   UPDATE_BY, ");
			sql.append("   UPDATE_DT) ");
			sql.append("VALUES ");
			sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sql.append("   ?, ?, ?, ?, ?, ?, ?) ");

			CommonSQLAdapter adapter = new CommonSQLAdapter();
			if ((datas != null) && (!datas.isEmpty())) {
				insertedCnt = adapter.execute(conn, sql.toString(), datas.toArray());
			}

			completed = true;
			return insertedCnt;
		} catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					if (completed) {
						conn.commit();
					} else {
						conn.rollback();
					}
					if (closeConnection) {
						conn.close();
						conn = null;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
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
	public String genSQLOverlap(String tcFrom, String tcTo, String tableForCheck, String whereCriteria) {

		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT CASE WHEN COUNT(1) > 0 THEN 'NG' ELSE 'OK' END   \n");
		sql.append("  FROM (    \n");
		sql.append("  		SELECT TC_FROM,    \n");	
		sql.append("  		       (CASE WHEN TC_TO IS NULL AND TC_FROM < TO_DATE('"+tcFrom+"','Mon-YY')     \n");	
		sql.append("  		        THEN LAST_DAY(TC_FROM) ELSE NVL(TC_TO, TO_DATE('"+AppConstants.DEFULAT_DATE+"','DD/MM/YYYY'))    \n");
		sql.append("  		       END) AS TC_TO    \n");
		sql.append("  		FROM  "+tableForCheck+"   \n");
		sql.append("  		WHERE  "+whereCriteria+"  \n");
		sql.append("  ) T  ");	
		sql.append("  WHERE  ");
		sql.append("  TC_FROM  BETWEEN TO_DATE('"+tcFrom+"','Mon-YY') AND NVL(LAST_DAY(TO_DATE('"+tcTo+"', 'Mon-YY')), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY')) OR    \n");
		sql.append("  TC_TO  BETWEEN TO_DATE('"+tcFrom+"','Mon-YY') AND NVL(LAST_DAY(TO_DATE('"+tcTo+"', 'Mon-YY')), TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY'))   \n");
	
		return sql.toString();
	}

	@Override
	public String genSQLExistVehiclePlantUnitRelation(String tcTo, String whereCriteria) {
		StringBuilder sql = new StringBuilder();
			sql.append("  SELECT (CASE  \n");
			sql.append("           WHEN FOUND_CNT IS NOT NULL AND FOUND_CNT = 0 THEN  \n");
			sql.append("            'NG'  \n");
			sql.append("           ELSE  \n");
			sql.append("            'OK'  \n");
			sql.append("         END) AS VALID    \n");
			sql.append("  FROM (  \n");
			sql.append(" 		SELECT SUM((CASE WHEN NVL(TC_TO,LAST_DAY(TO_DATE('"+AppConstants.DEFULAT_DATE+"','DD/MM/YYYY'))) <=    \n");
			sql.append("             		NVL(LAST_DAY(TO_DATE('"+tcTo+"','Mon-YY')),TO_DATE('"+AppConstants.DEFULAT_DATE+"', 'DD/MM/YYYY'))   \n");
			sql.append("             		THEN 1 ELSE 0 END)  \n");	
			sql.append("             	  ) AS FOUND_CNT   \n");		
			sql.append("        FROM TB_M_VEHICLE_UNIT_RELATION   \n");			
			sql.append("        WHERE "+whereCriteria+"  \n");
			sql.append("  )  \n");
		return sql.toString();
	}
	
	@Override
	public Object[] getPrevGetsudoMonthAndTiming(Connection conn, String getsudoMonthCurrent, String timingCurrent) throws CommonErrorException{
		List<String> result = new ArrayList<String>();
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
        try{
        	StringBuilder SQL = new StringBuilder();
        	SQL.append("SELECT (  \n");
        	SQL.append("       CASE \n");
        	SQL.append("              WHEN  \n");
        	SQL.append("                     (SELECT VALUE \n");
        	SQL.append("                             FROM    TB_M_SYSTEM S \n");
        	SQL.append("                             WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                             AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"' \n");
        	SQL.append("                             AND     S.CD           =  \n");
        	SQL.append("                                     (SELECT MIN(CD) \n");
        	SQL.append("                                     FROM    TB_M_SYSTEM S \n");
        	SQL.append("                                     WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                                     AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'  \n");
        	SQL.append("                                     )  \n");
        	SQL.append("                     )  \n");
        	SQL.append("                     = '"+timingCurrent+"'  \n");
        	SQL.append("              THEN TO_CHAR(ADD_MONTHS(TO_DATE('"+getsudoMonthCurrent+"', 'Mon-YY'), -1), 'Mon-YY') \n");
        	SQL.append("              ELSE '"+getsudoMonthCurrent+"' \n");
        	SQL.append("       END) AS PREV_GETSUDOMONTH, (  \n");
        	SQL.append("       CASE \n");
        	SQL.append("              WHEN  \n");
        	SQL.append("                     (SELECT VALUE \n");
        	SQL.append("                             FROM    TB_M_SYSTEM S \n");
        	SQL.append("                             WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                             AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"' \n");
        	SQL.append("                             AND     S.CD           =  \n");
        	SQL.append("                                     (SELECT MIN(CD) \n");
        	SQL.append("                                     FROM    TB_M_SYSTEM S \n");
        	SQL.append("                                     WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                                     AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'  \n");
        	SQL.append("                                     )  \n");
        	SQL.append("                     )  \n");
        	SQL.append("                     = '"+timingCurrent+"'  \n");
        	SQL.append("              THEN \n");
        	SQL.append("                     (SELECT VALUE \n");
        	SQL.append("                     FROM    TB_M_SYSTEM S \n");
        	SQL.append("                     WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                     AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"' \n");
        	SQL.append("                     AND     S.CD           =  \n");
        	SQL.append("                             (SELECT MAX(CD) \n");
        	SQL.append("                             FROM    TB_M_SYSTEM S \n");
        	SQL.append("                             WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                             AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'  \n");
        	SQL.append("                             )  \n");
        	SQL.append("                     ) \n");
        	SQL.append("              ELSE \n");
        	SQL.append("                    (SELECT VALUE \n");
        	SQL.append("                    FROM    TB_M_SYSTEM S \n");
        	SQL.append("                    WHERE   S.CATEGORY      = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                    AND     S.SUB_CATEGORY  = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"' \n");
        	SQL.append("                    AND     TO_NUMBER(S.CD) = \n");
        	SQL.append("                            (SELECT TO_NUMBER(CD) - 1 \n");
        	SQL.append("                            FROM    TB_M_SYSTEM S \n");
        	SQL.append("                            WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"' \n");
        	SQL.append("                            AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"' \n");
        	SQL.append("                            AND     S.VALUE        = '"+timingCurrent+"'  \n");
        	SQL.append("                            )  \n");
        	SQL.append("                    ) \n");
        	SQL.append("       END) AS PREV_TIMING \n");
        	SQL.append("FROM   DUAL");
			
        	if(conn==null){
        		SessionImpl session = (SessionImpl)(em.getDelegate());
        		conn = session.getJdbcConnectionAccess().obtainConnection();
        	}else{
        		closeConnection = false;
        	}
			
			pp = conn.prepareStatement(SQL.toString());
			rs = pp.executeQuery();
			while(rs.next()){
				result.add(rs.getString("PREV_GETSUDOMONTH"));
				result.add(rs.getString("PREV_TIMING"));
			}
			
			if(result.size()<=0){
				String[] params = new String[5];
				params[0] = "Timing";
				params[1] = "TB_M_SYSTEM";
				params[2] = "CATEGORY="+AppConstants.SYS_CATEGORY_MASTER;
				params[3] = "SUB_CATEGORY="+AppConstants.SYS_SUB_CATEGORY_TIMING;
				params[4] = "";
				throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params, AppConstants.ERROR);
			}
        }catch (CommonErrorException e){
				throw e;
        }catch(Exception e){
        	throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
        } finally {
        	try{
				if (rs !=null) {
					rs.close();					
		            rs = null;
		        }
				
				if (pp !=null) {
		            pp.close();
		            pp = null;
		        }
				
				if(conn!=null && !conn.isClosed() && closeConnection){
					conn.close();
					conn = null;
				}
        	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        return result.toArray();
	}	

	@Override
	public Object[] getNextGetsudoMonthAndTiming(Connection conn, String getsudoMonthCurrent, String timingCurrent) throws CommonErrorException{
		List<String> result = new ArrayList<String>();
		boolean closeConnection = true;
		PreparedStatement pp = null;
		ResultSet rs = null;
        try{
        	StringBuilder SQL = new StringBuilder();
        	SQL.append(" SELECT (CASE WHEN    \n");
        	SQL.append("                  (SELECT VALUE   \n");
        	SQL.append("                   FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                   WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                   AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'   \n");
        	SQL.append("                   AND     S.CD  = (SELECT MAX(CD)   \n");
        	SQL.append("                                       FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                                       WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                                       AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'    \n");
        	SQL.append("                                    )    \n");
        	SQL.append("                   ) = '"+timingCurrent+"'    \n");
        	SQL.append("                THEN  TO_CHAR(ADD_MONTHS(TO_DATE('"+getsudoMonthCurrent+"', 'Mon-YY'), +1), 'Mon-YY')  \n");
        	SQL.append("                ELSE  '"+getsudoMonthCurrent+"'  \n");
        	SQL.append("         END) AS NEXT_GETSUDOMONTH,     \n");
        	SQL.append("        (CASE  WHEN    \n");
        	SQL.append("                   (SELECT VALUE   \n");
        	SQL.append("                    FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                    WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                    AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'   \n");
        	SQL.append("                    AND     S.CD  = (SELECT MAX(CD)   \n");
        	SQL.append("                                       FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                                       WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                                       AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'    \n");
        	SQL.append("                                     )    \n");
        	SQL.append("                    ) = '"+timingCurrent+"'    \n");
        	SQL.append("                THEN (SELECT VALUE   \n");
        	SQL.append("                       FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                       WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                       AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'   \n");
        	SQL.append("                       AND     S.CD           =    \n");
        	SQL.append("                               (SELECT MIN(CD)   \n");
        	SQL.append("                               	FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                               	WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                               	AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'    \n");
        	SQL.append("                               )    \n");
        	SQL.append("                       )  \n");
        	SQL.append("                ELSE (SELECT VALUE   \n");
        	SQL.append("                      FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                      WHERE   S.CATEGORY      = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                      AND     S.SUB_CATEGORY  = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'   \n");
        	SQL.append("                      AND     TO_NUMBER(S.CD) =   \n");
        	SQL.append("                              (SELECT TO_NUMBER(CD) + 1   \n");
        	SQL.append("                              	FROM    TB_M_SYSTEM S   \n");
        	SQL.append("                              	WHERE   S.CATEGORY     = '"+AppConstants.SYS_CATEGORY_MASTER+"'   \n");
        	SQL.append("                              	AND     S.SUB_CATEGORY = '"+AppConstants.SYS_SUB_CATEGORY_TIMING+"'   \n");
        	SQL.append("                              	AND     S.VALUE        = '"+timingCurrent+"'    \n");
        	SQL.append("                              )    \n");
        	SQL.append("                      )  \n");
        	SQL.append("                      \n");
        	SQL.append("         END) AS NEXT_TIMING   \n");
        	SQL.append("  FROM   DUAL  \n");
			
        	if(conn==null){
        		SessionImpl session = (SessionImpl)(em.getDelegate());
        		conn = session.getJdbcConnectionAccess().obtainConnection();
        	}else{
        		closeConnection = false;
        	}
			
			pp = conn.prepareStatement(SQL.toString());
			rs = pp.executeQuery();
			while(rs.next()){
				result.add(rs.getString("NEXT_GETSUDOMONTH"));
				result.add(rs.getString("NEXT_TIMING"));
			}
			
			if(result.size()<=0){
				String[] params = new String[5];
				params[0] = "Timing";
				params[1] = "TB_M_SYSTEM";
				params[2] = "CATEGORY="+AppConstants.SYS_CATEGORY_MASTER;
				params[3] = "SUB_CATEGORY="+AppConstants.SYS_SUB_CATEGORY_TIMING;
				params[4] = "";
				throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params, AppConstants.ERROR);
			}
        }catch (CommonErrorException e){
				throw e;
        }catch(Exception e){
        	throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
        } finally {
        	try{
				if (rs !=null) {
					rs.close();					
		            rs = null;
		        }
				
				if (pp !=null) {
		            pp.close();
		            pp = null;
		        }
				
				if(conn!=null && !conn.isClosed() && closeConnection){
					conn.close();
					conn = null;
				}
        	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        return result.toArray();
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
	
	@Override
	public boolean worksheetAlreadyCompleted(Connection conn, String getsudoMonth, String timing, String vehiclePlant, String vehicleModel) throws Exception {
		boolean alreadyCompleted = false;
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
        	StringBuilder sql = new StringBuilder();
        	sql.append(" SELECT COUNT(1) AS ROWS_CNT ");
        	sql.append(" FROM TB_R_KAIKIENG_H ");
        	sql.append(" WHERE VERSION = '"+AppConstants.COMPANY_CD_TDEM+"' ");
        	sql.append("   AND GETSUDO_MONTH = '"+getsudoMonth+"' ");
        	sql.append("   AND TIMING = '"+timing+"' ");
        	sql.append("   AND VEHICLE_PLANT = '"+vehiclePlant+"' ");
        	sql.append("   AND VEHICLE_MODEL = '"+vehicleModel+"' ");
        	sql.append("   AND WORKSHEET_STATUS = '"+AppConstants.WORKSHEET_STATUS_COMPLETED+"' ");
        	
        	ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				BigDecimal totalRows = rs.getBigDecimal("ROWS_CNT");
				if(totalRows.intValue()>0){
					alreadyCompleted = true;
				}
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
		return alreadyCompleted;
	}
}
