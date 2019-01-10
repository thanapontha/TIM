/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST33020ExcelDownloadMonitoringService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  Apr 28, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.repository.IST33020ExcelDownloadMonitoringRepository;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.FileProcessingException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadFileId;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;

import com.google.common.base.Strings;

/**
 * Service implementation for Excel download monitoring screen.
 * 
 * @author Manego
 * 
 */
@Service
public class CST33020ExcelDownloadMonitoringService implements
		IST33020ExcelDownloadMonitoringService {

	/** A file logger instance. */
	final Logger logger = LoggerFactory
			.getLogger(CST33020ExcelDownloadMonitoringService.class);

	/** default download folder location. */
	@Value("${default.download.folder}")
	private String downloadPath;

	/** Excel download monitoring repository. */
	@Autowired
	protected IST33020ExcelDownloadMonitoringRepository repository;

	@Autowired
	public void CST33020ServiceImpl(
			IST33020ExcelDownloadMonitoringRepository repository) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #queryExcelDownloads()
	 */
	@Override
	public List<ExcelDownloadStatus> queryExcelDownloads()
			throws NoDataFoundException {
		return repository.queryExcelDownloads();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #queryExcelDownloads(java.lang.String, java.lang.Integer,
	 * java.lang.String, java.util.List)
	 */
	@Override
	public List<ExcelDownloadStatus> queryExcelDownloads(String requestDate,
			Integer status, String reportName, List<String> roleList)
			throws NoDataFoundException {

		SimpleDateFormat sdf = new SimpleDateFormat(
				CST30000Constants.DATE_STRING_SCREEN_FORMAT);
		List<ExcelDownloadStatus> output = new ArrayList<ExcelDownloadStatus>();
		try {
			Date startDate = sdf.parse(requestDate);
			Date endDate = new Date(startDate.getTime() + CST30000Constants.TOTAL_HOURS_IN_DAY);
			output = repository.queryExcelDownloads(startDate, endDate, status,
					reportName, roleList);
			if (output.isEmpty()) {
				throw new NoDataFoundException();
			}
		} catch (java.text.ParseException ex) {
			System.out.println(ex.toString());
		}
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #findReport(java.lang.String, int)
	 */
	@Override
	public ExcelDownloadFile findReport(String docId, int fileNo) {

		ExcelDownloadFileId exDownloadFileId = new ExcelDownloadFileId();
		ExcelDownloadFile exFile = null;
		ExcelDownloadStatus exStatus = null;

		exStatus = repository.findExcelDownloadStatus(docId);
		
		exDownloadFileId.setDocId(docId);
		
		if (fileNo != 0) {
			exDownloadFileId.setFileNo(fileNo);
			exFile = repository.findExcelDownloadFile(exDownloadFileId);
		} else {
			exFile = new ExcelDownloadFile();
			exDownloadFileId.setFileNo(fileNo);
			exDownloadFileId.setDocId(docId);
			exFile.setId(exDownloadFileId);
			exFile.setStatus(exStatus.getStatus());  
		}
		
		
		exFile.setExcelDownloadStatus(exStatus);
		
		return exFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #findStatus(java.lang.String)
	 */
	@Override
	public ExcelDownloadStatus findStatus(String docId) {
		return repository.findExcelDownloadStatus(docId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #deleteExcelDownload(th.co.toyota.st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public ServiceStatus deleteExcelDownload(ExcelDownloadFile exDownload)
			throws ConcurrencyException, FileProcessingException {
		
		File file = null;
		ServiceStatus serviceStatus = ServiceStatus.OK;

		ExcelDownloadStatus status = repository
				.findExcelDownloadStatus(exDownload.getId().getDocId());

		if (exDownload.getId().getFileNo() == 0) {

			if (status.getStatus() == CST30000Constants.EXCEL_DL_STATUS_FINISH) {
				status.setStatus(CST30000Constants.EXCEL_DL_STATUS_DELETE);
				repository.deleteExcelDownload(status);
			} else {
				serviceStatus = ServiceStatus.NG;
			}
			
		} else {
			
			// As per the old implementation, all files should be deleted from system
			// ExcelDownloadFile exFile = repository.findExcelDownloadFile(exDownload.getId());

			for(ExcelDownloadFile exFile : repository.findExcelDownloadFileWithDocId(exDownload.getId().getDocId())){
				if (exFile.getStatus() == CST30000Constants.EXCEL_DL_STATUS_FINISH) {
	
					String path = downloadPath;
					if (!Strings.isNullOrEmpty(status.getOverridePath())) {
						path = status.getOverridePath();
					}
	
					file = new File(path + File.separator + exFile.getFileName());
					if (file.exists()) {
						if(!file.delete()){
							throw new FileProcessingException("Unable to delete the file, please check permissions.");
						}
					}
	
					exFile.setStatus(CST30000Constants.EXCEL_DL_STATUS_DELETE);
					repository.updateExcelDownloadStatus(exFile);
					
				} else {
					serviceStatus = ServiceStatus.NG;
				}
			}
			
			
		}

		return serviceStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #cancelExcelDownload(th.co.toyota.st3.api.model.ExcelDownloadFile)
	 */
	@Override
	public ServiceStatus cancelExcelDownload(ExcelDownloadFile exDownload) {
		ServiceStatus status = ServiceStatus.OK;
		List<ExcelDownloadFile> lsExFile = repository
				.findExcelDownloadFileWithDocId(exDownload.getId().getDocId());
		Iterator<ExcelDownloadFile> xls = lsExFile.iterator();

		ExcelDownloadStatus xlsStatus = repository
				.findExcelDownloadStatus(exDownload.getId().getDocId());
		
		if (xlsStatus.getStatus() == CST30000Constants.EXCEL_DL_STATUS_ON_QUEUE
				|| xlsStatus.getStatus() == CST30000Constants.EXCEL_DL_STATUS_PROCESS) {
			
			try {
				exDownload.setStatus(CST30000Constants.EXCEL_DL_STATUS_CANCEL);
				repository.updateExcelDownloadStatus(exDownload);

				while (xls.hasNext()) {
					ExcelDownloadFile exFile = xls.next();

					if (exFile.getStatus() == CST30000Constants.EXCEL_DL_STATUS_ON_QUEUE
							|| exFile.getStatus() == CST30000Constants.EXCEL_DL_STATUS_PROCESS) {

						exFile.setStatus(CST30000Constants.EXCEL_DL_STATUS_CANCEL);
						repository.cancelExcelDownload(exFile);
					}

				} // while (xls.hasNext()) {
			} catch (Exception ex) {
				status = ServiceStatus.NG;

				logger.error("Exception occured in canceling excel download.", ex);
			}
			
		} else {
			status = ServiceStatus.NG;
		}
		

		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #listExcelDownloads(java.lang.String)
	 */
	// for mini mode
	@Override
	public List<ExcelDownloadStatus> listExcelDownloads(String userId)
			throws NoDataFoundException {

		String requestDate = new SimpleDateFormat(
				CST30000Constants.DATE_STRING_SCREEN_FORMAT).format(new Date()); // Today

		return listExcelDownloads(userId, null, null, requestDate);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService
	 * #listExcelDownloads(java.lang.String, java.lang.Integer,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ExcelDownloadStatus> listExcelDownloads(String userId, Integer status, String reportName, String requestDate)
			throws NoDataFoundException {
		
		final SimpleDateFormat sdf = new SimpleDateFormat(
				CST30000Constants.DATE_STRING_SCREEN_FORMAT);

		List<ExcelDownloadStatus> output = new ArrayList<ExcelDownloadStatus>();
		try {
			Date startDate = sdf.parse(requestDate);
			Date endDate = new Date(startDate.getTime() + CST30000Constants.TOTAL_HOURS_IN_DAY);
			output = repository.queryExcelDownloads(startDate, endDate, status, reportName, userId);
			if (output.isEmpty()) {
				throw new NoDataFoundException();
			}
		} catch (java.text.ParseException ex) {
			logger.warn("date format parsing exception.", ex.toString());
		}

		return output;
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService#isRolesDefinedInDB()
	 */
	@Override
	public boolean isRolesDefinedInDB(){
		final List<?> odbRoles = repository.findRolesForExcelDownloadStatus();
		if(odbRoles != null && !odbRoles.isEmpty())
			return true;
		
		return false;
	}
}