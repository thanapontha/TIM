/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST33020ExcelDownloadMonitoringRepository.java
 * Program Description	    :  Excel download monitoring repository implementation.
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  Apr 28, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadFileId;
import th.co.toyota.st3.api.model.ExcelDownloadFileId_;
import th.co.toyota.st3.api.model.ExcelDownloadFile_;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;
import th.co.toyota.st3.api.model.ExcelDownloadStatus_;
import th.co.toyota.st3.api.model.ODBRoles;
import th.co.toyota.st3.api.model.ODBRoles_;

/**
 * Repository implementation for excel download monitoring screen.
 * 
 * @author Thanawut T.
 * 
 */
@Repository
@Transactional(readOnly = false)
public class CST33020ExcelDownloadMonitoringRepository implements
		IST33020ExcelDownloadMonitoringRepository {

	/** Entity manager instance. */
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	/** Admin role qualifier. */
	@Value("${default.admin.roledn}")
	private String strAdminRole;
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository#queryExcelDownloads()
	 */
	@Override
	public List<ExcelDownloadStatus> queryExcelDownloads() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExcelDownloadStatus> cq = cb
				.createQuery(ExcelDownloadStatus.class);

		Root<ExcelDownloadStatus> ret = cq.from(ExcelDownloadStatus.class);
		

		cq.select(ret);

		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #queryExcelDownloads(java.util.Date, java.util.Date, java.lang.Integer,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ExcelDownloadStatus> queryExcelDownloads(Date startRequestDate, 
			   Date endRequestDate,
			   Integer status,
			   String reportName,
			   String userId) {
		return this.queryExcelDownloads(startRequestDate, endRequestDate, status, reportName, null, userId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #queryExcelDownloads(java.util.Date, java.util.Date, java.lang.Integer,
	 * java.lang.String, java.util.List)
	 */
	@Override
	public List<ExcelDownloadStatus> queryExcelDownloads(Date startRequestDate, 
													   Date endRequestDate,
													   Integer status,
													   String reportName,
													   List<String> roleList) {
		
		return this.queryExcelDownloads(startRequestDate, endRequestDate, status, reportName, roleList, null);
		
	}

	/**
	 * Query for excel download status with or without user and user roles.
	 * 
	 * @param startRequestDate A start date
	 * @param endRequestDate A end date
	 * @param status A excel file status
	 * @param reportName A excel report name
	 * @param roleList User roles
	 * @param userId A user id
	 * @return List of {@link ExcelDownloadStatus}
	 */
	protected List<ExcelDownloadStatus> queryExcelDownloads(Date startRequestDate, 
													   Date endRequestDate,
													   Integer status,
													   String reportName,
													   List<String> roleList, 
													   String userId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExcelDownloadStatus> cq = cb.createQuery(ExcelDownloadStatus.class);

		Root<ExcelDownloadStatus> ret = cq.from(ExcelDownloadStatus.class);
		ret.join("excelDownloadFiles", JoinType.LEFT);

		cq.select(ret);
		
		List<Predicate> preds = new ArrayList<Predicate>();
		
		preds.add( cb.between(ret.get(ExcelDownloadStatus_.requestDt), startRequestDate, endRequestDate) );
		
		if(status != 0) {
			preds.add( cb.equal(ret.get(ExcelDownloadStatus_.status),status) );
		}
		
		if (!Strings.isNullOrEmpty(reportName)) {
			if (!reportName.contains("*")) {
				preds.add(cb.equal(cb.upper(ret.get(ExcelDownloadStatus_.reportName)),
											reportName.toUpperCase()));
			}else{
				preds.add(cb.like(cb.upper(ret.get(ExcelDownloadStatus_.reportName)),
											reportName.replace("*", "%").toUpperCase()));
			}
		}
		
		if (!Strings.isNullOrEmpty(userId)) {
			preds.add(cb.equal(cb.upper(ret.get(ExcelDownloadStatus_.requestBy)), userId.toUpperCase()));
		}
		
		List<ODBRoles> odbRolesList = null;
		if(!isAdmin(roleList) && (roleList != null) && !roleList.isEmpty()) {
			List<String> docIdRoleList = new ArrayList<String>();
			odbRolesList = this.findODBRoles(roleList);
			
			for(int i=0;i<odbRolesList.size();i++) {
				docIdRoleList.add(odbRolesList.get(i).getDocId());
			}
			preds.add(ret.get(ExcelDownloadStatus_.docId).in(docIdRoleList));
		}
		cq.where(preds.toArray(new Predicate[] {}));

		cq.orderBy(cb.desc(ret.get(ExcelDownloadStatus_.requestDt)),
				   cb.asc(ret.get(ExcelDownloadStatus_.docId)));
		
		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #findExcelDownloadFile(th.co.toyota.st3.api.model.ExcelDownloadFileId)
	 */
	@Override
	public ExcelDownloadFile findExcelDownloadFile(ExcelDownloadFileId excelDownloadFileId) {
		return em.find(ExcelDownloadFile.class, excelDownloadFileId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #findExcelDownloadFileWithDocId(java.lang.String)
	 */
	public List<ExcelDownloadFile> findExcelDownloadFileWithDocId(String docId) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<ExcelDownloadFile> cq = cb.createQuery(ExcelDownloadFile.class);

		Root<ExcelDownloadFile> ret = cq.from(ExcelDownloadFile.class);

		cq.select(ret);
		
		cq.where(cb.equal(ret.get(ExcelDownloadFile_.id).get(ExcelDownloadFileId_.docId), docId));
		
		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #deleteExcelDownload(th.co.toyota.st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public void deleteExcelDownload(ExcelDownloadFile excelDownloadInput) {

		Character status = excelDownloadInput.getStatus();
		ExcelDownloadFileId excelDownloadFileId = excelDownloadInput.getId();

		ExcelDownloadFile excelDownloadFile = this.findExcelDownloadFile(excelDownloadFileId);
		
		if(excelDownloadFile != null) {
			excelDownloadFile.setStatus(status);
			em.merge(excelDownloadFile);
		}

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #updateExcelDownloadStatus(th.co.toyota.st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public void updateExcelDownloadStatus(ExcelDownloadFile excelDownloadInput) {
		Character status = excelDownloadInput.getStatus();
		ExcelDownloadFileId excelDownloadFileId = excelDownloadInput.getId();
		String docId = excelDownloadFileId.getDocId();
		
		ExcelDownloadStatus excelDownloadStatus = em.find(ExcelDownloadStatus.class, docId);
		
		if(excelDownloadStatus != null) {
			excelDownloadStatus.setStatus(status);
			em.merge(excelDownloadStatus);
		}
		
		ExcelDownloadFile xlsFile = em.find(ExcelDownloadFile.class, excelDownloadFileId);
		
		if (xlsFile != null) {
			xlsFile.setStatus(status);
			em.merge(xlsFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #cancelExcelDownload(th.co.toyota.st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public void cancelExcelDownload(ExcelDownloadFile excelDownloadInput) {

		Character status = excelDownloadInput.getStatus();
		ExcelDownloadFileId excelDownloadFileId = excelDownloadInput.getId();
		
		ExcelDownloadFile excelDownloadFile = this.findExcelDownloadFile(excelDownloadFileId);
		
		if(excelDownloadFile != null) {
			excelDownloadFile.setStatus(status);
			em.merge(excelDownloadFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #findExcelDownloadStatus(java.lang.String)
	 */
	@Override
	public ExcelDownloadStatus findExcelDownloadStatus(String docId) {
		return em.find(ExcelDownloadStatus.class, docId);
	}

	/**
	 * List out ODB role based on given list of role id's.
	 * 
	 * @param roleList
	 *            A role list.
	 * @return List of {@link ODBRoles}
	 */
	public List<ODBRoles> findODBRoles(List<String> roleList) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<ODBRoles> cq = cb.createQuery(ODBRoles.class);

		Root<ODBRoles> ret = cq.from(ODBRoles.class);

		cq.select(ret);
		
		cq.distinct(true);
		
		cq.where((ret.get(ODBRoles_.roleId).in(roleList)));
		
		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #isAdmin(java.util.List)
	 */
	@Override
	public boolean isAdmin(List<String> roleList) {
		boolean isAdmin = false;
		
		if(roleList != null) {
			for (String role : roleList) {
				if(role.equals(strAdminRole)) {
					isAdmin = true;
					break;
				}
			}
		}
		return isAdmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #deleteExcelDownload(th.co.toyota.st3.api.model.ExcelDownloadStatus)
	 */
	@Override
	public void deleteExcelDownload(ExcelDownloadStatus excelStatus) {
		Character status = excelStatus.getStatus();
		
		ExcelDownloadStatus exStatus = this.findExcelDownloadStatus(excelStatus.getDocId());
				
		if(exStatus != null) {
			exStatus.setStatus(status);
			em.merge(exStatus);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository
	 * #findRolesForExcelDownloadStatus()
	 */
	@Override
	public List<ODBRoles> findRolesForExcelDownloadStatus(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<ODBRoles> cq = cb.createQuery(ODBRoles.class);

		Root<ODBRoles> ret = cq.from(ODBRoles.class);

		cq.select(ret);
		
		return em.createQuery(cq).getResultList();
	}
}