/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST33022ExcelDownloadMonitoringMiniController.java
 * Program Description	    :  Controller for monitor download excel file (minimal screen)
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
package th.co.toyota.application.web;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST33020ExcelDownloadMonitoringService;
import th.co.toyota.application.web.form.CST33022ExcelDownloadMonitoringMiniForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.model.ExcelDownloadFile;
import th.co.toyota.st3.api.model.ExcelDownloadFileId;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;

import com.google.common.base.Strings;

/**
 * This screen is used for monitoring all on demand Excel download process
 * status. User can search the download process status, download results and
 * cancel the download process by using this screen.
 * 
 * 
 * @author Thanawut T.
 * 
 */
@Controller
@RequestMapping(value = "/common/excelDownloadMonitoringMiniList")
public class CST33022ExcelDownloadMonitoringMiniController extends CommonBaseController {

	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33022ExcelDownloadMonitoringMiniController.class);
	
	/** Excel download monitoring mini screen name. */
	final String viewName = "WST33022";
	
	/** Defalut download folder. */
	@Value("${default.download.folder}")
	private String downloadPath;
	
	/** Excel download monitoring service. */
	@Autowired
	protected IST33020ExcelDownloadMonitoringService service;
	
	CST33020ExcelDownloadMonitoringController excelDownloadMonitor = new CST33020ExcelDownloadMonitoringController();
	
	/**
	 * Initial operation when opening screen. It display detail information of
	 * selected record.
	 * 
	 * @param request A http request object.
	 * @return A {@link ModelAndView}
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView searchExcelDownload(HttpServletRequest request) {
		
		Payload payload = new XmlPayload();
		ModelAndView mv = new ModelAndView(viewName);
		
		try{
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setStatus(ServiceStatus.OK);
			payload.setReportStatusData(getReportStatusList());

			mv.addObject("form", new CST33022ExcelDownloadMonitoringMiniForm());

			mv.addObject("downloadPath",downloadPath);

		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + excelDownloadMonitor.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		
		mv.addObject("payload", payload);
		
		return mv;
	}
	
	/**
	 * Execute when opening screen
	 * <ul>
	 * <li>In case non-admin user logs on, display the status of login user's
	 * records in TB_L_EXCEL_DOWNLOAD_STATUS and TB_L_EXCEL_DOWNLOAD_FILE..
	 * <li>In case admin user logs on, display the status of records in
	 * TB_L_EXCEL_DOWNLOAD_STATUS and TB_L_EXCEL_DOWNLOAD_FILE..
	 * </ul>
	 * 
	 * @param form A {@link CST33022ExcelDownloadMonitoringMiniForm} form instance.
	 * @param request A http response object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/search", method = {RequestMethod.POST}, produces = "application/json")
	public @ResponseBody Object searchReport(CST33022ExcelDownloadMonitoringMiniForm form, HttpServletRequest request) {
		
		Payload payload = new XmlPayload();
		List<ExcelDownloadStatus> lsExelDownloadStatus = new ArrayList<ExcelDownloadStatus>();
		
		try {
			
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setReportStatusData(getReportStatusList());
			
			CSC22110UserInfo user = getUserInSession(request);
			lsExelDownloadStatus = service.listExcelDownloads(user.getUserId());
			
			payload.setExcelDownloadStatusList(lsExelDownloadStatus);
			
			payload.setStatus(ServiceStatus.OK);
		} catch (NoDataFoundException e) {
			payload.setStatus(ServiceStatus.NG);
			payload.setExcelDownloadStatusList(lsExelDownloadStatus);
		}
		
		return payload;
	}
	
	/**
	 * Display detail of excel download.
	 * <ol>
	 * <li>Display detail information of selected record in pop-up window.
	 * 
	 * <li>If no record is selected or plural records are selected, previous
	 * search screen is reloaded, with info message. Search criteria is
	 * retained.
	 * <ul>
	 * <li>Display Information message : MSTD1019AERR: A single record must be
	 * selected to view its details.
	 * </ul>
	 * </ol>
	 * 
	 * @param docId A document number.
	 * @param fileNo A file number.
	 * @param request A http response object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value="/details", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object detailsReport(@RequestParam ("docId") String docId,
											  @RequestParam ("fileNo") int fileNo, 
											  HttpServletRequest request) {
		ExcelDownloadFile exDownload = new ExcelDownloadFile();
		ExcelDownloadFileId exDownloadId = new ExcelDownloadFileId();
		Payload payload = new XmlPayload();
		
		try{
			exDownloadId.setDocId(docId);
			exDownloadId.setFileNo(fileNo);
			exDownload.setId(exDownloadId);
			
			ExcelDownloadFile exDownloadFile = service.findReport(docId, fileNo);

			payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
	
			payload.setExcelDownloadFile(exDownloadFile);
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + excelDownloadMonitor.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	/**
	 * Verifies report file on server before to download.
	 * 
	 * @param docId A document number.
	 * @param fileNo A file number.
	 * @param request A http response object.
	 * @param response A http response.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "application/json")
	public  @ResponseBody Object checkZip(@RequestParam("docId") String docId, @RequestParam("fileNo") int fileNo, 
			HttpServletRequest request, HttpServletResponse response) {
		Payload payload = null;
		ExcelDownloadStatus status = service.findStatus(docId);
		ExcelDownloadFile exDownloadFile = service.findReport(docId, fileNo);
		
		String logString;
		File file = null;
		
		payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
		
		try {
			String path = downloadPath;
			
			if (!Strings.isNullOrEmpty(status.getOverridePath())) {
				path = status.getOverridePath();
			}
		
			file = new File(path + File.separator + exDownloadFile.getFileName());
			
			if (!file.exists()) {
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERR_FILE_NOT_EXISTS,
						new String[] {  exDownloadFile.getFileName(), "server"}, RequestContextUtils.getLocale(request)));
				
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, 
						new String[] { file.getAbsolutePath() },
						Locale.getDefault());
				
				logger.error(logString);
			} else {
				payload.setStatus(ServiceStatus.OK);
			}
		} catch (Exception e) {
			payload.setStatus(ServiceStatus.NG);

			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] {  e.getMessage() }, RequestContextUtils.getLocale(request)));
			logger.error("System exception", e);
		} 
		
		
		return payload;
	}
	
	/**
	 * Download the excel download report using mini screen.
	 * @param docId
	 * @param fileNo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/download/{docId}/{fileNo}", method = RequestMethod.GET)
	public  @ResponseBody void downloadZip(@PathVariable("docId") String docId, @PathVariable("fileNo") int fileNo, 
			HttpServletRequest request, HttpServletResponse response) {
		
		ExcelDownloadStatus status = service.findStatus(docId);
		ExcelDownloadFile exDownloadFile = service.findReport(docId, fileNo);
		
		String logString;
		File file = null;
		
		try {
			String path = downloadPath;
			
			if (!Strings.isNullOrEmpty(status.getOverridePath())) {
				path = status.getOverridePath();
			}
		
			file = new File(path + File.separator + exDownloadFile.getFileName());
			
			if (!file.exists()) {
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, 
						new String[] { file.getAbsolutePath() },
						Locale.getDefault());
				
				logger.error(logString);
			} else {
				response.setContentType(CST30000Constants.CONTENT_TYPE_ZIP);
				response.setHeader("Content-Disposition", 
						"attachment; filename=" + file.getName());
				FileInputStream fs = new FileInputStream(file);
				
				FileCopyUtils.copy(fs, response.getOutputStream());
				response.flushBuffer();
		
			}
		} catch (Exception e) {
			logger.error("System exception", e);
		} 
		
	}
	
	private Map<Character, String> getReportStatusList(){
		
		Map<Character, String> map = new TreeMap<Character, String>();
		map.put(CST30000Constants.EXCEL_DL_STATUS_ON_QUEUE, CST30000Constants.EXCEL_DL_STRING_ON_QUEUE);
		map.put(CST30000Constants.EXCEL_DL_STATUS_PROCESS, CST30000Constants.EXCEL_DL_STRING_PROCESS);
		map.put(CST30000Constants.EXCEL_DL_STATUS_FINISH, CST30000Constants.EXCEL_DL_STRING_FINISH);
		map.put(CST30000Constants.EXCEL_DL_STATUS_CANCEL, CST30000Constants.EXCEL_DL_STRING_CANCEL);
		map.put(CST30000Constants.EXCEL_DL_STATUS_TIMEOUT, CST30000Constants.EXCEL_DL_STRING_TIMEOUT);
		map.put(CST30000Constants.EXCEL_DL_STATUS_DELETE, CST30000Constants.EXCEL_DL_STRING_DELETE);
		map.put(CST30000Constants.EXCEL_DL_STATUS_ERROR_OCCUR, CST30000Constants.EXCEL_DL_STRING_ERROR_OCCUR);
		map.put(CST30000Constants.EXCEL_DL_STATUS_NO_DATA_FOUND, CST30000Constants.EXCEL_DL_STRING_NO_DATA_FOUND);

		return map;
	}
}
