/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST33050BatchStatusService.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;

import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.repository.IST33050BatchStatusRepository;
import th.co.toyota.application.repository.IST33060SystemMasterRepository;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.BatchStatusLog;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

@Service
public class CST33050BatchStatusService implements IST33050BatchStatusService {
	final Logger logger = LoggerFactory
			.getLogger(CST33050BatchStatusService.class);

	@Autowired
	private IST33050BatchStatusRepository batchStatusRepository;
	
	@Autowired
	private IST33060SystemMasterRepository systemMasterRepository;

	@Value("${templateFileBatchStatus}")
	private String batchStatusExcelTemplate;
	
	@Value("${projectCode}")
	private String systemName;
	
	public static final String CATEGORY_BFW = "BFW";
	public static final String SUB_CATEGORY_APP_LOG = "APP_LOG_URL";

	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33050BatchStatusService#listBatchStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> listBatchStatus(String batchId, String batchName,
			String projectCode, String runDate, String reqBy) {
		logger.debug("service invoke to find the batch status details");
		try{
			final Date reqRunDate = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT).parse(runDate);
			final Map<String, Object> batchStatusSearchCriteria = new HashMap<String, Object>();
			batchStatusSearchCriteria.put("batchId", batchId);
			batchStatusSearchCriteria.put("batchName", batchName);
			batchStatusSearchCriteria.put("projectCode", projectCode);
			batchStatusSearchCriteria.put("runDate", reqRunDate);
			batchStatusSearchCriteria.put("requestBy", reqBy);
	
			return batchStatusRepository.queryBatchStatusLog(batchStatusSearchCriteria);
		}catch(final Exception ex){
			throw new RuntimeException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33050BatchStatusService#deleteBatchStatus(int, java.lang.String, java.util.Date)
	 */
	@Override
	public ServiceStatus deleteBatchStatus(final int batchSerialNo, final String batchId, final Date updateDt) throws ConcurrencyException,
	SystemDoesNotExistsException {
		logger.debug("service invoke to delete the batch status details");
		ServiceStatus status = ServiceStatus.OK;
		
		// check if in queue.
		final BatchQueue inQueue = batchStatusRepository.findBatchQueueInfo(batchId, batchSerialNo);
		if(inQueue != null){
			return ServiceStatus.NG;
		}
		
		// check if in processing state.
		final BatchStatusLog statusLog = batchStatusRepository.findLatestBatchStatusLog(batchId, batchSerialNo);
		if(statusLog != null && CST30000Constants.BATCH_STATUS_PROCESSING.equals(statusLog.getMessage())){
			return ServiceStatus.NG;
		}
		
		// neither in queue nor in processing state, so delete all records.
		batchStatusRepository.deleteBatchStatus(batchId, batchSerialNo);
		
		// return success status.
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33050BatchStatusService#cancelBatchStatus(int, java.lang.String, java.util.Date)
	 */
	@Override
	public ServiceStatus cancelBatchStatus(final int batchSerialNo, final String batchId, final Date updateDt)
			throws ConcurrencyException, SystemDoesNotExistsException {
		ServiceStatus status = ServiceStatus.OK;
		
		// check if in queue. it should be in queue to perform cancel operation.
		final BatchQueue inQueue = batchStatusRepository.findBatchQueueInfo(batchId, batchSerialNo);
		if(inQueue == null){
			return ServiceStatus.NG;
		}
		
		// not in queue, proceed for cancelation.
		final BatchQueue batchQueue = new BatchQueue();
		batchQueue.setQueueNo(batchSerialNo);
		batchQueue.setBatchId(batchId);
		//batchQueue.setUpdateDate(updateDt);
		
		batchStatusRepository.cancelBatchStatus(batchQueue);
		
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33050BatchStatusService#detailedBatchStatus(java.lang.String)
	 */
	@Override
	public String detailedBatchStatus(final String batchId) throws SystemDoesNotExistsException, SystemIsNotActiveException {
		final SystemInfoId id = new SystemInfoId();
		id.setCategory(CATEGORY_BFW);
		id.setSubCategory(SUB_CATEGORY_APP_LOG);
		id.setCode(systemName + CST30000Constants.FILE_DELIMITER + batchId);

		final List<SystemInfo> systemInfoList = systemMasterRepository.querySystemMasterInfo(id);
		if(systemInfoList == null || systemInfoList.isEmpty()){
			throw new SystemDoesNotExistsException("CATEGORY="
					+ id.getCategory() + ", SUB_CATEGORY="
					+ id.getSubCategory() + ", CD = " + id.getCode());
		}
		
		final SystemInfo systemInfo = systemInfoList.get(0);
		// throws exception is configuration is not active.
		if(!CST30000Constants.YES.equalsIgnoreCase(String.valueOf(systemInfo.getStatus()))){
			logger.error("log monitoring configuration is not active.");
			throw new SystemIsNotActiveException("CATEGORY="
					+ id.getCategory() + ", SUB_CATEGORY="
					+ id.getSubCategory() + ", CD = " + id.getCode());
		}
		
		return systemInfo.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33050BatchStatusService#listBatchStatusToExcel(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public HSSFWorkbook listBatchStatusToExcel(String batchId, String batchName,
			String projectCode, String runDate, String reqBy)
			throws SystemDoesNotExistsException,
			UnableToCreateExcelForDowloadException {
		logger.debug("service invoke to genrate excel for given batch status input search criteria.");
		InputStream is = null;
		HSSFWorkbook wb = null;

		try {
			final List<Map<String,Object>> batchStatusList = this.listBatchStatus(batchId, batchName, projectCode, runDate, reqBy);
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(batchStatusExcelTemplate);
			wb = new HSSFWorkbook(is);

			final HSSFSheet ws = wb.getSheetAt(0);
			int startRow = 3;

			// iterate system status data
			for (final Map<String, Object> batchStatus : batchStatusList) {
				int startCell = 0;
				HSSFRow row = null;
				row = createRow(ws, startRow++);
				writeBatchStatusInfo(row, batchStatus, startCell);
			}

		} catch (final FileNotFoundException fe) {
			logger.error("Unable to read the template excel file: {}",
					batchStatusExcelTemplate);
			throw new UnableToCreateExcelForDowloadException();
		} catch (final IOException e) {
			logger.error(
					"Unable to create excel file from template using POI.", e);
			throw new UnableToCreateExcelForDowloadException();
		} catch (final Exception e) {
			logger.error(
					"Unable to create excel file as undefined error occured.", e);
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
	 * @param ws A worksheet
	 * @param startRow A row index.
	 * @return Return newly created row.
	 */
	private HSSFRow createRow(final HSSFSheet ws, final int startRow) {

		final HSSFRow row = ws.createRow(startRow);

		// pre-create the cell
		final CellStyle csBorder = ws.getWorkbook().createCellStyle();
		csBorder.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);

		for (int i = 0; i < 10; i++) {
			row.createCell(i).setCellStyle(csBorder);
		}

		return row;
	}

	/**
	 * write batch status information in row.
	 * 
	 * @param row A row to add batch status details.
	 * @param batchInfo A batch status information map.
	 * @param startCell A row index.
	 * @return A next row index.
	 */
	private int writeBatchStatusInfo(final HSSFRow row,
			final Map<String, Object> batchInfo, int startCell) {
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("appId")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("projectCode")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("batchId")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("batchName")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("supportId")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("description")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("status")));
		row.getCell(startCell++).setCellValue(ObjectUtils.toString(batchInfo.get("requestBy")));

		if (batchInfo.get("requestDate") != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(batchInfo.get("requestDate")));
		} else {
			row.getCell(startCell++).setCellValue("");
		}

		if (batchInfo.get("runDate") != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(batchInfo.get("runDate")));
		} else {
			row.getCell(startCell++).setCellValue("");
		}

		return startCell;
	}
}
