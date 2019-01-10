/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST33010LogMonitoringController.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 3, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponentsBuilder;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST33010LogMonitoringService;
import th.co.toyota.application.service.IST33060SystemMasterService;
import th.co.toyota.application.web.form.CST33010LogMonitoringForm;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.download.CST30091Downloader;
import th.co.toyota.st3.api.exception.LogsDoesNotExistsException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;
import th.co.toyota.st3.api.model.SystemInfo;

import com.google.common.base.Strings;

/**
 * This screen is used to monitor all processes and to deleting process errors.
 * User enable to Select which data to be shown and specify the criteria(s)
 * 
 * 
 * @author danilo
 * 
 */
@Controller
@RequestMapping(value = "/common/logMonitoring")
public class CST33010LogMonitoringController extends CommonBaseController {
	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33010LogMonitoringController.class);

	/** A log monitoring screen name. */
	final String viewName = "WST33010";
	
	/** Report name used by this screen. */
	final String reportName = "Log Monitoring";

	/** A spring validator. */
	@Autowired
	protected Validator validator;

	/** A log monitoring service.  */
	@Autowired
	protected IST33010LogMonitoringService service;
	
	/** A system master service. */
	@Autowired
	private IST33060SystemMasterService serviceSystemMaster = null;
	
	@Autowired
	protected CST30091Downloader downloader;
	
	/**
	 * A initial screen
	 * <ol>
	 * <li>Default Date from and date to with current date
	 * <li>Log details on the detail area will not be displayed. As it is
	 * initial screen.
	 * </ol>
	 * 
	 * @param request A HttpRequest.
	 * @return {@link ModelAndView} instance.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initial(HttpServletRequest request) {
		logger.info("Initial form is initated.");

		List<ModuleHeaderInfo> modules = service.queryModules();

		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		payload.setStatus(ServiceStatus.OK);
		payload.setModules(modules);

		String objIdJSON = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			objIdJSON = mapper.writeValueAsString(service.queryModules());
		} catch (Exception e) {
			logger.error("Error encountered in JSON conversion.", e);
		}
		
		SimpleDateFormat dFmt = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT);
		DateTime date = DateTime.now();
		
		List<SystemInfo> logStatusquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_BATCH_STATUS);
		
		List<SystemInfo> processStatusquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_PROCESS_STATUS);
		
		List<SystemInfo> processmessageLevelquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_MESSAGE_LEVEL);				
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("form", new CST33010LogMonitoringForm());
		mv.addObject("payload", payload);
		mv.addObject("modules", objIdJSON);
		mv.addObject("currentDate", dFmt.format(date.toDate()));
		mv.addObject("logStatus", logStatusquerySystemMasterCodeValue);
		mv.addObject("callFromHyperlink", false);		
		mv.addObject("processStatus", processStatusquerySystemMasterCodeValue);
		mv.addObject("messageLevel", processmessageLevelquerySystemMasterCodeValue);
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ModelAndView checkingReport(
			CST33010LogMonitoringForm form, HttpServletRequest request){
		logger.info("Initial form is initated.");
		Set<ConstraintViolation<CST33010LogMonitoringForm>> errors = validator
				.validate(form);
		List<ModuleHeaderInfo> modules = service.queryModules();
		String appId = form.getAppId();
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		payload.setStatus(ServiceStatus.OK);
		payload.setModules(modules);
		payload.setAppId(appId);

		String objIdJSON = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			objIdJSON = mapper.writeValueAsString(service.queryModules());
		} catch (Exception e) {
			logger.error("Error encountered in JSON conversion.", e);
		}
		SimpleDateFormat dFmt = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT);
		DateTime date = new DateTime();
		if(!Strings.isNullOrEmpty(form.getDateFrom())){
			
			try {
				Date from = dFmt.parse(form.getDateFrom());
				date = new DateTime(from.getTime());
			} catch (ParseException e) {
				logger.error("Form validators returned {} number of errors.",
						errors.size());
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessages(processErrorMessageFromValidator(
						errors.toArray(), RequestContextUtils.getLocale(request), form));
				e.printStackTrace();
			}
			
		}else{
			date = DateTime.now();
		}
	
		List<SystemInfo> logStatusquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_BATCH_STATUS);
		
		List<SystemInfo> processStatusquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_PROCESS_STATUS);
		
		List<SystemInfo> processmessageLevelquerySystemMasterCodeValue = serviceSystemMaster.querySystemMasterCodeValue(
				CST30000Constants.SYS_MASTER_CATEGORY_ST3,CST30000Constants.SUB_CATEGORY_MESSAGE_LEVEL);
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("form", form);
		mv.addObject("payload", payload);
		mv.addObject("modules", objIdJSON);
		mv.addObject("appId", appId);
//		mv.addObject("isSearchOnLoad", true);
		mv.addObject("callFromHyperlink", true);		
		mv.addObject("currentDate", dFmt.format(date.toDate()));
		mv.addObject("logStatus", logStatusquerySystemMasterCodeValue);
		mv.addObject("processStatus", processStatusquerySystemMasterCodeValue);
		mv.addObject("messageLevel", processmessageLevelquerySystemMasterCodeValue);
		return mv;
	}

	/**
	 * Perform Search Operation
	 * <ol>
	 * <li>On click of [Search] button, system will search the using the entered
	 * selection criteria.
	 * <ul>
	 * <li>If no date range are entered, the search result should display all
	 * records satisfying the other selection criteria.
	 * <li>If date range are entered, the search result should display all
	 * records satisfying the selection criteria within the date range.
	 * <li>If no user ID is entered, the search result should display all
	 * records satisfying the other selection criteria.
	 * <li>If user id is entered, the selection criteria should include the user
	 * ID.
	 * </ul>
	 * 
	 * <li>If searching of record is successful, the search results will be
	 * retrieved and will be displayed on the table in the detail area. Details
	 * will be displayed as text.
	 * 
	 * <li>If no record is found, validation message will be displayed and the
	 * table of details will not be displayed.
	 * <li>If a record is found, the table of details will be displayed showing
	 * the search results.
	 * </ol>
	 * 
	 * @param form A {@link CST33010LogMonitoringForm}
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance.
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST,
			produces = "application/json")
	public @ResponseBody
	Object searchLog(CST33010LogMonitoringForm form, HttpServletRequest request) {
		logger.info("Searching Log Information.");

        List<List<LogInfo>> logs = null;
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
                
		//temporary values
		int firstResult = Integer.valueOf(request.getParameter("firstResult"));
		int rowsPerPage = Integer.valueOf(request.getParameter("rowsPerPage"));
		int totalRows = 0;

		Set<ConstraintViolation<CST33010LogMonitoringForm>> errors = validator
				.validate(form);

		if (errors.size() > 0) {
			logger.error("Form validators returned {} number of errors.",
					errors.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), form));
		} else {
			LogInfo log = new LogInfo();

			if (!Strings.isNullOrEmpty(form.getModule())) {
				log.setModuleId(form.getModule());
			}

			if (!Strings.isNullOrEmpty(form.getFunction())) {
				log.setFunctionId(form.getFunction());
			}

			if (!Strings.isNullOrEmpty(form.getLogStatus())) {
				log.setStatus(form.getLogStatus());
			}

			if (!Strings.isNullOrEmpty(form.getUserId())) {
				log.setCreateBy(form.getUserId());
			}

			if (!Strings.isNullOrEmpty(form.getAppId())) {
				log.setAppId(form.getAppId());
			}
			
			if (!Strings.isNullOrEmpty(form.getMessageType())) {
				log.setMessageType(form.getMessageType());
			}

			log.setLogDetail(form.isLogDetail());
			
			Date dateFrom = null;
			Date dateTo = null;

			DateTimeFormatter dFmt = new DateTimeFormatterFactory(
					CST30000Constants.DATE_STRING_SCREEN_FORMAT)
					.createDateTimeFormatter();

			
			String dateString = "", dateId = "";
			try {
				if (!Strings.isNullOrEmpty(form.getDateFrom())) {
					dateString = this.messageSource.getMessage("ST3.WST33010.Label.FromDate", null, RequestContextUtils.getLocale(request));
					dateId = "#dateFrom";
					dateFrom = LocalDate.parse(form.getDateFrom(), dFmt).toDate();
				}

				if (!Strings.isNullOrEmpty(form.getDateTo())) {
					dateString = this.messageSource.getMessage("ST3.WST33010.Label.ToDate", null, RequestContextUtils.getLocale(request));
					dateId = "#dateTo";
					dateTo = LocalDate.parse(form.getDateTo(), dFmt).toDate();
				}
				
				if ( dateFrom == null || dateTo == null || dateFrom.compareTo(dateTo) <= 0) {
					
					if (form.isLogDetail()){
                        totalRows = service.getLogDetailCount(log, dateFrom, dateTo);
						firstResult = firstResult - (firstResult % rowsPerPage);
						if (firstResult >= totalRows) {
							firstResult = totalRows - rowsPerPage;
							if (firstResult < 0)
								firstResult = 0;
						}
                        
//						payload.setGroupedLogsDisplay(service.searchLogDisplay(log, dateFrom, dateTo));
                        payload.setGroupedLogsDisplay(service.searchLogDisplay(
								log, dateFrom, dateTo, firstResult, rowsPerPage));
                    
					}else{
                        totalRows = service.searchHeaderCount(log, dateFrom, dateTo);
						firstResult = firstResult - (firstResult % rowsPerPage);
						if (firstResult >= totalRows) {
							firstResult = totalRows - rowsPerPage;
							if (firstResult < 0)
								firstResult = 0;
						}
                        
//						List<List<LogInfo>> logs = service.searchLog(log, dateFrom, dateTo);
                        logs = service.searchHeaderLog(log, 
								dateFrom, dateTo, firstResult, rowsPerPage);
						
						for (List<LogInfo> list : logs) {
							for (LogInfo logInfo : list) {
								logInfo.setGroupLog(null); //prevent error: Direct self-reference leading to cycle
							}
						}
						payload.setGroupedLogs(logs);
					}
					
					payload.setStatus(ServiceStatus.OK);
				}else{
					dateString = this.messageSource.getMessage("ST3.WST33010.Label.FromDate", null, RequestContextUtils.getLocale(request));
					String dateToString = this.messageSource.getMessage("ST3.WST33010.Label.ToDate", 
							null, RequestContextUtils.getLocale(request));
					dateId = "#dateFrom";
					payload.setFocusId(dateId);
					payload.setStatus(ServiceStatus.NG);
					payload.addErrorMessage(messageSource.getMessage(
							CST30000Messages.ERROR_TODATE_MUST_GREATER_THAN_FROMDATE, 
							new String[] {dateToString, dateString},
							RequestContextUtils.getLocale(request)));
				}
			} catch (LogsDoesNotExistsException e) {
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, null,
						RequestContextUtils.getLocale(request)));
			}catch(IllegalArgumentException e){
				payload.setStatus(ServiceStatus.NG);
				payload.setFocusId(dateId);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, new String[]{dateString},
						RequestContextUtils.getLocale(request)));
			}
		}
        
        payload.setFirstResult(firstResult);
		payload.setRowsPerPage(rowsPerPage);
		payload.setTotalRows(totalRows);

		return payload;
	}

	/**
	 * Perform Download Operation:
	 * <ol>
	 * <li>On click of [Download], the search criteria will be passed on a
	 * common download function.
	 * <li>If No data is found for the the selection criteria, error message
	 * will be displayed
	 * <li>If search result is found, Excel file will be created and displayed.
	 * <ul>
	 * 
	 * <li>If error is encountered while creating the excel file, log error
	 * message.
	 * </ul>
	 * </ol>
	 * 
	 * @param form A {@link CST33010LogMonitoringForm}
	 * @param request A HttpRequest.
	 * @param response A HttpResponse object.
	 * @return {@link Payload} instance.
	 */
	@RequestMapping(value = "/download/xls", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody Object downloadLogsXLS(CST33010LogMonitoringForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Downloading Log Information in XLS.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		HSSFWorkbook workbook = null;
		try {
			DateTime dt = DateTime.now();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

			
			LogInfo log = new LogInfo();

			if (!Strings.isNullOrEmpty(form.getModule())) {
				log.setModuleId(form.getModule());
			}

			if (!Strings.isNullOrEmpty(form.getFunction())) {
				log.setFunctionId(form.getFunction());
			}

			if (!Strings.isNullOrEmpty(form.getLogStatus())) {
				log.setStatus(form.getLogStatus());
			}

			if (!Strings.isNullOrEmpty(form.getUserId())) {
				log.setCreateBy(form.getUserId());
			}

			if (!Strings.isNullOrEmpty(form.getAppId())) {
				log.setAppId(form.getAppId());
			}
			
			if (!Strings.isNullOrEmpty(form.getMessageType())) {
				log.setMessageType(form.getMessageType());
			}

			log.setLogDetail(form.isLogDetail());
			
			Date dateFrom = null;
			Date dateTo = null;

			DateTimeFormatter dFmt = new DateTimeFormatterFactory(
					CST30000Constants.DATE_STRING_SCREEN_FORMAT)
					.createDateTimeFormatter();

			if (!Strings.isNullOrEmpty(form.getDateFrom())) {
				dateFrom = LocalDate.parse(form.getDateFrom(), dFmt).toDate();
			}

			if (!Strings.isNullOrEmpty(form.getDateTo())) {
				dateTo = LocalDate.parse(form.getDateTo(), dFmt).toDate();
			}

			workbook = service.listRolesToExcelXLS(log, dateFrom, dateTo);
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ reportName + "_" + format.format(dt.toDate()) + ".xls");
			workbook.write(response.getOutputStream());
			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SYSTEM_INFORMATION_CREATE_SUCCESSFUL,
					new String[] { "Excel file" }, RequestContextUtils.getLocale(request)));
		} catch (LogsDoesNotExistsException
				| UnableToCreateExcelForDowloadException e) {
			response.setContentType("text/plain");
			logger.error("Log to download does not exists.");
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, null,
					RequestContextUtils.getLocale(request)));
			
			String objIdJSON = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				objIdJSON = mapper.writeValueAsString(payload);
			} catch (Exception e2) {
				logger.error("Error encountered in JSON conversion.", e2);
			}
			
			return objIdJSON;
		} catch (IOException e) {
			logger.error("Unable to write to output stream");
		}
		return payload;
	}
	
	/**
	 * Perform Download Operation:
	 * <ol>
	 * <li>On click of [Download], the search criteria will be passed on a
	 * common download function.
	 * <li>If No data is found for the the selection criteria, error message
	 * will be displayed
	 * <li>If search result is found, Excel file will be created and displayed.
	 * <ul>
	 * 
	 * <li>If error is encountered while creating the excel file, log error
	 * message.
	 * </ul>
	 * </ol>
	 * 
	 * @param form A {@link CST33010LogMonitoringForm}
	 * @param request A HttpRequest.
	 * @param response A HttpResponse object.
	 * @return {@link Payload} instance.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody Object downloadLogsXLSX(CST33010LogMonitoringForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.info("Downloading Log Information in XLSX.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		
		try {

			
			LogInfo log = new LogInfo();

			if (!Strings.isNullOrEmpty(form.getModule())) {
				log.setModuleId(form.getModule());
			}

			if (!Strings.isNullOrEmpty(form.getFunction())) {
				log.setFunctionId(form.getFunction());
			}

			if (!Strings.isNullOrEmpty(form.getLogStatus())) {
				log.setStatus(form.getLogStatus());
			}

			if (!Strings.isNullOrEmpty(form.getUserId())) {
				log.setCreateBy(form.getUserId());
			}

			if (!Strings.isNullOrEmpty(form.getAppId())) {
				log.setAppId(form.getAppId());
			}
			
			if (!Strings.isNullOrEmpty(form.getMessageType())) {
				log.setMessageType(form.getMessageType());
			}

			log.setLogDetail(form.isLogDetail());
			
			Date dateFrom = null;
			Date dateTo = null;

			DateTimeFormatter dFmt = new DateTimeFormatterFactory(
					CST30000Constants.DATE_STRING_SCREEN_FORMAT)
					.createDateTimeFormatter();

			if (!Strings.isNullOrEmpty(form.getDateFrom())) {
				dateFrom = LocalDate.parse(form.getDateFrom(), dFmt).toDate();
			}

			if (!Strings.isNullOrEmpty(form.getDateTo())) {
				dateTo = LocalDate.parse(form.getDateTo(), dFmt).toDate();
			}
			
			
			String excelName = service.listRolesToExcelXLSX(log, dateFrom, dateTo, reportName);
			
			downloader.download(excelName, CST30000Constants.FILE_TYPE_XLSX, response);
			response.flushBuffer();

			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SYSTEM_INFORMATION_CREATE_SUCCESSFUL,
					new String[] { "Excel file" }, RequestContextUtils.getLocale(request)));
		} catch (LogsDoesNotExistsException
				| UnableToCreateExcelForDowloadException | NoDataFoundException e) {
			response.setContentType("text/plain");
			logger.error("Log to download does not exists.");
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, null,
					RequestContextUtils.getLocale(request)));
			
			String objIdJSON = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				objIdJSON = mapper.writeValueAsString(payload);
			} catch (Exception e2) {
				logger.error("Error encountered in JSON conversion.", e2);
			}
			
			return objIdJSON;
		} catch (IOException e) {
			logger.error("Unable to write to output stream");
		} catch(Exception e){
			logger.error("Undefined Exception");
			e.printStackTrace();
		}
		return payload;
	}
	

}
