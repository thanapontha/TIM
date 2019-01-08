/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST33050BatchStatusRepository.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Manego
 * Version					:  1.0
 * Creation Date            :  June 5, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.BatchMaster;
import th.co.toyota.st3.api.model.BatchMasterId_;
import th.co.toyota.st3.api.model.BatchMaster_;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.BatchQueue_;
import th.co.toyota.st3.api.model.BatchStatusLog;
import th.co.toyota.st3.api.model.BatchStatusLogId;
import th.co.toyota.st3.api.model.BatchStatusLogId_;
import th.co.toyota.st3.api.model.BatchStatusLog_;

import com.google.common.base.Strings;

/**
 * A repository implementation class use for batch master screen operations.
 * 
 * @author Manego
 * 
 */
@Repository
@Transactional(value = "bfw", readOnly = false)
public class CST33050BatchStatusRepository implements
		IST33050BatchStatusRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory_bfw")
	private EntityManager em;

	final Logger logger = LoggerFactory
			.getLogger(CST33050BatchStatusRepository.class);
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#
	 * queryBatchStatusLog(java.util.Map)
	 */
	@Override
	public List<Map<String, Object>> queryBatchStatusLog(
			Map<String, Object> batchStatusSearchCriteria){
		final String projectCode = (String) batchStatusSearchCriteria.get("projectCode");
		final Date runDate = (Date) batchStatusSearchCriteria.get("runDate");
		if (batchStatusSearchCriteria.isEmpty() || Strings.isNullOrEmpty(projectCode)
				|| runDate == null) {
			logger.debug("do not have valid input to perform the batch status search");
			throw new IllegalArgumentException();
		}
		
		final String batchId = (String) batchStatusSearchCriteria.get("batchId");
		final String requestBy = (String) batchStatusSearchCriteria.get("requestBy");
		final String batchName = (String) batchStatusSearchCriteria.get("batchName");
				
		final CriteriaQuery<Tuple> cqBatchStatus = buildQueryToFindBatchStatusLog(projectCode, runDate, batchId, requestBy, batchName);
		final CriteriaQuery<Tuple> cqBatchQueue = buildQueryToFindBatchQueue(projectCode, runDate, batchId, requestBy, batchName);
		
		final List<Tuple> resultList = new ArrayList<Tuple>();
		resultList.addAll(em.createQuery(cqBatchStatus).getResultList());
		resultList.addAll(em.createQuery(cqBatchQueue).getResultList());
		
		final List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
		
		for (final Tuple tuple : resultList) {
			final Map<String, Object> resultMap = convertTupleToMap(tuple);
			
			resultMapList.add(resultMap);
		}
		
		sortMapList(resultMapList, "batchNo");
		sortMapList(resultMapList, "requestDate", "runDate");
		return Collections.unmodifiableList(resultMapList);
	}

	/**
	 * build query to get the result from batch status log for given search criteria.
	 * 
	 * @param projectCode A project code
	 * @param runDate Batch run date.
	 * @param batchId Batch id.
	 * @param requestBy Requested user id.
	 * @param batchName Batch name
	 * @return A @{link CriteriaQuery}
	 */
	private CriteriaQuery<Tuple> buildQueryToFindBatchStatusLog(final String projectCode,
			final Date runDate, final String batchId, final String requestBy, final String batchName) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cqBatchStatus = cb.createQuery(Tuple.class);
		final Root<BatchMaster> refBatchMaster = cqBatchStatus.from(BatchMaster.class);
		final Root<BatchStatusLog> refBatchStatus = cqBatchStatus.from(BatchStatusLog.class);
		
		cqBatchStatus.multiselect(refBatchStatus.get(BatchStatusLog_.appId).alias("appId"),
				refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.projectCode).alias("projectCode"), 
				refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId).alias("batchId"), 
				refBatchMaster.get(BatchMaster_.batchName).alias("batchName"),
				refBatchStatus.get(BatchStatusLog_.supportId).alias("supportId"), 
				refBatchStatus.get(BatchStatusLog_.description).alias("description"),
				refBatchStatus.get(BatchStatusLog_.message).alias("status"), 
				refBatchStatus.get(BatchStatusLog_.requestBy).alias("requestBy"),
				//cb.nullLiteral(Date.class).alias("requestDate"), 
				refBatchStatus.get(BatchStatusLog_.runDate).alias("runDate"),
				refBatchStatus.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo).alias("batchNo"));
		cqBatchStatus.distinct(true);
		
		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(cb.upper(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.projectCode)), projectCode.toUpperCase()));
		predicates.add(cb.between(refBatchStatus.get(BatchStatusLog_.runDate), runDate, new Date(runDate.getTime() + CST30000Constants.TOTAL_HOURS_IN_DAY)));
		predicates.add(cb.equal(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId), refBatchStatus.get(BatchStatusLog_.batchId)));
		
		// create sub query to display only the latest status message for each batch, order of status (from highest to lowest).
		final Subquery<Integer> sq = cqBatchStatus.subquery(Integer.class);
		final Root<BatchStatusLog> s2 = sq.from(BatchStatusLog.class);
		sq.select(cb.max(s2.get(BatchStatusLog_.id).get(BatchStatusLogId_.groupSeqNo)));
		sq.where(cb.equal(s2.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo), refBatchStatus.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo)));
		predicates.add(cb.and(cb.equal(refBatchStatus.get(BatchStatusLog_.id).get(BatchStatusLogId_.groupSeqNo), sq)));
				
		// search if batch id given.
		if (!Strings.isNullOrEmpty(batchId)) {
			predicates.add(cb.equal(cb.upper(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId)),
					batchId.toUpperCase()));
		}

		// search if batch name provided in input.
		if (!Strings.isNullOrEmpty(requestBy)) {
			if (!requestBy.contains("*")) {
				predicates
						.add(cb.equal(cb.upper(refBatchStatus
								.get(BatchStatusLog_.requestBy)), requestBy
								.toUpperCase()));
			} else {
				predicates
						.add(cb.like(cb.upper(refBatchStatus
								.get(BatchStatusLog_.requestBy)), requestBy
								.replace("*", "%").toUpperCase()));
			}
		}

		// search if batch name provided in input.
		if (!Strings.isNullOrEmpty(batchName)) {
			if (!batchName.contains("*")) {
				predicates.add(cb.equal(
						cb.upper(refBatchMaster.get(BatchMaster_.batchName)),
						batchName.toUpperCase()));
			} else {
				predicates.add(cb.like(
						cb.upper(refBatchMaster.get(BatchMaster_.batchName)),
						batchName.replace("*", "%").toUpperCase()));
			}
		}
		
		cqBatchStatus.where(predicates.toArray(new Predicate[] {}));
		cqBatchStatus.orderBy(cb.desc(refBatchStatus.get(BatchStatusLog_.runDate)),
				cb.asc(refBatchStatus.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo)));
		
		return cqBatchStatus;
	}
	
	/**
	 * build query to get the result from batch queue for given search criteria.
	 * 
	 * @param projectCode A project code.
	 * @param runDate A batch run date.
	 * @param batchId A batch id.
	 * @param requestBy A requested user id.
	 * @param batchName A batch name.
	 * @return A {@link CriteriaQuery}
	 */
	private final CriteriaQuery<Tuple> buildQueryToFindBatchQueue(final String projectCode,
			final Date runDate, final String batchId, final String requestBy, final String batchName) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cqBatchQueue = cb.createQuery(Tuple.class);
		final Root<BatchMaster> refBatchMaster = cqBatchQueue.from(BatchMaster.class);
		final Root<BatchQueue> refBatchQueue = cqBatchQueue.from(BatchQueue.class);
		
		cqBatchQueue.multiselect(refBatchQueue.get(BatchQueue_.appId).alias("appId"),
				refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.projectCode).alias("projectCode"), 
				refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId).alias("batchId"), 
				refBatchMaster.get(BatchMaster_.batchName).alias("batchName"),
				refBatchQueue.get(BatchQueue_.supportId).alias("supportId"), 
				refBatchQueue.get(BatchQueue_.description).alias("description"),
				cb.literal(CST30000Constants.BATCH_STATUS_IN_QUEUE).alias("status"), 
				refBatchQueue.get(BatchQueue_.requestBy).alias("requestBy"),
				refBatchQueue.get(BatchQueue_.requestDate).alias("requestDate"),
				//cb.nullLiteral(Date.class).alias("runDate"),
				cb.toBigDecimal(refBatchQueue.get(BatchQueue_.queueNo)).alias("batchNo"));
		cqBatchQueue.distinct(true);
		
		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(cb.upper(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.projectCode)), projectCode.toUpperCase()));
		predicates.add(cb.between(refBatchQueue.get(BatchQueue_.requestDate), runDate, new Date(runDate.getTime() + (24 *60 *60000) - 1)));
		predicates.add(cb.equal(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId), refBatchQueue.get(BatchQueue_.batchId)));
		
		// search if batch id given.
		if (!Strings.isNullOrEmpty(batchId)) {
			predicates.add(cb.equal(cb.upper(refBatchMaster.get(BatchMaster_.id).get(BatchMasterId_.batchId)),
					batchId.toUpperCase()));
		}

		// search if batch name provided in input.
		if (!Strings.isNullOrEmpty(requestBy)) {
			if (!requestBy.contains("*")) {
				predicates.add(cb.equal(cb.upper(refBatchQueue.get(BatchQueue_.requestBy)),
						requestBy.toUpperCase()));
			} else {
				predicates.add(cb.like(
						cb.upper(refBatchQueue.get(BatchQueue_.requestBy)),
						requestBy.replace("*", "%").toUpperCase()));
			}
		}

		// search if batch name provided in input.
		if (!Strings.isNullOrEmpty(batchName)) {
			if (!batchName.contains("*")) {
				predicates.add(cb.equal(
						cb.upper(refBatchMaster.get(BatchMaster_.batchName)),
						batchName.toUpperCase()));
			} else {
				predicates.add(cb.like(
						cb.upper(refBatchMaster.get(BatchMaster_.batchName)),
						batchName.replace("*", "%").toUpperCase()));
			}
		}
				
		cqBatchQueue.where(predicates.toArray(new Predicate[] {}));
		cqBatchQueue.orderBy(cb.desc(refBatchQueue.get(BatchQueue_.requestDate)),
				cb.asc(refBatchQueue.get(BatchQueue_.queueNo)));
		
		return cqBatchQueue;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#
	 * findBatchStatusLog(th.co.toyota.st3.api.model.BatchStatusLogId)
	 */
	@Override
	public BatchStatusLog findLatestBatchStatusLog(final String batchId, final Integer seqNo) {
		logger.debug("find batch status log for given seqNo & batch id");
		try{
			// first build the query to fetch the batch status log.
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<BatchStatusLog> cq = cb.createQuery(BatchStatusLog.class);
			Root<BatchStatusLog> refBatchStatusLog = cq.from(BatchStatusLog.class);
			
			cq.select(refBatchStatusLog);
			cq.distinct(true);
	
			// define the other conditions
			final List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(cb.equal(refBatchStatusLog.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo), seqNo));
			predicates.add(cb.equal(cb.upper(refBatchStatusLog.get(BatchStatusLog_.batchId)), batchId.toUpperCase()));
	
			// create sub query to display only the latest status message for each batch, order of status (from highest to lowest)
			final Subquery<Integer> sq = cq.subquery(Integer.class);
			final Root<BatchStatusLog> s2 = sq.from(BatchStatusLog.class);
			sq.select(cb.max(s2.get(BatchStatusLog_.id).get(BatchStatusLogId_.groupSeqNo)));
			sq.where(cb.equal(s2.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo), refBatchStatusLog.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo)));
			predicates.add(cb.equal(refBatchStatusLog.get(BatchStatusLog_.id).get(BatchStatusLogId_.groupSeqNo), sq));
					
			if ((predicates != null) && (predicates.size() > 0)) {
				cq.where(predicates.toArray(new Predicate[] {}));
			}
			
			return em.createQuery(cq).getSingleResult();
		}catch(NoResultException | EmptyResultDataAccessException ex) {
			logger.warn("NoResultException exception occured, but it skip by service");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#findBatchQueueInfo(java.lang.String, java.lang.Integer)
	 */
	@Override
	public BatchQueue findBatchQueueInfo(final String batchId,
			final Integer queueNo) {
		logger.debug("find batch queue object for given queue number");
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<BatchQueue> cq = cb.createQuery(BatchQueue.class);
		final Root<BatchQueue> refBatchQueue = cq.from(BatchQueue.class);

		cq.select(refBatchQueue);
		cq.distinct(true);

		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates
				.add(cb.equal(refBatchQueue.get(BatchQueue_.queueNo), queueNo));
		predicates.add(cb.equal(
				cb.upper(refBatchQueue.get(BatchQueue_.batchId)),
				batchId.toUpperCase()));

		if ((predicates != null) && (predicates.size() > 0)) {
			cq.where(predicates.toArray(new Predicate[] {}));
		}

		try{
			return em.createQuery(cq).getSingleResult();
		}catch(NoResultException | EmptyResultDataAccessException ex) {
			logger.warn("NoResultException exception occured, but it skip by service");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#
	 * cancelBatchStatus(th.co.toyota.st3.api.model.BatchStatusLog)
	 */
	@Override
	public void cancelBatchStatus(final BatchQueue batchQueue)
			throws SystemDoesNotExistsException {
		try {
			logger.debug("deleting batch queue details");
			final String batchId = batchQueue.getBatchId();
			final int queueNumber = batchQueue.getQueueNo();
			final BatchQueue refBatchQueue = this.findBatchQueueInfo(batchId, queueNumber);

			if (refBatchQueue != null) {
				final BatchQueue cancelBatchStatus = em.getReference(
						BatchQueue.class, queueNumber);
				em.remove(cancelBatchStatus);
			} else {
				logger.warn("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}
		} catch (final NoResultException erdae) {
			logger.warn("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		} catch (final Exception ex) {
			logger.warn("ERROR: System already deleted");
			throw new RuntimeException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#
	 * deleteBatchStatus(th.co.toyota.st3.api.model.BatchStatusLog)
	 */
	@Override
	public void deleteBatchStatus(BatchStatusLog batchStatusLog)
			throws ConcurrencyException, SystemDoesNotExistsException {
		try {
			logger.debug("deleting the btach status details.");
			final BatchStatusLogId id = batchStatusLog.getId();
			final String batchId = batchStatusLog.getBatchId();
			final int seqNo = id.getSeqNo().intValue();
			final BatchStatusLog refBatchStatusLog = this.findLatestBatchStatusLog(
					batchId, seqNo);

			if (refBatchStatusLog != null) {
				final BatchStatusLog delBatchStatus = em.getReference(
						BatchStatusLog.class, refBatchStatusLog.getId());
				em.remove(delBatchStatus);
			} else {
				logger.warn("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}

		} catch (final NoResultException erdae) {
			logger.warn("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#deleteBatchStatus(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void deleteBatchStatus(final String batchId, final Integer seqNo)
			throws SystemDoesNotExistsException{
		try{
			// build the query first to get the batch log details.
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<BatchStatusLog> cq = cb.createQuery(BatchStatusLog.class);
			Root<BatchStatusLog> refBatchStatusLog = cq.from(BatchStatusLog.class);
			
			cq.select(refBatchStatusLog);
			cq.distinct(true);
	
			// define the other conditions
			final List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(cb.equal(refBatchStatusLog.get(BatchStatusLog_.id).get(BatchStatusLogId_.seqNo), seqNo));
			predicates.add(cb.equal(cb.upper(refBatchStatusLog.get(BatchStatusLog_.batchId)), batchId.toUpperCase()));
	
			if ((predicates != null) && (predicates.size() > 0)) {
				cq.where(predicates.toArray(new Predicate[] {}));
			}
		
			// get the batch log details.
			final List<BatchStatusLog> resultList = em.createQuery(cq).getResultList();
			if(resultList == null || resultList.isEmpty()){
				logger.warn("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}
			
			// delete all batch log details.
			for(final BatchStatusLog delBatchStatus : resultList){
				em.remove(delBatchStatus);
			}
		} catch (final NoResultException erdae) {
			logger.warn("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.repository.IST33050BatchStatusRepository#addBatchQueue(th.co.toyota.st3.api.model.BatchQueue)
	 */
	@Override
	public int addBatchQueue(final BatchQueue batchQueueInfo)
			throws SystemAlreadyExistsException {
		BatchQueue refBatchQueue = null;
		try {
			refBatchQueue = this.findBatchQueueInfo(batchQueueInfo.getBatchId(), batchQueueInfo.getQueueNo());
		} catch (final NoResultException erdae) {
			logger.warn("NoResultException occured, but skip by operation.");
			refBatchQueue = null;
		}

		if (refBatchQueue != null) {
			logger.warn("table already contails details for given batchQueueInfo values.");
			throw new SystemAlreadyExistsException();
		}

		final BatchQueue newBatchQueue = em.merge(batchQueueInfo);
		return newBatchQueue.getQueueNo();
	}
	
	@Override
	public void addBatchLog(final BatchStatusLog batchStatusLog)
			throws SystemAlreadyExistsException {
		BatchStatusLog refBatchStatusLog = null;
		try {
			refBatchStatusLog = em.find(BatchStatusLog.class, batchStatusLog.getId());
		} catch (final NoResultException erdae) {
			logger.warn("NoResultException occured, but skip by operation.");
			refBatchStatusLog = null;
		}

		if (refBatchStatusLog != null) {
			logger.warn("table already contails details for given batchStatusLog values.");
			throw new SystemAlreadyExistsException();
		}

		em.persist(batchStatusLog);
	}
	
	/**
	 * operation use to convert given Tuple object to Map instance. it will read
	 * the alias defination from the tuple and set same alias as the map key.
	 * 
	 * @param tuple
	 *            - input tuple instance.
	 * @return - return the map.
	 */
	private Map<String, Object> convertTupleToMap(final Tuple tuple) {
		final Map<String, Object> map = new HashMap<String, Object>();
		for (TupleElement<?> element : tuple.getElements()) {
			map.put(element.getAlias(), tuple.get(element.getAlias()));
		}

		return map;
	}

	/**
	 * operation us to sort the map list by given key.
	 * 
	 * @param listMap
	 *            Input map list
	 * @param keys
	 *            Key to sort the map.
	 */
	@SuppressWarnings("all")
	private void sortMapList(final List<Map<String, Object>> listMap,
			final String... keys) {
		Collections.sort(listMap, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (keys != null && keys.length > 0) {
					Object value1 = o1.get(keys[0]);
					Object value2 = keys.length == 2 && o2.get(keys[1]) != null ? o2
							.get(keys[1]) : o2.get(keys[0]);

					if (value1 == null)
						return 0;
					if (value2 == null)
						return 0;

					if (value1.getClass() != value2.getClass()) {
						if (value1 instanceof Number) {
							value1 = ((Number) value1).longValue();
						}

						if (value2 instanceof Number) {
							value2 = ((Number) value2).longValue();
						}
					}

					if (value1 instanceof Comparable
							&& value2 instanceof Comparable)
						return ((Comparable) value1)
								.compareTo(((Comparable) value2));
				}

				return 0;
			}
		});
	}
}
