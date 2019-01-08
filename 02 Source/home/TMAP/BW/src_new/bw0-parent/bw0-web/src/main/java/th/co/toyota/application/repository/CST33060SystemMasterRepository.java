/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST33060SystemMasterRepository.java
 * Program Description	    :  System master repository.
 * Environment	 	        :  Java 7
 * Author					:  LJ
 * Version					:  1.0
 * Creation Date            :  June 5, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.BaseEntity_;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.st3.api.model.SystemInfoId_;
import th.co.toyota.st3.api.model.SystemInfo_;

import com.google.common.base.Strings;

/**
 * A system master repository implementation.
 * 
 * @author LJ
 *
 */
@Repository
@Transactional(readOnly = false)
public class CST33060SystemMasterRepository implements IST33060SystemMasterRepository {

	/** A entity manager */
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	/** A file logger. */
	final Logger logger = LoggerFactory.getLogger(CST33060SystemMasterRepository.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * querySystemMasterInfoId()
	 */
	@Override
	public List<SystemInfoId> querySystemMasterInfoId() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(cb.tuple(
				sys.get(SystemInfo_.id).get(SystemInfoId_.category).alias("category"),
				sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory).alias("subCategory")))
				.distinct(true);

		cq.orderBy(cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.category))); 

		List<SystemInfoId> list = null;
		List<Tuple> tList = em.createQuery(cq).getResultList();

		if (tList != null) {
			list = new ArrayList<SystemInfoId>();
			for (Tuple t : tList) {
				SystemInfoId id = new SystemInfoId();
				id.setCategory((String) t.get("category"));
				id.setSubCategory((String) t.get("subCategory"));

				list.add(id);
			}
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * querySystemMasterInfo(th.co.toyota.st3.api.model.SystemInfoId)
	 */
	@Override
	public List<SystemInfo> querySystemMasterInfo(SystemInfoId sysInfoId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.distinct(true);

		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(cb.upper(sys.get(SystemInfo_.id).get(SystemInfoId_.category)),
				sysInfoId.getCategory().toUpperCase()));

		if (!Strings.isNullOrEmpty(sysInfoId.getSubCategory())) {
			predicates.add(cb.equal(cb.upper(sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory)),
					sysInfoId.getSubCategory().toUpperCase()));
		}
		if (!Strings.isNullOrEmpty(sysInfoId.getCode())) {
			if (!sysInfoId.getCode().contains("*")) {
				predicates.add(cb.equal(cb.upper(sys.get(SystemInfo_.id).get(SystemInfoId_.code)),
						sysInfoId.getCode().toUpperCase()));
			} else {
				predicates.add(cb.like(cb.upper(sys.get(SystemInfo_.id).get(SystemInfoId_.code)),
						sysInfoId.getCode().replace("*", "%").toUpperCase()));
			}
			
		}

		if ((predicates != null) && (predicates.size() > 0)) {
			cq.where(predicates.toArray(new Predicate[] {}));
		}
		
		cq.orderBy(cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.category)), 
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory)), 
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.code)));

		return em.createQuery(cq).getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * findSystemMasterInfo(th.co.toyota.st3.api.model.SystemInfoId)
	 */
	@Override
	public SystemInfo findSystemMasterInfo(SystemInfoId infoId) {
		return em.find(SystemInfo.class, infoId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * addSystemMasterInfo(th.co.toyota.st3.api.model.SystemInfo)
	 */
	@Override
	@Transactional(readOnly=false)
	public void addSystemMasterInfo(SystemInfo sysInfo) 
			throws SystemAlreadyExistsException {
		
		SystemInfo refSys = null;
		try {
			refSys = this.findSystemMasterInfo(sysInfo.getId());
		} catch(NoResultException erdae) {
			refSys = null;
		}
		
		if (refSys != null) {
			throw new SystemAlreadyExistsException();
		}
			
		em.persist(sysInfo);

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * updateSystemMasterInfo(th.co.toyota.st3.api.model.SystemInfo)
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateSystemMasterInfo(SystemInfo sysInfo) 
			throws ConcurrencyException, SystemDoesNotExistsException {
		
		try {

			SystemInfo refSys  = this.findSystemMasterInfo(sysInfo.getId());
		
			if (refSys != null) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String updateDate = sdf.format(sysInfo.getUpdateDate());
				String refUpdateDate = sdf.format(refSys.getUpdateDate());
				
				if (updateDate.equalsIgnoreCase(refUpdateDate)) {
					refSys.setValue(sysInfo.getValue());
					refSys.setRemark(sysInfo.getRemark());
					refSys.setStatus(sysInfo.getStatus());
					refSys.setUpdateBy(sysInfo.getUpdateBy());
					refSys.setUpdateDate(FormatUtil.currentTimestampToOracleDB());
					em.merge(refSys);
				} else {
					logger.debug("ERROR: Update date not matched");
					throw new ConcurrencyException("Update date not matched");
				}
				
			} else {
				logger.debug("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}
		} catch(NoResultException erdae) {
			logger.debug("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * deleteSystemMasterInfo(th.co.toyota.st3.api.model.SystemInfo)
	 */
	@Override
	@Transactional(readOnly=false)
	public void deleteSystemMasterInfo(SystemInfo sysInfo) throws ConcurrencyException, SystemDoesNotExistsException {

		try {
			
			SystemInfo refSys = this.findSystemMasterInfo(sysInfo.getId());

			if (refSys != null) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String updateDate = sdf.format(sysInfo.getUpdateDate());
				String refUpdateDate = sdf.format(refSys.getUpdateDate());
				
				if (updateDate.equalsIgnoreCase(refUpdateDate)) {
					SystemInfo refSysMaster = em.getReference(
							SystemInfo.class, sysInfo.getId());
					em.remove(refSysMaster);
				} else {
					logger.debug("ERROR: Update date not matched");
					throw new ConcurrencyException("Update date not matched");
				}
				
			} else {
				logger.debug("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}
			
			
		} catch(NoResultException erdae) {
			logger.debug("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * querySystemMasterOrderedCodeValue(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SystemInfo> querySystemMasterOrderedCodeValue(String category,
			String subCategory) throws NoDataFoundException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.where(cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.category),
				category), cb.equal(
				sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory),
				subCategory)).orderBy(cb.asc(sys.get(BaseEntity_.createDate)));

		final List<SystemInfo> systemInfoList = em.createQuery(cq).getResultList();
		if(systemInfoList == null || systemInfoList.isEmpty()){
			logger.debug("ERROR: no records found for given system id.");
			throw new NoDataFoundException();
		}
		
		return systemInfoList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33060SystemMasterRepository#
	 * querySystemMasterCodeValue(java.lang.String, java.lang.String)
	 */
	//TODO Used by log monitoring to retrieve data for combobox. This should be in common repository.
	@Override
	public List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.where(cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.category),
				category), cb.equal(
				sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory),
				subCategory));

		return em.createQuery(cq).getResultList();
	}
}