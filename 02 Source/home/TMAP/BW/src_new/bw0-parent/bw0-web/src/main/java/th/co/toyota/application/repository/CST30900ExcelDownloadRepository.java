/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST30900ExcelDownloadRepository.java
 * Program Description	    :  Excel download repository.
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  April 8, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 * 1.1		   13/5/2015   Elaine           N/A			    Remove insertExcelDownloadStatus
 * 															and insertODBRole
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.lang.reflect.Field;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SettingInfo_;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId_;
import th.co.toyota.st3.api.model.SystemInfo_;
import th.co.toyota.st3.api.model.TableRoleMap;
import th.co.toyota.st3.api.model.TableRoleMap_;

/**
 * Repository implementation for excel download screen.
 * 
 * @author Manego
 * 
 */
@Repository
public class CST30900ExcelDownloadRepository implements IST30900ExcelDownloadRepository {

	/** A entity manager. */
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getTableList(java.util.List)
	 */
	@Override
	public List<TableRoleMap> getTableList(List<String> roleId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		
		Root<TableRoleMap> tableMap = cq.from(TableRoleMap.class);
		
		cq.select(cb.tuple(tableMap.get(TableRoleMap_.tableName).alias("tableName")
				,tableMap.get(TableRoleMap_.modelClassName).alias("modelClassName")))
					.distinct(true);
		
		Expression<String> exp = tableMap.get(TableRoleMap_.roleID);
		Predicate predicate = exp.in(roleId);
		cq.where(predicate).orderBy(cb.asc(tableMap.get(TableRoleMap_.tableName)));
		
		List<Tuple> tList = em.createQuery(cq).getResultList();
		List<TableRoleMap> list = null;
		
		if (tList != null) {
			list = new ArrayList<TableRoleMap>();
			for (Tuple t : tList) {
				TableRoleMap tableRm = new TableRoleMap();
				tableRm.setTableName((String) t.get("tableName"));
				tableRm.setModelClassName((String) t.get("modelClassName"));
				
				list.add(tableRm);
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getBookmarkList(java.util.List)
	 */
	@Override
	public List<SettingInfo> getBookmarkList(List<String> roleId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		
		Root<SettingInfo> info = cq.from(SettingInfo.class);
		
		cq.select(cb.tuple(info.get(SettingInfo_.settingID).alias("settingID"))).distinct(true);
		
		Expression<String> exp = info.get(SettingInfo_.roleID);
		Predicate predicate = exp.in(roleId);
		cq.where(predicate).orderBy(cb.asc(info.get(SettingInfo_.settingID)));
		
		List<Tuple> tList = em.createQuery(cq).getResultList();
		List<SettingInfo> list = null;
		if (tList != null) {
			list = new ArrayList<SettingInfo>();
			for (Tuple t: tList) {
				SettingInfo sInfo = new SettingInfo();
				sInfo.setSettingID((String) t.get("settingID"));
				
				list.add(sInfo);
			}
		}
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getBookmarkList(java.util.List, java.lang.String)
	 */
	//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	@Override
	public List<SettingInfo> getBookmarkList(List<String> roleId, String selectedTableName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		
		Root<SettingInfo> info = cq.from(SettingInfo.class);
		
		cq.select(cb.tuple(info.get(SettingInfo_.settingID).alias("settingID"))).distinct(true);

		List<Predicate> preds = new ArrayList<Predicate>();
		preds.add(info.get(SettingInfo_.roleID).in(roleId));
		if(selectedTableName != null && selectedTableName.trim().length() > 0){
			preds.add(cb.equal(info.get(SettingInfo_.tableName), selectedTableName));		
		}
		cq.where(preds.toArray(new Predicate[] {})).orderBy(cb.asc(info.get(SettingInfo_.settingID)));
		
		List<Tuple> tList = em.createQuery(cq).getResultList();
		List<SettingInfo> list = null;
		if (tList != null) {
			list = new ArrayList<SettingInfo>();
			for (Tuple t: tList) {
				SettingInfo sInfo = new SettingInfo();
				sInfo.setSettingID((String) t.get("settingID"));
				
				list.add(sInfo);
			}
		}
		return list;
	}
	//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getBookmarkDetails(java.util.List, java.lang.String)
	 */
	@Override
	public List<SettingInfo> getBookmarkDetails(List<String> roleId, String bookmarkId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		// START - 0002_SC2_UT_IS_20150914_002_Excel Download with Bookmark shows Double line at each fields
//		CriteriaQuery<SettingInfo> cq = cb.createQuery(SettingInfo.class);		
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<SettingInfo> bookmark = cq.from(SettingInfo.class);
		// Search condition
		List<Predicate> preds = new ArrayList<Predicate>();
		preds.add(bookmark.get(SettingInfo_.roleID).in(roleId));
		preds.add(cb.equal(bookmark.get(SettingInfo_.settingID), bookmarkId));

//		cq.select(bookmark);
		cq.select(cb.tuple(
				bookmark.get(SettingInfo_.fieldName).alias("fieldName"),
				bookmark.get(SettingInfo_.displayName).alias("displayName"),
				bookmark.get(SettingInfo_.orderDisp).alias("orderDisp"),
				bookmark.get(SettingInfo_.pkOption).alias("pkOption"),
				bookmark.get(SettingInfo_.dataType).alias("dataType"),
				bookmark.get(SettingInfo_.criteria).alias("criteria"),
				bookmark.get(SettingInfo_.logicalOpr).alias("logicalOpr"),
				bookmark.get(SettingInfo_.displayOption).alias("displayOption"),
				bookmark.get(SettingInfo_.sort).alias("sort"),
				bookmark.get(SettingInfo_.tableName).alias("tableName"),
				bookmark.get(SettingInfo_.settingID).alias("settingID"),
				bookmark.get(SettingInfo_.reportName).alias("reportName"),
				bookmark.get(SettingInfo_.reportTitle).alias("reportTitle"),
				bookmark.get(SettingInfo_.startRow).alias("startRow"),
				bookmark.get(SettingInfo_.startColumn).alias("startColumn"),
				bookmark.get(SettingInfo_.entryName).alias("entryName"),
				bookmark.get(SettingInfo_.objFieldName).alias("objFieldName")
				));
		cq.distinct(true);
		cq.where(preds.toArray(new Predicate[] {}));
		cq.orderBy(cb.asc(bookmark.get(SettingInfo_.orderDisp).as(Integer.class)));
		
//		return em.createQuery(cq).getResultList();
		List<SettingInfo> list = new ArrayList<SettingInfo>();
		List<Tuple> tList = em.createQuery(cq).getResultList();
		if (tList != null) {
			for (Tuple t : tList) {
				SettingInfo sInfo = new SettingInfo();
				sInfo.setFieldName((String)t.get("fieldName"));
				sInfo.setDisplayName((String)t.get("displayName"));
				sInfo.setOrderDisp((String)t.get("orderDisp"));
				sInfo.setPkOption((String)t.get("pkOption"));
				sInfo.setDataType((String)t.get("dataType"));
				sInfo.setCriteria((String)t.get("criteria"));
				sInfo.setLogicalOpr((String)t.get("logicalOpr"));
				sInfo.setDisplayOption((String)t.get("displayOption"));
				sInfo.setSort((String)t.get("sort"));
				sInfo.setTableName((String)t.get("tableName"));
				sInfo.setSettingID((String)t.get("settingID"));
				sInfo.setReportName((String)t.get("reportName"));
				sInfo.setReportTitle((String)t.get("reportTitle"));
				sInfo.setStartRow((String)t.get("startRow"));
				sInfo.setStartColumn((String)t.get("startColumn"));
				sInfo.setEntryName((String)t.get("entryName"));
				sInfo.setObjFieldName((String)t.get("objFieldName"));
				list.add(sInfo);
			}
		}
		return list;
		// END - 0002_SC2_UT_IS_20150914_002_Excel Download with Bookmark shows Double line at each fields
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#querySystemMaster(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SystemInfo> querySystemMaster(String category, String subCategory, String code) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.where(cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.category),category), 
				 cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory),subCategory),
				 cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.code),code),
				 cb.equal(sys.get(SystemInfo_.status), Character.valueOf(CST30000Constants.YES.charAt(0))));

		return em.createQuery(cq).getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#saveBookmark(java.util.List)
	 */
	@Override
	@Transactional(readOnly=false)
	public boolean saveBookmark(List<SettingInfo> listBookmark) throws EntityExistsException, IllegalArgumentException, TransactionRequiredException{
		boolean isResult = false;

		for(SettingInfo settingInfo : listBookmark) {
			em.persist(settingInfo);
		}
		isResult = true;
		
		return isResult;
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getMetadata(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<SettingInfo> getMetadata(String modelClassName) throws ClassNotFoundException, SQLSyntaxErrorException {
		List<SettingInfo> listMetaData = new ArrayList<SettingInfo>();
		List<String> listSelectField = new ArrayList<String>();
		Class myClass = Class.forName(modelClassName);
		StringBuffer sf = new StringBuffer("SELECT ");
		
		for (Field field : myClass.getSuperclass().getDeclaredFields()) {
			SettingInfo setting = getColumnInfo(field);
			if (setting != null) {
				listMetaData.add(setting);
				listSelectField.add(setting.getObjFieldName());
			}				
		}

		
		for (Field field : myClass.getDeclaredFields()) {
			
			EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
			if (embId != null) {
				Class emClass = field.getType();
				for (Field embedIdField : emClass.getDeclaredFields()) {
					SettingInfo setting = getColumnInfo(embedIdField);
					if (setting != null) {
						listMetaData.add(setting);
						listSelectField.add(field.getName() + "." +setting.getObjFieldName());
					}
				}
			} else {			
				SettingInfo setting = getColumnInfo(field);
				if (setting != null) {
					listMetaData.add(setting);
					listSelectField.add(setting.getObjFieldName());
				}	
			}
		}

		for(String tmp : listSelectField) {
			sf.append(tmp).append(",");
		}
		sf.delete(sf.length() -1, sf.length());
		sf.append(" FROM ").append(modelClassName);
		sf.append(" WHERE 1=0 ");	//get metadata only, no need to retrieve data
		System.out.println(sf);
		Query query = em.createQuery(sf.toString());
		List list = query.getResultList();
		System.out.println(list.size());
		
		return listMetaData;
	}
	
	/**
	 * Use to know the column information like column name, column length, data
	 * type.
	 * 
	 * @param field A cloumn/field name.
	 * @return {@link SettingInfo}
	 */
	public SettingInfo getColumnInfo(Field field) {
		SettingInfo setting = null;
		Column column = field.getAnnotation(Column.class);
		if (column != null) {
			setting = new SettingInfo();
			setting.setDisplayOption("1");
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				setting.setPkOption("PK");
			}
			
			setting.setFieldName(column.name());
			setting.setObjFieldName(field.getName());
			
			if (column.columnDefinition() != null && !column.columnDefinition().isEmpty()) {
				setting.setDataType(column.columnDefinition());
			} else {
				StringBuffer strType = new StringBuffer(field.getType().getSimpleName());
				
				int length = (int) AnnotationUtils.getDefaultValue(column, "length") == (int) AnnotationUtils.getValue(column, "length") ? 0 : column.length() ;
				int precison = column.precision();
				int scale = column.scale();
				
				if (length != 0) {
					strType.append("(").append(length).append(")");
				} else if (precison != 0 && scale != 0) {
					strType.append("(").append(precison).append(",").append(scale).append(")");
				}
				
				setting.setDataType(strType.toString());
			}
		}
		
		return setting;
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST30900ExcelDownloadRepository#getRecordCount(java.lang.String, java.lang.Object[], java.lang.String)
	 */
	@Override
	public int getRecordCount(String strQuery, Object[] params, String entryClassName) {
		int recordCount = 0;
		//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 20/5/2015
		Pattern pattern = Pattern.compile("SELECT(.+)FROM");
        Matcher matcher = pattern.matcher(strQuery);
        matcher.find();
        String group1 = matcher.group(1).trim();
        String countQuery = strQuery.replaceFirst(group1, "COUNT(*)");
        
		Query query = em.createQuery(countQuery);
		// set parameters
		if (params != null && params.length > 0) {
			for(int i=0; i<params.length; i++) {
				
				if(params[i] instanceof Date){
					query.setParameter(i+1, (Date) params[i], TemporalType.DATE);
				}else{
					query.setParameter(i+1, params[i]);
				}
			}
		}
		recordCount  = Integer.parseInt(query.getSingleResult().toString());
		//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 20/5/2015
		
//		query = em.createQuery(strQuery);
//		if (params != null && params.length > 0) {
//			for(int i=0; i<params.length; i++) {
//				
//				if(params[i] instanceof Date){
//					query.setParameter(i+1, (Date) params[i], TemporalType.DATE);
//				}else{
//					query.setParameter(i+1, params[i]);
//				}
//			}
//		}
//		recordCount = query.getResultList().size();

		return recordCount;
	}
	
	/*
	@Override
	@Transactional(readOnly=false)
	public void insertExcelDownloadStatus(ExcelDownloadStatus excel) throws EntityExistsException, IllegalArgumentException, TransactionRequiredException{
		em.persist(excel);		
	}
	
	@Override
	@Transactional(readOnly=false)
	public void insertODBRole(ODBRoles odbRole) throws EntityExistsException, IllegalArgumentException, TransactionRequiredException{
		em.persist(odbRole);		
	}
	*/
}

