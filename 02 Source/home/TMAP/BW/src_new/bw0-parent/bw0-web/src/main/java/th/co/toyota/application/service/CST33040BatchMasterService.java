/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST33040BatchMasterService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  manego
 * Version					:  1.0
 * Creation Date            :  June 5, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import th.co.toyota.application.repository.IST33040BatchMasterRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.BatchMaster;
import th.co.toyota.st3.api.model.BatchMasterId;

@Service
public class CST33040BatchMasterService implements IST33040BatchMasterService {
	final Logger logger = LoggerFactory
			.getLogger(CST33040BatchMasterService.class);

	@Autowired
	private IST33040BatchMasterRepository batchMasterRepository;

	@Value("${templateFileBatchMaster}")
	private String batchMasterExcelTemplate;

	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * retrieveBatchMasterPriority()
	 */
	@Override
	public Map<Integer, String> retrieveBatchMasterPriority() {
		// return batchMasterRepository.queryBatchMasterPriority();
		Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(CST30000Constants.ZERO_CODE, CST30000Constants.PRIORITY_ONE);
		map.put(CST30000Constants.ONE_CODE, CST30000Constants.PRIORITY_TWO);
		map.put(CST30000Constants.TWO_CODE, CST30000Constants.PRIORITY_THREE);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * retrieveBatchMasterConcurrency()
	 */
	@Override
	public Map<Integer, String> retrieveBatchMasterConcurrency() {
		// return batchMasterRepository.queryBatchMasterConcurrency();

		Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(CST30000Constants.ZERO_CODE, CST30000Constants.CONCURRENCY_TRUE);
		map.put(CST30000Constants.ONE_CODE, CST30000Constants.CONCURRENCY_FALSE);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33040BatchMasterService#getBatchMaster
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public BatchMaster getBatchMaster(final String projectCode,
			final String batchId) {
		try {
			return batchMasterRepository.findBatchMasterInfo(projectCode,
					batchId);
		} catch (final NoResultException | EmptyResultDataAccessException ex) {
			logger.warn("NoResultException exception occured, but it skip by service");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33040BatchMasterService#listBatchInfo
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<BatchMaster> listBatchInfo(final String batchId,
			final String batchName, final String projectCode,
			final int priorityLevel, final int concurrencyFlag) {
		logger.debug("service invoke to find the batch master details");
		final BatchMaster batchMasterSearchCriteria = new BatchMaster();
		final BatchMasterId id = new BatchMasterId(batchId, projectCode);
		batchMasterSearchCriteria.setId(id);
		batchMasterSearchCriteria.setBatchName(batchName);
		batchMasterSearchCriteria.setPriorityLevel(priorityLevel);
		batchMasterSearchCriteria.setConcurrencyFlag(concurrencyFlag);

		return batchMasterRepository
				.queryBatchMaster(batchMasterSearchCriteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * addBatchMasterInfo(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addBatchMasterInfo(final String batchId,
			final String projectCode, final String batchName,
			final int priorityLevel, final int concurrencyFlag,
			final int runningCount, final String runAs, final String shell,
			final String supportId, final String userId)
			throws SystemAlreadyExistsException {
		logger.debug("service invoke to add the given batch master details");
		final BatchMaster batchMasterInfo = new BatchMaster();
		final BatchMasterId id = new BatchMasterId(batchId, projectCode);
		batchMasterInfo.setId(id);
		batchMasterInfo.setBatchName(batchName);
		batchMasterInfo.setPriorityLevel(priorityLevel);
		batchMasterInfo.setConcurrencyFlag(concurrencyFlag);
		batchMasterInfo.setRunningCount(runningCount);
		batchMasterInfo.setRunAs(runAs);
		batchMasterInfo.setShell(shell);
		batchMasterInfo.setSupportId(supportId);
		batchMasterInfo.setUpdateBy(userId);
		batchMasterInfo.setCreateBy(userId);
		batchMasterInfo.setUpdateDate(FormatUtil.currentTimestampToOracleDB());
		batchMasterInfo.setCreateDate(batchMasterInfo.getUpdateDate());

		batchMasterRepository.addBatchMaster(batchMasterInfo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * updateBatchMasterInfo(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public void updateBatchMasterInfo(final String batchId,
			final String projectCode, final String batchName,
			final int priorityLevel, final int concurrencyFlag,
			final String runAs, final String shell, final String supportId,
			final String userId, final Date updateDt)
			throws ConcurrencyException, SystemDoesNotExistsException {
		logger.debug("service invoke to update the given batch master details");
		final BatchMaster batchMasterInfo = new BatchMaster();
		final BatchMasterId id = new BatchMasterId(batchId, projectCode);
		batchMasterInfo.setId(id);
		batchMasterInfo.setBatchName(batchName);
		batchMasterInfo.setPriorityLevel(priorityLevel);
		batchMasterInfo.setConcurrencyFlag(concurrencyFlag);
		batchMasterInfo.setRunAs(runAs);
		batchMasterInfo.setShell(shell);
		batchMasterInfo.setSupportId(supportId);
		batchMasterInfo.setUpdateBy(userId);
		batchMasterInfo.setUpdateDate(updateDt);

		batchMasterRepository.updateBatchMaster(batchMasterInfo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * deleteBatchMasterInfo(java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public void deleteBatchMasterInfo(final String projectCode,
			final String batchId, final Date updateDt)
			throws ConcurrencyException, SystemDoesNotExistsException {
		logger.debug("service invoke to delete the batch master details");
		final BatchMaster batchMaster = new BatchMaster();
		final BatchMasterId id = new BatchMasterId(batchId, projectCode);
		batchMaster.setId(id);
		batchMaster.setUpdateDate(updateDt);

		batchMasterRepository.deleteBatchMaster(batchMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33040BatchMasterService#
	 * listBatchMasterInfoToExcel(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public HSSFWorkbook listBatchMasterInfoToExcel(final String batchId,
			final String batchName, final String projectCode,
			final int priorityLevel, final int concurrencyFlag)
			throws SystemDoesNotExistsException,
			UnableToCreateExcelForDowloadException {
		logger.debug("service invoke to genrate excel for given batch master input search criteria.");
		final BatchMaster batchMasterSearchCriteria = new BatchMaster();
		final BatchMasterId id = new BatchMasterId(batchId, projectCode);
		batchMasterSearchCriteria.setId(id);
		batchMasterSearchCriteria.setBatchName(batchName);
		batchMasterSearchCriteria.setPriorityLevel(priorityLevel);
		batchMasterSearchCriteria.setConcurrencyFlag(concurrencyFlag);

		InputStream is = null;
		HSSFWorkbook wb = null;

		try {

			final List<BatchMaster> batchMasterList = batchMasterRepository
					.queryBatchMaster(batchMasterSearchCriteria);
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(batchMasterExcelTemplate);
			wb = new HSSFWorkbook(is);

			final HSSFSheet ws = wb.getSheetAt(0);
			int startRow = 3;

			// iterate system master data
			for (final BatchMaster batchMaster : batchMasterList) {
				int startCell = 0;
				HSSFRow row = null;
				row = createRow(ws, startRow++);
				writeBatchMasterInfo(row, batchMaster, startCell);
			}

		} catch (final FileNotFoundException fe) {
			logger.error("Unable to read the template excel file: {}",
					batchMasterExcelTemplate);
			throw new UnableToCreateExcelForDowloadException();
		} catch (final IOException e) {
			logger.error(
					"Unable to create excel file from template using POI.", e);
			throw new UnableToCreateExcelForDowloadException();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Unable to close input stream.", e);
				}
			}
		}

		return wb;
	}

	/**
	 * create excel row.
	 * 
	 * @param ws A work sheet instance.
	 * @param startRow A row index
	 * @return A newly created row.
	 */
	private HSSFRow createRow(final HSSFSheet ws, final int startRow) {

		final HSSFRow row = ws.createRow(startRow);

		// pre-create the cell
		final CellStyle csBorder = ws.getWorkbook().createCellStyle();
		csBorder.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);

		for (int i = 0; i < 12; i++) {
			row.createCell(i).setCellStyle(csBorder);
		}

		return row;
	}

	/**
	 * write bach master information in row.
	 * 
	 * @param row A row to add batch master details
	 * @param sysInfo Batch master information add in excel.
	 * @param startCell A excel cell.
	 * @return New row index.
	 */
	private int writeBatchMasterInfo(final HSSFRow row,
			final BatchMaster sysInfo, int startCell) {
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getId().getBatchId()));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getBatchName()));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getId().getProjectCode()));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(retrieveBatchMasterPriority().get(sysInfo.getPriorityLevel())));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(retrieveBatchMasterConcurrency().get(sysInfo.getConcurrencyFlag())));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getRunAs()));
		// row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getRunningCount()));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getShell()));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getSupportId()));

		if (sysInfo.getUpdateDate() != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(sysInfo.getUpdateDate()));
		} else {
			row.getCell(startCell++).setCellValue("");
		}
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getUpdateBy()));

		if (sysInfo.getCreateDate() != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(sysInfo.getCreateDate()));
		} else {
			row.getCell(startCell++).setCellValue("");
		}
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(sysInfo.getCreateBy()));

		return startCell;
	}
}
