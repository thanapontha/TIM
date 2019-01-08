package th.co.toyota.bw0.web.master.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.common.form.CommonBaseForm;
import th.co.toyota.bw0.common.repository.CommonRepository;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.bw0.web.master.form.SystemMasterForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

import com.google.common.base.Strings;

@Repository
public class SystemMasterRepositoryImp implements SystemMasterRepository{
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	final Logger logger = LoggerFactory.getLogger(SystemMasterRepositoryImp.class);

	@Autowired
	private CommonRepository commonRepository;	
	
	private String sep = "\\" + AppConstants.CHECKBOX_SEPERATER;

	@Override
	public boolean addObject(Connection conn, CommonBaseForm activeform, CSC22110UserInfo userInfo) throws CommonErrorException {
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		try {
			SystemMasterForm form = (SystemMasterForm) activeform;
			
			Timestamp currentDate = FormatUtil.currentTimestampToOracleDB();
			
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
			StringBuilder sql = new StringBuilder();
			
			sql.append(" INSERT INTO TB_M_SYSTEM ");
			sql.append(" (CATEGORY, SUB_CATEGORY, CD, VALUE, REMARK, STATUS, CREATE_BY, CREATE_DT, UPDATE_BY, UPDATE_DT)  ");
			sql.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, form.getCategory().toUpperCase());
			ps.setString(2, form.getSubCategory().toUpperCase());
			ps.setString(3, form.getCode().toUpperCase());
			ps.setString(4, form.getValue());
			ps.setString(5, form.getRemark());
			ps.setString(6, form.getStatus());	
			ps.setString(7, userInfo.getUserId());
			ps.setTimestamp(8, currentDate);
			ps.setString(9, userInfo.getUserId());
			ps.setTimestamp(10, currentDate);
			
			ps.executeUpdate();
			completed = true;
		}catch(SQLIntegrityConstraintViolationException e){
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new CommonErrorException(CST30000Messages.ERR_ENTITY_EXISTS, new String[] { "Data already exists" }, AppConstants.ERROR);
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			CommonUtility.closeConnection(conn, null, ps, closeConnection, completed);
		}
		
		return completed;
	}

	@Override
	public boolean editObject(Connection conn, CommonBaseForm activeform, CSC22110UserInfo userInfo) throws CommonErrorException {
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		try {
			SystemMasterForm form = (SystemMasterForm) activeform;
			String[] updateKeySet = form.getUpdateKeySet().split(sep);
			if ((updateKeySet != null) && (updateKeySet.length >= 4)) {
				String category = updateKeySet[0];
				String subCategory = updateKeySet[1];
				String code = updateKeySet[2];
				Timestamp dtUpdateDate = FormatUtil.convertTimestampToOracleDB(updateKeySet[3]);
				
				StringBuilder sql = new StringBuilder();
				Timestamp currentDate = FormatUtil.currentTimestampToOracleDB();
				
				if(conn==null){
					SessionImpl session = (SessionImpl)(em.getDelegate());
					conn = session.getJdbcConnectionAccess().obtainConnection();
				}else{
					closeConnection = false;
				}
				conn.setAutoCommit(false);					
				
				
				sql = new StringBuilder();
				sql.append(" UPDATE TB_M_SYSTEM SET ");
				sql.append("	VALUE = ? ,");
				sql.append("	REMARK = ? ,");
				sql.append("	STATUS = ? ,");
				sql.append("	UPDATE_BY = ? ,");
				sql.append("	UPDATE_DT = ? ");
				sql.append(" WHERE CATEGORY = ?  ");
				sql.append(" 	AND SUB_CATEGORY = ?  ");
				sql.append(" 	AND CD = ?  ");
				sql.append(" 	AND UPDATE_DT = ? ");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(1, form.getValue());
				ps.setString(2, form.getRemark());
				ps.setString(3, form.getStatus());
				ps.setString(4, userInfo.getUserId());
				ps.setTimestamp(5, currentDate);
				ps.setString(6, category.toUpperCase());
				ps.setString(7, subCategory.toUpperCase());
				ps.setString(8, code.toUpperCase());					
				ps.setTimestamp(9, dtUpdateDate);
				
				int count = ps.executeUpdate();
				if (count == 0) {
					throw new CommonErrorException(CST30000Messages.ERROR_UPDATE_CONCURRENCY_CHECK, new String[] {"Error in updated, because of concurrency check",""}, AppConstants.ERROR);
				}
				
				completed = true;
			}else{
				logger.debug("ERROR: Don't have key for check update data");
				throw new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR,
						new String[] { "ERROR: Don't have key for check update data" }, AppConstants.ERROR);
			}
		}catch(SQLIntegrityConstraintViolationException e){
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new CommonErrorException(CST30000Messages.ERR_ENTITY_EXISTS, new String[] { "Data already exists" },
					AppConstants.ERROR);
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			CommonUtility.closeConnection(conn, null, ps, closeConnection, completed);
		}
		return completed;
	}

	@Override
	public void deleteObject(Connection conn, List<Object[]> objectsDelete, CommonBaseForm activeform) throws CommonErrorException {
		boolean closeConnection = true;
		PreparedStatement ps = null;
		boolean completed = false;
		try{
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
			if(objectsDelete!=null && !objectsDelete.isEmpty()){
				StringBuilder sql = new StringBuilder();				
				sql.append(" DELETE FROM TB_M_SYSTEM ");
				sql.append(" WHERE CATEGORY = ?  ");
				sql.append(" 	AND SUB_CATEGORY = ?  ");
				sql.append(" 	AND CD = ?  ");
				sql.append(" 	AND UPDATE_DT = ? ");
				
				for(int i=0;i<objectsDelete.size();i++){
					Object[] objArr = objectsDelete.get(i);
					ps = conn.prepareStatement(sql.toString());
					
					String category = (String)objArr[0];
					String subCategory = (String)objArr[1];
					String code = (String)objArr[2];
					Timestamp dtUpdateDate = FormatUtil.convertTimestampToOracleDB((String)objArr[3]);
					
					ps.setString(1, category.toUpperCase());
					ps.setString(2, subCategory.toUpperCase());
					ps.setString(3, code.toUpperCase());
					ps.setTimestamp(4, dtUpdateDate);
					
					int countDelete = ps.executeUpdate();
					ps.close();
					ps = null;
					if (countDelete == 0) {
						throw new CommonErrorException(CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK, new String[] {"Error in Delete because of concurrency check",""}, AppConstants.ERROR);
					}
				}
			}
			objectsDelete = null;
			completed = true;
		}  catch (Exception e) {
			completed = false;
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			CommonUtility.closeConnection(conn, null, ps, closeConnection, completed);
		}
	}

	@Override
	public void loadComboboxs(Connection conn, CommonBaseForm activeform) throws CommonErrorException {
		SystemMasterForm form = (SystemMasterForm)activeform;
		StringBuilder criteria = new StringBuilder();
		criteria.append(" STATUS = 'Y' ");
		List<ComboValue> categoryList = commonRepository.loadCombobox(conn, AppConstants.TABLE_TB_M_SYSTEM, 
																	   new String[]{"CATEGORY","CATEGORY"}, 
																	   criteria.toString(), 
																	   "CATEGORY ASC");
		form.setCategoryList(categoryList);
	}

	@Override
	public List<ComboValue> loadSubCategory(Connection conn, String category)throws CommonErrorException {
		StringBuilder criteria = new StringBuilder();
		criteria.append(" CATEGORY = '"+category+"'");
		criteria.append(" AND STATUS = 'Y' ");
		return commonRepository.loadCombobox(conn, AppConstants.TABLE_TB_M_SYSTEM, 
																	   new String[]{"SUB_CATEGORY","SUB_CATEGORY"}, 
																	   criteria.toString(), 
																	   "SUB_CATEGORY ASC");
	}

	@Override
	public Object[] generateSearchQuery(SystemMasterForm activeform) throws CommonErrorException {
		SystemMasterForm form = activeform;
		StringBuilder SQL  = new StringBuilder();
		SQL.append(" SELECT 	");
		SQL.append(" 	CATEGORY,  ");
		SQL.append(" 	SUB_CATEGORY, ");
		SQL.append(" 	CD, ");
		SQL.append(" 	VALUE, ");
		SQL.append(" 	REMARK, ");
		SQL.append(" 	STATUS, ");
		SQL.append(" 	CREATE_BY, ");
		SQL.append(" 	CREATE_DT, ");
		SQL.append(" 	UPDATE_BY, ");
		SQL.append(" 	UPDATE_DT, ");
		SQL.append(" 	 ROW_NUMBER() OVER(ORDER BY CATEGORY, SUB_CATEGORY, CD ) AS ROW_NUM ");
		SQL.append(" FROM TB_M_SYSTEM ");
		SQL.append(" WHERE UPPER(CATEGORY) = ? ");
		
		List<Object> parameter = new ArrayList<>();
		parameter.add(form.getCategorySearch().toUpperCase());
		if (!Strings.isNullOrEmpty(activeform.getSubCategorySearch())) {
			SQL.append(" AND UPPER(SUB_CATEGORY) = ? ");
			parameter.add(form.getSubCategorySearch());
		}
		if (!Strings.isNullOrEmpty(activeform.getCodeSearch())) {
			if (!activeform.getCodeSearch().contains("*")) {
				SQL.append(" AND UPPER(CD) = ? ");
				parameter.add(form.getCodeSearch().toUpperCase());
			} else {
				SQL.append(" AND UPPER(CD) LIKE ? ");
				parameter.add(activeform.getCodeSearch().replace("*", "%").toUpperCase());
			}
			
		}
		return new Object[]{ SQL, parameter};
	}

	@Override
	public List<SystemInfo> searchObjectList(Connection conn, StringBuilder sql, List<Object> parameter,
			int firstResult, int rowsPerPage) throws CommonErrorException {
		List<SystemInfo> listResult = new ArrayList<>();
		SystemInfo info = null;
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
			
			int sumRowNum = firstResult + rowsPerPage;
			StringBuilder sqlPaging = new StringBuilder();
			sqlPaging.append(" SELECT * FROM ( ");
			sqlPaging.append(sql.toString());
			sqlPaging.append(" ) T ");
			sqlPaging.append(" WHERE T.ROW_NUM >= (" + (firstResult == 0 ? 0 : 1) + " + " + firstResult + ") ");
			sqlPaging.append("   AND T.ROW_NUM <= (" + sumRowNum + ") ");

			ps = conn.prepareStatement(sqlPaging.toString());
			int index = 1;
			for (Object objParam : parameter) {
				ps.setString(index++, (String) objParam);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				info = new SystemInfo();
				info.setId(new SystemInfoId());
				info.getId().setCategory(rs.getString("CATEGORY"));
				info.getId().setSubCategory(rs.getString("SUB_CATEGORY"));
				info.getId().setCode(rs.getString("CD"));
				info.setValue(rs.getString("VALUE"));
				info.setRemark(rs.getString("REMARK"));
				info.setStatus(Character.valueOf(rs.getString("STATUS").charAt(0)));	
				info.setCreateBy(rs.getString("CREATE_BY"));
				info.setCreateDate(rs.getTimestamp("CREATE_DT"));
				info.setUpdateBy(rs.getString("UPDATE_BY"));
				info.setUpdateDate(rs.getTimestamp("UPDATE_DT"));
				listResult.add(info);
			}
		} catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, false);
		} finally {
			CommonUtility.closeConnection(conn, rs, ps, closeConnection);
		}
		return listResult;
	}
}
