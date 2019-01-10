/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-webtemplate
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.repository
 * Program ID 	            :  CST33040BatchMasterRepository.java
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.model.BatchMaster;
import th.co.toyota.st3.api.model.BatchMasterId;
import th.co.toyota.st3.api.model.BatchMasterId_;
import th.co.toyota.st3.api.model.BatchMaster_;

import com.google.common.base.Strings;

/**
 * A repository implementation class use for batch master screen operations.
 * 
 * @author Manego
 * 
 */
@Repository
@Transactional(value = "bfw", readOnly = false)
public class CST33040BatchMasterRepository implements
		IST33040BatchMasterRepository {

	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory_bfw")
	private EntityManager em;

	final Logger logger = LoggerFactory
			.getLogger(CST33040BatchMasterRepository.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * queryBatchMasterPriority()
	 */
	@Override
	public List<String> queryBatchMasterPriority() {
		logger.debug("query for priority list.");
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<BatchMaster> refBatchMaster = cq.from(BatchMaster.class);

		cq.select(
				cb.tuple(refBatchMaster.get(BatchMaster_.priorityLevel).alias(
						"priorityLevel"))).distinct(true);

		cq.orderBy(cb.asc(refBatchMaster.get(BatchMaster_.id).get(
				BatchMasterId_.batchId)));

		final List<String> list = new ArrayList<String>();
		final List<Tuple> tList = em.createQuery(cq).getResultList();

		if (tList != null) {
			for (Tuple t : tList) {
				list.add((String) t.get("priorityLevel"));
			}
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * queryBatchMasterConcurrency()
	 */
	@Override
	public List<String> queryBatchMasterConcurrency() {
		logger.debug("query for concurrency list.");
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<BatchMaster> refBatchMaster = cq.from(BatchMaster.class);

		cq.select(
				cb.tuple(refBatchMaster.get(BatchMaster_.concurrencyFlag)
						.alias("concurrencyFlag"))).distinct(true);

		cq.orderBy(cb.asc(refBatchMaster.get(BatchMaster_.id).get(
				BatchMasterId_.batchId)));

		final List<String> list = new ArrayList<String>();
		final List<Tuple> tList = em.createQuery(cq).getResultList();

		if (tList != null) {
			for (Tuple t : tList) {
				list.add((String) t.get("concurrencyFlag"));
			}
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * queryBatchMaster(th.co.toyota.st3.api.model.BatchMaster)
	 */
	@Override
	public List<BatchMaster> queryBatchMaster(
			final BatchMaster batchMasterSearchCriteria) {
		logger.debug("query on batch master for given search criteria");
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<BatchMaster> cq = cb.createQuery(BatchMaster.class);
		final Root<BatchMaster> refBatchMaster = cq.from(BatchMaster.class);

		cq.select(refBatchMaster);
		cq.distinct(true);

		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(
				cb.upper(refBatchMaster.get(BatchMaster_.id).get(
						BatchMasterId_.projectCode)), batchMasterSearchCriteria
						.getId().getProjectCode().toUpperCase()));

		// search if batch id given.
		if (!Strings.isNullOrEmpty(batchMasterSearchCriteria.getId()
				.getBatchId())) {
			predicates.add(cb.equal(
					cb.upper(refBatchMaster.get(BatchMaster_.id).get(
							BatchMasterId_.batchId)), batchMasterSearchCriteria
							.getId().getBatchId().toUpperCase()));
		}

		// search if batch name provided in input.
		if (!Strings.isNullOrEmpty(batchMasterSearchCriteria.getBatchName())) {
			if (!batchMasterSearchCriteria.getBatchName().contains("*")) {
				predicates
						.add(cb.equal(cb.upper(refBatchMaster
								.get(BatchMaster_.batchName)),
								batchMasterSearchCriteria.getBatchName()
										.toUpperCase()));
			} else {
				predicates.add(cb.like(
						cb.upper(refBatchMaster.get(BatchMaster_.batchName)),
						batchMasterSearchCriteria.getBatchName()
								.replace("*", "%").toUpperCase()));
			}
		}

		// search if priority level provided in input
		if (batchMasterSearchCriteria.getPriorityLevel() > -1) {
			predicates.add(cb.equal(
					refBatchMaster.get(BatchMaster_.priorityLevel),
					batchMasterSearchCriteria.getPriorityLevel()));
		}

		// search if, concurrency flag define in input.
		if (batchMasterSearchCriteria.getConcurrencyFlag() > -1) {
			predicates.add(cb.equal(
					refBatchMaster.get(BatchMaster_.concurrencyFlag),
					batchMasterSearchCriteria.getConcurrencyFlag()));
		}

		if ((predicates != null) && (predicates.size() > 0)) {
			cq.where(predicates.toArray(new Predicate[] {}));
		}

		cq.orderBy(
				cb.asc(refBatchMaster.get(BatchMaster_.id).get(
						BatchMasterId_.projectCode)),
				cb.asc(refBatchMaster.get(BatchMaster_.id).get(
						BatchMasterId_.batchId)));

		return em.createQuery(cq).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * findBatchMasterInfo(java.lang.String)
	 */
	@Override
	public BatchMaster findBatchMasterInfo(final String projectCode,
			final String batchId) {
		logger.debug("find batch master object for given batch id, " + batchId);
		// return em.find(BatchMaster.class, batchId);

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<BatchMaster> cq = cb.createQuery(BatchMaster.class);
		Root<BatchMaster> refBatchMaster = cq.from(BatchMaster.class);

		cq.select(refBatchMaster);
		cq.distinct(true);

		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates
				.add(cb.equal(
						cb.upper(refBatchMaster.get(BatchMaster_.id).get(
								BatchMasterId_.projectCode)),
						projectCode.toUpperCase()));
		predicates.add(cb.equal(
				cb.upper(refBatchMaster.get(BatchMaster_.id).get(
						BatchMasterId_.batchId)), batchId.toUpperCase()));

		if ((predicates != null) && (predicates.size() > 0)) {
			cq.where(predicates.toArray(new Predicate[] {}));
		}

		return em.createQuery(cq).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * addBatchMaster(th.co.toyota.st3.api.model.BatchMaster)
	 */
	@Override
	@Transactional(value = "bfw", readOnly = false)
	public void addBatchMaster(final BatchMaster batchMasterInfo)
			throws SystemAlreadyExistsException {
		BatchMaster refBatchMaster = null;
		try {
			refBatchMaster = this.findBatchMasterInfo(batchMasterInfo.getId()
					.getProjectCode(), batchMasterInfo.getId().getBatchId());
		} catch (final NoResultException erdae) {
			logger.warn("NoResultException occured, but skip by operation.");
			refBatchMaster = null;
		}

		if (refBatchMaster != null) {
			logger.warn("table already contails details for given batch id & project code values.");
			throw new SystemAlreadyExistsException();
		}

		em.persist(batchMasterInfo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * updateBatchMaster(th.co.toyota.st3.api.model.BatchMaster)
	 */
	@Override
	public void updateBatchMaster(final BatchMaster batchMaster)
			throws ConcurrencyException, SystemDoesNotExistsException {
		try {
			logger.debug("updating batch master details");
			BatchMaster refBatchMaster = this
					.findBatchMasterInfo(batchMaster.getId().getProjectCode(),
							batchMaster.getId().getBatchId());

			if (refBatchMaster != null) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String updateDate = sdf.format(batchMaster.getUpdateDate());
				String refUpdateDate = sdf.format(refBatchMaster
						.getUpdateDate());

				if (updateDate.equalsIgnoreCase(refUpdateDate)) {
					refBatchMaster.setBatchName(batchMaster.getBatchName());
					refBatchMaster.setConcurrencyFlag(batchMaster
							.getConcurrencyFlag());
					refBatchMaster.setPriorityLevel(batchMaster
							.getPriorityLevel());
					refBatchMaster.setRunAs(batchMaster.getRunAs());
					refBatchMaster.setShell(batchMaster.getShell());
					refBatchMaster.setSupportId(batchMaster.getSupportId());
					refBatchMaster.setUpdateBy(batchMaster.getUpdateBy());
					refBatchMaster.setUpdateDate(Calendar.getInstance()
							.getTime());
					em.merge(refBatchMaster);
				} else {
					logger.warn("ERROR: Update date not matched");
					throw new ConcurrencyException("Update date not matched");
				}

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
	 * 
	 * @see th.co.toyota.application.repository.IST33040BatchMasterRepository#
	 * deleteBatchMaster(th.co.toyota.st3.api.model.BatchMaster)
	 */
	@Override
	public void deleteBatchMaster(BatchMaster batchMaster)
			throws ConcurrencyException, SystemDoesNotExistsException {
		try {
			logger.debug("deleting the bach master details.");
			BatchMaster refBatchMaster = this
					.findBatchMasterInfo(batchMaster.getId().getProjectCode(),
							batchMaster.getId().getBatchId());

			if (refBatchMaster != null) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String updateDate = sdf.format(batchMaster.getUpdateDate());
				String refUpdateDate = sdf.format(refBatchMaster
						.getUpdateDate());

				if (updateDate.equalsIgnoreCase(refUpdateDate)) {
					final Object id = new BatchMasterId(batchMaster.getId()
							.getBatchId(), batchMaster.getId().getProjectCode());
					BatchMaster delBatchMaster = em.getReference(
							BatchMaster.class, id);
					em.remove(delBatchMaster);
				} else {
					logger.warn("ERROR: Update date not matched");
					throw new ConcurrencyException("Update date not matched");
				}

			} else {
				logger.warn("ERROR: System already deleted");
				throw new SystemDoesNotExistsException();
			}

		} catch (final NoResultException erdae) {
			logger.warn("ERROR: System already deleted");
			throw new SystemDoesNotExistsException();
		}
	}
}
