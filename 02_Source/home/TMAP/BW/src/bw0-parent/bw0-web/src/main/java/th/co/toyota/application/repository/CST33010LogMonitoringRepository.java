/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST33010LogMonitoringRepository.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 2, 2013
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
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;

import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.LogInfo_;
import th.co.toyota.st3.api.model.ModuleDetailInfo;
import th.co.toyota.st3.api.model.ModuleDetailInfo_;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo_;
import th.co.toyota.st3.api.model.ModuleId_;

/**
 * Repository implementation for log monitoring screen.
 * 
 * @author danilo
 * 
 */
@Repository
public class CST33010LogMonitoringRepository implements IST33010LogMonitoringRepository {
	/** JPA Entity manager instance. */
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33010LogMonitoringRepository#
	 * queryModules()
	 */
	@Override
	public List<ModuleHeaderInfo> queryModules() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ModuleHeaderInfo> cq = cb
				.createQuery(ModuleHeaderInfo.class);

		Root<ModuleHeaderInfo> mod = cq.from(ModuleHeaderInfo.class);

		cq.select(mod);
		cq.orderBy(cb.asc(mod.get(ModuleHeaderInfo_.moduleId)), cb.asc(mod.get(ModuleHeaderInfo_.moduleName)));

		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33010LogMonitoringRepository#queryLog
	 * (th.co.toyota.st3.api.model.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public List<LogInfo> queryLog(LogInfo logInfo, Date dateFrom, Date dateTo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LogInfo> cq = cb.createQuery(LogInfo.class);

		Root<LogInfo> log = cq.from(LogInfo.class);
		List<Predicate> preds = new ArrayList<Predicate>();

		List<Predicate> sqPreds = new ArrayList<Predicate>();

		if (!Strings.isNullOrEmpty(logInfo.getModuleId())) {
			preds.add(cb.equal(log.get(LogInfo_.moduleId),
					logInfo.getModuleId()));

		}

		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())) {
			preds.add(cb.equal(log.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
		}

		final String userId = logInfo.getCreateBy();
		if (!Strings.isNullOrEmpty(userId)) {
			if (!userId.contains("*")) {
				preds.add(cb.equal(cb.upper(log.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
			} else {
				preds.add(cb.like(
						cb.upper(cb.upper(log.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
			}
		}

		if (!Strings.isNullOrEmpty(logInfo.getAppId())) {
			preds.add(cb.equal(log.get(LogInfo_.appId), logInfo.getAppId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getMessageType())) {
			preds.add(cb.equal(log.get(LogInfo_.messageType),
					logInfo.getMessageType()));
		}

		LocalDate ldFrom = null;
		LocalDate ldTo = null;

		if (dateFrom != null) {
			ldFrom = new LocalDate(dateFrom);
		}

		if (dateTo != null) {
			ldTo = new LocalDate(dateTo);
		}

		if (ldFrom != null) {
			preds.add(cb.greaterThanOrEqualTo(log.get(LogInfo_.createDate),
					ldFrom.toDate()));
		}

		if (ldTo != null) {
			preds.add(cb.lessThan(log.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getStatus())) {
			if (!logInfo.isLogDetail()) {
				Subquery<Integer> sq = cq.subquery(Integer.class);
				Root<LogInfo> sqLog = sq.from(LogInfo.class);
				sq.select(cb.max(sqLog.get(LogInfo_.seqNo)));
				sq.groupBy(sqLog.get(LogInfo_.appId));
				sqPreds.addAll(preds);

				if (!logInfo.getStatus().equalsIgnoreCase("P")) {
					sqPreds.add(cb.equal(sqLog.get(LogInfo_.status), "E"));
					sqPreds.add(cb.equal(sqLog.get(LogInfo_.messageType),
							logInfo.getStatus()));
				}

				sq.where(sqPreds.toArray(new Predicate[] {}));

				preds.add(log.get(LogInfo_.seqNo).in(sq));
			} else { // if (!logInfo.isLogDetail()) {

				preds.add(cb.equal(log.get(LogInfo_.status),
						logInfo.getStatus()));

			}
		}

		cq.select(log);

		cq.where(preds.toArray(new Predicate[] {}));
//		cq.orderBy(cb.desc(log.get(LogInfo_.createDate)),
//				cb.desc(log.get(LogInfo_.seqNo)));
		cq.orderBy(cb.desc(log.get(LogInfo_.createDate)));

		return em.createQuery(cq).getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33010LogMonitoringRepository#queryLogGroup
	 * (th.co.toyota.st3.api.model.LogInfo, java.util.Date, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Integer> queryLogGroup(LogInfo logInfo, Date dateFrom, Date dateTo, 
			int firstResult, int rowsPerPage) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Tuple> cqg = cb.createQuery(Tuple.class);
		Root<LogInfo> logGroup = cqg.from(LogInfo.class);
		List<Predicate> groupPred = new ArrayList<Predicate>();
		
		Subquery<Integer> sq = cqg.subquery(Integer.class);
		Root<LogInfo> sqLog = sq.from(LogInfo.class);
		List<Predicate> sqFilter = new ArrayList<Predicate>();
		
		if (!Strings.isNullOrEmpty(logInfo.getModuleId())) {
			groupPred.add(cb.equal(logGroup.get(LogInfo_.moduleId),
					logInfo.getModuleId()));
			sqFilter.add(cb.equal(sqLog.get(LogInfo_.moduleId),
					logInfo.getModuleId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())) {
			groupPred.add(cb.equal(logGroup.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
			sqFilter.add(cb.equal(sqLog.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
		}

		final String userId = logInfo.getCreateBy();
		if (!Strings.isNullOrEmpty(userId)) {
			if (!userId.contains("*")) {
				groupPred.add(cb.equal(cb.upper(logGroup.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
				sqFilter.add(cb.equal(cb.upper(sqLog.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
			} else {
				groupPred.add(cb.like(
						cb.upper(cb.upper(logGroup.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
				sqFilter.add(cb.like(
						cb.upper(cb.upper(sqLog.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
			}
		}

		if (!Strings.isNullOrEmpty(logInfo.getAppId())) {
			groupPred.add(cb.equal(logGroup.get(LogInfo_.appId), logInfo.getAppId()));
			sqFilter.add(cb.equal(sqLog.get(LogInfo_.appId), logInfo.getAppId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getMessageType())) {
			groupPred.add(cb.equal(logGroup.get(LogInfo_.messageType),
					logInfo.getMessageType()));
			sqFilter.add(cb.equal(sqLog.get(LogInfo_.messageType),
					logInfo.getMessageType()));
		}

		LocalDate ldFrom = null;
		LocalDate ldTo = null;

		if (dateFrom != null) {
			ldFrom = new LocalDate(dateFrom);
		}

		if (dateTo != null) {
			ldTo = new LocalDate(dateTo);
		}

		if (ldFrom != null) {
			groupPred.add(cb.greaterThanOrEqualTo(logGroup.get(LogInfo_.createDate),
					ldFrom.toDate()));
			sqFilter.add(cb.greaterThanOrEqualTo(sqLog.get(LogInfo_.createDate),
					ldFrom.toDate()));
		}

		if (ldTo != null) {
			groupPred.add(cb.lessThan(logGroup.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
			sqFilter.add(cb.lessThan(sqLog.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getStatus())) {

			if (!logInfo.getStatus().equalsIgnoreCase("P")) {
				groupPred.add(cb.equal(logGroup.get(LogInfo_.status), "E"));
				groupPred.add(cb.equal(logGroup.get(LogInfo_.messageType),
						logInfo.getStatus()));
			} else {
				
				List<Predicate> sqPreds = new ArrayList<Predicate>();
//				Subquery<Integer> sq = cqg.subquery(Integer.class);
//				Root<LogInfo> sqLog = sq.from(LogInfo.class);
				sq.select(cb.max(sqLog.get(LogInfo_.seqNo)));
				sq.groupBy(sqLog.get(LogInfo_.appId));
//				sqPreds.addAll(groupPred);
				sqPreds.addAll(sqFilter);
				
				List<Predicate> sq1Preds = new ArrayList<Predicate>();
				Subquery<Integer> sq1 = cqg.subquery(Integer.class);
				Root<LogInfo> sq1Log = sq1.from(LogInfo.class);
				sq1.select(cb.max(sq1Log.get(LogInfo_.seqNo)));
				sq1Preds.add(cb.equal(sq1Log.get(LogInfo_.appId), 
						sqLog.get(LogInfo_.appId)));
				sq1.where(sq1Preds.toArray(new Predicate[] {}));
				
				sqPreds.add(sqLog.get(LogInfo_.seqNo).in(sq1));
				sqPreds.add(cb.notEqual(sqLog.get(LogInfo_.status), "E"));
				
				sq.where(sqPreds.toArray(new Predicate[] {}));
				groupPred.add(logGroup.get(LogInfo_.seqNo).in(sq));
			}
		}
		
		// START - INC_20151223_001_20151223-16
		List<Predicate> modPreds = new ArrayList<Predicate>();
		Subquery<String> sqMod= cqg.subquery(String.class);
		Root<ModuleHeaderInfo> rootMod = sqMod.from(ModuleHeaderInfo.class);
		sqMod.select(rootMod.get(ModuleHeaderInfo_.moduleId));
		modPreds.add(cb.equal(rootMod.get(ModuleHeaderInfo_.moduleId), 
				logGroup.get(LogInfo_.moduleId)));
		sqMod.where(modPreds.toArray(new Predicate[] {}));
		groupPred.add(cb.exists(sqMod));
		
		List<Predicate> funcPreds = new ArrayList<Predicate>();
		Subquery<String> sqFunc= cqg.subquery(String.class);
		Root<ModuleDetailInfo> rootFunc = sqFunc.from(ModuleDetailInfo.class);
		sqFunc.select(rootFunc.get(ModuleDetailInfo_.id).get(ModuleId_.functionId));
		funcPreds.add(cb.equal(rootFunc.get(ModuleDetailInfo_.id).get(ModuleId_.functionId), 
				logGroup.get(LogInfo_.functionId)));
		funcPreds.add(cb.equal(rootFunc.get(ModuleDetailInfo_.id).get(ModuleId_.moduleId), 
				logGroup.get(LogInfo_.moduleId)));
		sqFunc.where(funcPreds.toArray(new Predicate[] {}));
		groupPred.add(cb.exists(sqFunc));
		// END - INC_20151223_001_20151223-16
		
		cqg.multiselect(
				cb.max(logGroup.get(LogInfo_.seqNo)).alias("maxSeqNo"), 
				cb.min(logGroup.get(LogInfo_.seqNo)).alias("minSeqNo"));
		if (!groupPred.isEmpty()) {
			cqg.where(groupPred.toArray(new Predicate[] {}));
		}
		cqg.groupBy(logGroup.get(LogInfo_.appId));
		cqg.orderBy(cb.desc(cb.max(logGroup.get(LogInfo_.seqNo))));
		
		List<Tuple> result = null;
		if (firstResult == 0 && rowsPerPage == 0) {
			// no pagination
			result = em.createQuery(cqg).getResultList();
		} else {
			// with pagination
			TypedQuery<Tuple> entityQuery = em.createQuery(cqg);
			entityQuery = em.createQuery(cqg);
			entityQuery.setFirstResult(firstResult);
			entityQuery.setMaxResults(rowsPerPage);

			result = entityQuery.getResultList();
		}
		
		List<Integer> seqList = new ArrayList<Integer>();
		for (Tuple tmp : result) {
			seqList.add(Integer.parseInt(tmp.get("maxSeqNo").toString()));
			seqList.add(Integer.parseInt(tmp.get("minSeqNo").toString()));
		}
		// START - bug fix - startDate and endDate display is equal
//		return result;
		return seqList;
		// END - bug fix - startDate and endDate display is equal
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33010LogMonitoringRepository#queryHeaderLog
	 * (th.co.toyota.st3.api.model.LogInfo, java.util.Date, java.util.Date, List<Integer> )
	 */
	@Override
	public List<LogInfo> queryHeaderLog(LogInfo logInfo, Date dateFrom, Date dateTo, List<Integer> appIdList) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LogInfo> cq = cb.createQuery(LogInfo.class);

		Root<LogInfo> log = cq.from(LogInfo.class);
		List<Predicate> preds = new ArrayList<Predicate>();
		

		if (!Strings.isNullOrEmpty(logInfo.getModuleId())) {
			preds.add(cb.equal(log.get(LogInfo_.moduleId),
					logInfo.getModuleId()));

		}

		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())) {
			preds.add(cb.equal(log.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
		}

		final String userId = logInfo.getCreateBy();
		if (!Strings.isNullOrEmpty(userId)) {
			if (!userId.contains("*")) {
				preds.add(cb.equal(cb.upper(log.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
			} else {
				preds.add(cb.like(
						cb.upper(cb.upper(log.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
			}
		}

		if (!Strings.isNullOrEmpty(logInfo.getAppId())) {
			preds.add(cb.equal(log.get(LogInfo_.appId), logInfo.getAppId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getMessageType())) {
			preds.add(cb.equal(log.get(LogInfo_.messageType),
					logInfo.getMessageType()));
		}

		LocalDate ldFrom = null;
		LocalDate ldTo = null;

		if (dateFrom != null) {
			ldFrom = new LocalDate(dateFrom);
		}

		if (dateTo != null) {
			ldTo = new LocalDate(dateTo);
		}

		if (ldFrom != null) {
			preds.add(cb.greaterThanOrEqualTo(log.get(LogInfo_.createDate),
					ldFrom.toDate()));
		}

		if (ldTo != null) {
			preds.add(cb.lessThan(log.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
		}
		
		if (appIdList!=null && !appIdList.isEmpty()) {
			preds.add(log.get(LogInfo_.seqNo).in(appIdList));
		}
		
		cq.select(log);
		cq.where(preds.toArray(new Predicate[] {}));
		cq.orderBy(cb.desc(log.get(LogInfo_.createDate)),
				cb.desc(log.get(LogInfo_.seqNo)));
		
		return em.createQuery(cq).getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33010LogMonitoringRepository#queryLog
	 * (th.co.toyota.st3.api.model.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public int queryDetailCount(LogInfo logInfo, Date dateFrom, 
			Date dateTo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);

		Root<LogInfo> log = cq.from(LogInfo.class);
		List<Predicate> preds = new ArrayList<Predicate>();

		if (!Strings.isNullOrEmpty(logInfo.getModuleId())) {
			preds.add(cb.equal(log.get(LogInfo_.moduleId),
					logInfo.getModuleId()));

		}

		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())) {
			preds.add(cb.equal(log.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
		}

		final String userId = logInfo.getCreateBy();
		if (!Strings.isNullOrEmpty(userId)) {
			if (!userId.contains("*")) {
				preds.add(cb.equal(cb.upper(log.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
			} else {
				preds.add(cb.like(
						cb.upper(cb.upper(log.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
			}
		}

		if (!Strings.isNullOrEmpty(logInfo.getAppId())) {
			preds.add(cb.equal(log.get(LogInfo_.appId), logInfo.getAppId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getMessageType())) {
			preds.add(cb.equal(log.get(LogInfo_.messageType),
					logInfo.getMessageType()));
		}

		LocalDate ldFrom = null;
		LocalDate ldTo = null;

		if (dateFrom != null) {
			ldFrom = new LocalDate(dateFrom);
		}

		if (dateTo != null) {
			ldTo = new LocalDate(dateTo);
		}

		if (ldFrom != null) {
			preds.add(cb.greaterThanOrEqualTo(log.get(LogInfo_.createDate),
					ldFrom.toDate()));
		}

		if (ldTo != null) {
			preds.add(cb.lessThan(log.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getStatus())) {
			preds.add(cb.equal(log.get(LogInfo_.status),
					logInfo.getStatus()));
		}

		cq.select(cb.count(log));
		cq.where(preds.toArray(new Predicate[] {}));

		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.repository.IST33010LogMonitoringRepository#queryLog
	 * (th.co.toyota.st3.api.model.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public List<LogInfo> queryDetailLog(LogInfo logInfo, Date dateFrom, Date dateTo, 
			int firstResult, int rowsPerPage) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LogInfo> cq = cb.createQuery(LogInfo.class);

		Root<LogInfo> log = cq.from(LogInfo.class);
		List<Predicate> preds = new ArrayList<Predicate>();

		if (!Strings.isNullOrEmpty(logInfo.getModuleId())) {
			preds.add(cb.equal(log.get(LogInfo_.moduleId),
					logInfo.getModuleId()));

		}

		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())) {
			preds.add(cb.equal(log.get(LogInfo_.functionId),
					logInfo.getFunctionId()));
		}

		final String userId = logInfo.getCreateBy();
		if (!Strings.isNullOrEmpty(userId)) {
			if (!userId.contains("*")) {
				preds.add(cb.equal(cb.upper(log.get(LogInfo_.createBy)),
						logInfo.getCreateBy().toUpperCase()));
			} else {
				preds.add(cb.like(
						cb.upper(cb.upper(log.get(LogInfo_.createBy))), userId
								.replace("*", "%").toUpperCase()));
			}
		}

		if (!Strings.isNullOrEmpty(logInfo.getAppId())) {
			preds.add(cb.equal(log.get(LogInfo_.appId), logInfo.getAppId()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getMessageType())) {
			preds.add(cb.equal(log.get(LogInfo_.messageType),
					logInfo.getMessageType()));
		}

		LocalDate ldFrom = null;
		LocalDate ldTo = null;

		if (dateFrom != null) {
			ldFrom = new LocalDate(dateFrom);
		}

		if (dateTo != null) {
			ldTo = new LocalDate(dateTo);
		}

		if (ldFrom != null) {
			preds.add(cb.greaterThanOrEqualTo(log.get(LogInfo_.createDate),
					ldFrom.toDate()));
		}

		if (ldTo != null) {
			preds.add(cb.lessThan(log.get(LogInfo_.createDate), ldTo
					.plusDays(1).toDate()));
		}

		if (!Strings.isNullOrEmpty(logInfo.getStatus())) {
			preds.add(cb.equal(log.get(LogInfo_.status),
					logInfo.getStatus()));
		}

		cq.select(log);
		cq.where(preds.toArray(new Predicate[] {}));
		
		//cq.orderBy(cb.desc(log.get(LogInfo_.createDate)),
		//cb.desc(log.get(LogInfo_.seqNo)));
		cq.orderBy(cb.desc(cb.function("trunc", Date.class , log.get(LogInfo_.createDate))),
		cb.asc(log.get(LogInfo_.seqNo)));
		
		List<LogInfo> result = null;
		if (firstResult == 0 && rowsPerPage == 0) {
			// no pagination
			result = em.createQuery(cq).getResultList();
		} else {
			// with pagination
			TypedQuery<LogInfo> entityQuery = em.createQuery(cq);
			entityQuery = em.createQuery(cq);
			entityQuery.setFirstResult(firstResult);
			entityQuery.setMaxResults(rowsPerPage);

			result = entityQuery.getResultList();
		}

//		return em.createQuery(cq).getResultList();
		return result;
	}
	
}
