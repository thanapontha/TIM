/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST33020ExcelDownloadMonitoringController.java
 * Program Description	    :  Controller for monitor download excel file
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.math.NumberUtils;
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
import th.co.toyota.application.web.form.CST33020ExcelDownloadMonitoringForm;
import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.FileProcessingException;
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
 * @author Thanawut T.
 * 
 */
@Controller
@RequestMapping(value = "/common/excelDownloadMonitoring")
public class CST33020ExcelDownloadMonitoringController extends CommonBaseController {

	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33020ExcelDownloadMonitoringController.class);
	
	/** Excel download monitoring screen name. */
	final String viewName = "WST33020";
	
	/** Default download path */
	@Value("${default.download.folder}")
	private String downloadPath;
	
	/** Flag to enable ODB role check.*/
	@Value("${default.odb.rolesck:true}")
	private String odbroles_check;
	
	/** Excel download monitoring screen service. */
	protected @Autowired IST33020ExcelDownloadMonitoringService service;
	
	/**
	 * Initial operation when opening screen, It display Report Name is blank.
	 * Status combobox displays "<ALL>". Request Date textbox displays
	 * "Systemdate".
	 * 
	 * @param request A http response object.
	 * @return A {@link ModelAndView} instance.
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView initial(HttpServletRequest request) {
		
		Payload payload = new XmlPayload();
		ModelAndView mv = new ModelAndView(viewName);
		
		try{
			
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setStatus(ServiceStatus.OK);
			payload.setReportStatusData(getReportStatusList());

			mv.addObject("form", new CST33020ExcelDownloadMonitoringForm());

			String defaultRequestDate = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT)
										.format(new Date());
			mv.addObject("defaultRequestDate", defaultRequestDate);
			mv.addObject("downloadPath",downloadPath);
			
		} catch (Exception e) {
		
			logger.error(e.toString() + "\r\n" + getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		
		mv.addObject("payload", payload);
		
		return mv;
	}

	/**
	 * Perform the search operation
	 * <ol>
	 * <li>There is one option to invoke search operation.
	 * <ul>
	 * <li>Click Search button. </ul>
	 * 
	 * <li>Search function will retrieve records according to criteria.
	 * <ul>
	 * <li>In case normal user logs in, retrieve record(s) of only log in user.
	 * Join with table TB_R_ODB_ROLES
	 * <li>In case super admin logs in, retrieve record(s) of all users. Join
	 * with table TB_R_ODB_ROLES </br>
	 * <li>If there is found data, screen will display first page of records.
	 * Search criteria is retained.
	 * <li>If there is no found data, initial screen is reloaded, with error
	 * message. Search criteria is retained.
	 * <ul>
	 * <li>Display Error message : MSTD0059AERR: No data found.
	 * </ul>
	 * </ul>
	 * 
	 * <li>Wildcard search is allowed in Report Name.
	 * 
	 * <li>The value inputted in Report Name textbox can be available to
	 * retrieve Report Name in captal letter(s) and in small letter(s). </br>
	 * e.g. Inputted Report Name : aaa => It's possible to retrieve Report Name
	 * : AAA & aaa & AaA.
	 * 
	 * <li>Validation of search criteria.
	 * </ol>
	 * 
	 * @param form {@link CST33020ExcelDownloadMonitoringForm} object.
	 * @param request A http response object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/search", method = {RequestMethod.POST}, produces = "application/json")
	public @ResponseBody Object searchReport(CST33020ExcelDownloadMonitoringForm form, HttpServletRequest request) {
		
		Payload payload = new XmlPayload();
		List<ExcelDownloadStatus> lsExelDownloadStatus = new ArrayList<ExcelDownloadStatus>();
		try {
			
			//validation
			Set<ConstraintViolation<CST33020ExcelDownloadMonitoringForm>> errors =  super.validator.validate(form);
			if(errors.size() > 0) {
				logger.error("Form validators returned {} number of errors.",
						errors.size());
				payload.addErrorMessages(processErrorMessageFromValidator(errors.toArray(), 
						RequestContextUtils.getLocale(request), 
						new CST33020ExcelDownloadMonitoringForm()));
				payload.setStatus(ServiceStatus.NG);
				return payload; // if error(s) found return payload
			}
			
			final String strRequestDate = form.getRequestDate();
			final Integer iReportStatus = NumberUtils.toInt(form
					.getReportStatus());
			final String strReportName = form.getReportName();

			// if roles check enabled in configuration and roles defined in
			// TB_R_ODB_ROLE.
			if (Boolean.valueOf(odbroles_check) && service.isRolesDefinedInDB()) {
				// system will retrieve DOC_ID that created by who are having
				// same Role.
				CSC22110AccessControlList accessControlList = getAccessControlList(request);
				List<String> roleList = accessControlList.getRoleList();

				payload = populatePayloadForDisplay(viewName, payload,RequestContextUtils.getLocale(request));
				payload.setStatus(ServiceStatus.OK);
				payload.setReportStatusData(getReportStatusList());

				lsExelDownloadStatus = service.queryExcelDownloads(
						strRequestDate, iReportStatus, strReportName, roleList);
			} else {
				// system not found data in TB_R_ODB_ROLE, system will retrieve
				// DOC_ID that created by login user.
				CSC22110UserInfo user = getUserInSession(request);
				lsExelDownloadStatus = service.listExcelDownloads(user.getUserId(), iReportStatus, strReportName, strRequestDate);
			}
			
			payload.setExcelDownloadStatusList(lsExelDownloadStatus);
			
			payload.setStatus(ServiceStatus.OK);
		} catch (NoDataFoundException e) {
			payload.setStatus(ServiceStatus.NG);
			payload.setExcelDownloadStatusList(lsExelDownloadStatus);
		}
		
		return payload;
	}
	
	/**
	 * Display Detail Report
	 * <ul>
	 * <li>Display detail information of selected record in pop-up window.</li>
	 * <li>If no record is selected or plural records are selected, previous
	 * search screen is reloaded, with info message. Search criteria is
	 * retained.</br> Display Information message : MSTD0104AINF: Select one
	 * {record} to be retrieved</li>
	 * </ul>
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
			logger.error(e.toString() + "\r\n" + getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	/**
	 * Cancel Excel download report.
	 * <ol>
	 * <li>Display the confirm message.
	 * <ul>
	 * <li>Display Confirm message : MSTD0003ACFM: Are you sure you want to
	 * abort the operation?
	 * 
	 * <ul>
	 * <li>In case 'Yes' is clicked.
	 * <ul>
	 * <li>Change the status of selected Doc ID record(s) to "4"(Canceled).
	 * <li>Display Information message : MSTD0088AINF: Cancellation for
	 * {DYYMMXXXXXXXXX} is completed successfully
	 * </ul>
	 * 
	 * <li>In case 'Cancel' is clicked.
	 * <ul>
	 * <li>Back to previous screen.
	 * </ul>
	 * </ul>
	 * </ul>
	 * 
	 * <li>If status of selected record isn't "1"(On Queue) or "2"(Processing),
	 * previous search screen is reloaded, with error message. Search criteria
	 * is retained.
	 * <ul>
	 * <li>Display Error message : MSTD1006AERR: Can not cancel {DYYMMXXXXX},
	 * Status must be On Queue or Processing.
	 * </ul>
	 * 
	 * <li>If no record is selected or plural records are selected, previous
	 * search screen is reloaded, with error message. Search criteria is
	 * retained.
	 * <ul>
	 * <li>Display Error message : MSTD1018AERR: A single record must be
	 * selected to execute Cancel operation.
	 * </ul>
	 * </ol>
	 * 
	 * @param docId A document number.
	 * @param request A http response object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value="/cancel",method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object cancelReport(@RequestParam ("docId") String docId, 
											 HttpServletRequest request) {
		ExcelDownloadFile exDownload = new ExcelDownloadFile();
		ExcelDownloadFileId exDownloadId = new ExcelDownloadFileId();
		Payload payload = new XmlPayload();
		try {
			exDownloadId.setDocId(docId);
		
			
			exDownload.setId(exDownloadId);
			ServiceStatus actionResult = service.cancelExcelDownload(exDownload);
			
			
			
			payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
			payload.setReportStatusData(getReportStatusList());
			
			if(actionResult == ServiceStatus.OK) {
				payload.setStatus(actionResult);
				payload.addInfoMessage(messageSource.getMessage(
						CST30000Messages.INFO_CANCELATION_SUCCESS, new String[]{docId} ,RequestContextUtils.getLocale(request)));
			}else if(actionResult == ServiceStatus.NG){
				payload.setStatus(actionResult);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_CANNOT_CANCEL, new String[]{docId," Status must be On Queue or Processing."},
						RequestContextUtils.getLocale(request)));
			}else{
				payload.setStatus(ServiceStatus.NG);
			}
		
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}

		return payload;
	}
	
	/**
	 * Delete The report
	 * <ol>
	 * <li>Display the confirm message.
	 * <ul>
	 * <li>Display Confirm message : MSTD0001ACFM: Are you sure you want to
	 * delete the record ?
	 * 
	 * <ul>
	 * <li>In case 'Yes' is clicked.
	 * <ul>
	 * <li>In case status of selected record is "3"(Finished), delete the
	 * generated zip file and change the status of selected Doc ID record(s) to
	 * "6"(Deleted).
	 * <li>Display Information message : MSTD0090AINF: Deletion process is
	 * completed successfully
	 * </ul>
	 * <li>In case 'Cancel' is clicked.
	 * <ul>
	 * <li>Back to previous screen.
	 * </ul>
	 * </ul>
	 * 
	 * <li>If status of selected record isn't "3"(Finished), previous search
	 * screen is reloaded, with error message. Search criteria is retained.
	 * <ul>
	 * <li>Display Error message : MSTD1005AERR: Can not delete {DYYMMXXXXX},
	 * Status should be Download status.
	 * </ul>
	 * 
	 * <li>If no record is selected or plural records are selected, previous
	 * search screen is reloaded, with info message. Search criteria is
	 * retained.
	 * <ul>
	 * <li>Display Information message : MSTD1016AERR: A single record must be
	 * selected to execute Delete operation.
	 * </ul>
	 * </ol>
	 * 
	 * @param docId A document number
	 * @param fileNo A file number.
	 * @param request A http response object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object deleteReport(@RequestParam ("docId") String docId,
											 @RequestParam ("fileNo") int fileNo, 
											 HttpServletRequest request) {
		Payload payload = new XmlPayload();
		try {
			// Update status to 6 (status can change 3 => 6 only)
			ExcelDownloadFile exDownload = new ExcelDownloadFile();
			ExcelDownloadFileId exDownloadId = new ExcelDownloadFileId();
			
			exDownloadId.setDocId(docId);
			exDownloadId.setFileNo(fileNo);
			
			exDownload.setId(exDownloadId);
			
			ServiceStatus actionResult = service.deleteExcelDownload(exDownload);

			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setReportStatusData(getReportStatusList());
			
			if(actionResult == ServiceStatus.OK) {
				payload.setStatus(actionResult);
				payload.addInfoMessage(messageSource.getMessage(
						CST30000Messages.INFO_DELETION_SUCCESS, null,RequestContextUtils.getLocale(request)));
				
			}else if(actionResult == ServiceStatus.NG){
				payload.setStatus(actionResult);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_CANNOT_DELETE, new String[]{docId," Status should be Download status."},
						RequestContextUtils.getLocale(request)));
			}else{
				payload.setStatus(ServiceStatus.NG);
			}
		
		} catch (ConcurrencyException e) {
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK,
					new String[] {},
					RequestContextUtils.getLocale(request)));
		} catch (FileProcessingException fpe) {
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_DELETE_FILE_FAILURE,
					new String[] {""},
					RequestContextUtils.getLocale(request)));
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString()}, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	/**
	 * Download the report.</br> - Display the download file dialog to download
	 * the generated file.
	 * 
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

	/**
	 * Verify the report exists on server or not.
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
	
	public String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
}
