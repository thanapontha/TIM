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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponentsBuilder;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST33050BatchStatusService;
import th.co.toyota.application.web.form.CST33050BatchStatusForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;

import com.google.common.base.Strings;

/**
 * The On Demand Batch Status Screen would be used by Application adminstrators
 * to monitor On Demand Batch programs that was posted in TMAP Batch Framework
 * to run. ODB programs that are currently on queue/on hold can also be canceled
 * by the administrator if deemed appropriate.
 * <p>
 * This screen will be a common screen for all applications and therefore will
 * be located in a separate menu in the application menu bar selection. This
 * will be one of the screens in the TMAP Standard System Template.
 * 
 * @author Manego
 * 
 */
@Controller
@RequestMapping(value = "/common/batchStatus")
public class CST33050BatchStatusController extends CommonBaseController {

	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33050BatchStatusController.class);
	
	/** A batch status screen name. */
	final String viewName = "WST33050";
	
	/** A report name use by this screen to download the report. */
	final String reportName = "Batch Status";
	
	/** A batch status service. */
	@Autowired
	protected IST33050BatchStatusService service;
	
	/** A project code. */
	@Value("${projectCode}")
	private String systemName;

	/**
	 * Initialize all search parameters
	 * <ul>
	 * <li>Project Code field searches for all available in Batch Master table
	 * <li>Request/Run Date is set the the CURRENT DATE
	 * <li>Batch ID field is blank
	 * <li>Batch Name field is blank
	 * <li>Request By field is blank
	 * <li>Detail part (detail table, operation and navigation button) are
	 * invisible.
	 * </ul>
	 * 
	 * @param request A http request object.
	 * @return A {@link ModelAndView} instance.
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView initial(HttpServletRequest request) {
		
		Payload payload = new XmlPayload();
		ModelAndView mv = new ModelAndView(viewName);
		final CSC22110UserInfo userInfo = getUserInSession(request);
		try{
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setStatus(ServiceStatus.OK);

			String defaultRequestDate = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT)
										.format(new Date());
			mv.addObject("defaultRequestDate", defaultRequestDate);
		} catch (final Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + e.toString()
					+ "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		
		mv.addObject("payload", payload);
		return mv;
	}
	
	/**
	 * Performs the search operation
	 * <ol>
	 * <li>Validate the search criteria</li>
	 * <li>Display only the latest status message for each Batch ID. (Refer to
	 * TB_L_BATCH_STATUS_LOG table SEQ_NO field)</li>
	 * </br> Order of status (from highest to lowest)
	 * <ul>
	 * <li>1. SUCCESSFULLY COMPLETED</li>
	 * or
	 * <li>1. PROBLEM ENCOUNTERED</li>
	 * <li>2. PROCESSING</li>
	 * <li>3. POSTED FOR WORK</li>
	 * <li>4. QUEUE</li>
	 * 
	 * </ul>
	 * <li>Displays the result on the detail part of the screen. Search
	 * operation should look on either the QUEUE or STATUS table.
	 * 
	 * <li>If no records match the criteria Show waring message MSG
	 * =MSTD0059AERR:No data found
	 * </ol>
	 * 
	 * @param form A {@link CST33050BatchStatusForm} object.
	 * @param request A http request object.
	 * @param context A request context object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object searchBatchStatus(final CST33050BatchStatusForm form,
			final HttpServletRequest request, final RequestContext context) {
		
		logger.info("Searching Information.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		// list the batch master details for given search.
		final List<?> infoList = service.listBatchStatus(form.getBatchId(), form.getBatchName(), form.getProjectCode(), 
				form.getRequestDate(), form.getRequestBy());
		payload.setBatchStatusList(infoList);
		return payload;
	}
	
	/**
	 * Display the detailed log's.
	 * <ol>
	 * <li>User checks one item in the Detail table.
	 * <li>Proceed with Detail opreation On click of the [Detail link], the page
	 * will go to display screen by sent parameter. The configuration to know
	 * the log monitor screen define in TB_M_SYSTEM table. </br>User maintain
	 * data for link between Batch Status and Log Monitoring Screen </br>This is
	 * the condition that necessary for maintain this category of data
	 * <ul>
	 * <li>Category = BFW
	 * <li>Sub Category = APP_LOG_URL
	 * <li>code = Field to combine the key for use to link(PRJCD_BATCHID)
	 * <li>value = URL for Log Monitoring Screen and parse parameter for sent to
	 * Log Monitoring screen to display Log details of selected batch status
	 * screen use parameters like application id etc.
	 * </ul>
	 * <li>Log Monitoring don't have any detail for Batch status, prompt an
	 * error message MSG = MSTD1066AERR : No Record Found on Log Monitoring
	 * </ol>
	 * 
	 * @param form A {@link CST33050BatchStatusForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value="/details", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object detailsLog(final CST33050BatchStatusForm form, 
											  HttpServletRequest request) {
		Payload payload = new XmlPayload();
		final CSC22110UserInfo userInfo = getUserInSession(request);
		try {
			final String appLogsUrl = service.detailedBatchStatus(form.getBatchId());
			final String path = appLogsUrl.contains("?") ? appLogsUrl.substring(0, appLogsUrl.indexOf("?")) : appLogsUrl;
			final UriComponentsBuilder uri = UriComponentsBuilder.fromPath(path);
			
			// define the query parameters.
			if(appLogsUrl.contains("?")){
				String query = appLogsUrl.substring(appLogsUrl.indexOf("?") + 1, appLogsUrl.length());
				// remove the parameters if the already defined in url.
				query = query.replace("appId=", "").replace("dateFrom=", "").replace("dateTo=", "").replace("searchOnLoad=", "");
				uri.query(query);
			}
			
			// add the application id parameter.
			if(!Strings.isNullOrEmpty(form.getAppId()))
				uri.queryParam("appId", form.getAppId());
			
			// add the from date, to data parameters.
			if(!Strings.isNullOrEmpty(form.getRequestDate()) && NumberUtils.isNumber(form.getRequestDate())){
				final String reqDate = new DateTime(Long.parseLong(form.getRequestDate())).toString("dd/MM/yyyy");
				uri.queryParam("dateFrom", reqDate);
				uri.queryParam("dateTo", reqDate);
			}
			uri.queryParam("searchOnLoad", "true");
			String url = uri.build(true).toUriString();
			url = url.startsWith("http:/") ? url.replaceFirst("/", "//") : "http://" + url;
			payload.setStatus(ServiceStatus.OK);
			payload.setAppLogURL(url);
		} catch (final SystemDoesNotExistsException sdnee) {
			final String msg = messageSource.getMessage(
					CST30000Messages.ERROR_SYSTEM_NOT_EXISTS, 
					new String[] { sdnee.getMessage(), "TB_M_SYSTEM" },
					RequestContextUtils.getLocale(request));
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + msg
					+ "\r\n" + sdnee.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(msg);
		} catch (final SystemIsNotActiveException sinae) {
			final String msg = messageSource.getMessage(
					CST30000Messages.ERROR_INACTIVE_SYSTEM,
					new String[] { sinae.getMessage(), "TB_M_SYSTEM" },
					RequestContextUtils.getLocale(request));
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + msg
					+ "\r\n" + sinae.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(msg);
		} catch (final Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + e.toString()
					+ "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}

		return payload;
	}
	
	/**
	 * The the batch queue status.
	 * <ol>
	 * <li>User checks one item in the Detail table.
	 * 
	 * <li>Proceed with Cancel operation <br>
	 * Prompt a confirmation message for cancel operation Pop up message to
	 * confirm. (MSTD0001ACFM : Are you sure you want to cancel the record ?)
	 * <ul>
	 * <li>If confirmation is approved
	 * <li>If Status of item is not Queue , prompt an error message MSG =
	 * MSTD1006AERR: Can not cancel {0},{1}.
	 * <li>Then Delete all the batch records in Queue table selected using the
	 * QUEUE_NO as the key.
	 * </ul>
	 * <li>After cancel successfully, go to previous mode.
	 * <li>Display message MSG =MSTD0088AINF: Cancellation for {0} is completed
	 * successfully
	 * </ol>
	 * 
	 * @param form A {@link CST33050BatchStatusForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value="/cancel",method=RequestMethod.DELETE, produces = "application/json")
	public @ResponseBody Object cancelBatchStatus(HttpServletRequest request, final CST33050BatchStatusForm form) {
		Payload payload = new XmlPayload();
		final CSC22110UserInfo userInfo = getUserInSession(request);
		try {
			ServiceStatus actionResult = service.cancelBatchStatus(form.getBatchNo(), form.getBatchId(), null);
			
			payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
			
			if(actionResult == ServiceStatus.OK) {
				payload.setStatus(actionResult);
				payload.addInfoMessage(messageSource.getMessage(
						CST30000Messages.INFO_CANCELATION_SUCCESS, new String[]{form.getBatchId()} ,RequestContextUtils.getLocale(request)));
			}else if(actionResult == ServiceStatus.NG){
				payload.setStatus(actionResult);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_CANNOT_CANCEL, new String[]{"record"," Status must be On Queue."},
						RequestContextUtils.getLocale(request)));
			}else{
				payload.setStatus(ServiceStatus.NG);
			}
		} catch (Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + e.toString()
					+ "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}

		return payload;
	}
	
	/**
	 * Perform the Batch status delete operation.
	 * <ol>
	 * <li>Use select a one records and clicks to Delete button
	 * <li>Pop up message to confirm. (MSTD0001ACFM : Are you sure you want to
	 * delete the record ?)
	 * <ul>
	 * <li>If confirmation is approved
	 * <ul>
	 * <li>If Status of item to delete is Queue or posted-for-work or
	 * Processing, prompt an error message MSG = MSTD1005AERR :Can not delete
	 * {0}.
	 * <ul></li>
	 * </ul>
	 * <li>Delete all the batch records in Status table selected using the
	 * SEQ_NO as the key.
	 * <li>After delete successfully, go to previous mode.
	 * <li>Display message MSG = MSTD0090AINF: Deletion process is completed
	 * successfully.
	 * </ol>
	 * 
	 * @param form A {@link CST33050BatchStatusForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public Object deleteBatchMaster(final HttpServletRequest request,
			final CST33050BatchStatusForm form) {
		Payload payload = new XmlPayload();
		
		final CSC22110UserInfo userInfo = getUserInSession(request);
		
		// deleting batch master details.
		try {
			Date updateDate = null;
			if(!StringUtils.isEmpty(form.getUpdateDate()) && NumberUtils.isNumber(form.getUpdateDate())){
				final DateTime dtUpdateDate = new DateTime(Long.parseLong(form.getUpdateDate()));
				updateDate = dtUpdateDate.toDate();
			}
			
			final ServiceStatus actionResult = service.deleteBatchStatus(form.getBatchNo(), form.getBatchId(),
					updateDate);
			
			payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
			
			if(actionResult == ServiceStatus.OK) {
				payload.setStatus(actionResult);
				payload.addInfoMessage(messageSource.getMessage(
						CST30000Messages.INFO_DELETION_SUCCESS, new String[]{form.getBatchId()} ,RequestContextUtils.getLocale(request)));
			}else if(actionResult == ServiceStatus.NG){
				payload.setStatus(actionResult);
				payload.addErrorMessage(messageSource.getMessage(
						CST30000Messages.ERROR_CANNOT_DELETE, new String[]{"record"," status should not be On Queue or Processing."},
						RequestContextUtils.getLocale(request)));
			}else{
				payload.setStatus(ServiceStatus.NG);
			}
		} catch (final ConcurrencyException | SystemDoesNotExistsException e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId()
					+ "}  Error message from common Library: "
					+ CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK,
					new String[] {}, RequestContextUtils.getLocale(request)));
		} catch (final Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_STATUS + "}{"
					+ userInfo.getUserId() + "}  Error = " + e.toString()
					+ "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() },
					RequestContextUtils.getLocale(request)));
		}
		
		return payload;
	}
	
	/**
	 * Function data is downloaded to the excel format.
	 * 
	 * @param form A {@link CST33050BatchStatusForm} object.
	 * @param request A http request object.
	 * @param response A http response object.
	 * @return A {@link Payload} instance.
	 * @throws IOException If fails to write excel file.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody
	Object downloadBatchStatus(final CST33050BatchStatusForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		logger.info("Downloading Information.");
		Payload payload = null;

		HSSFWorkbook workbook = null;
		try {
			final DateTime dt = DateTime.now();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyyMMddHHmmss");

			workbook = service.listBatchStatusToExcel(form.getBatchId(), form.getBatchName(), form.getProjectCode(), 
				form.getRequestDate(), form.getRequestBy());

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ reportName + "_" + format.format(dt.toDate()) + ".xls");
			workbook.write(response.getOutputStream());
		} catch (final SystemDoesNotExistsException
				| UnableToCreateExcelForDowloadException e) {

			payload = new XmlPayload();
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));

			response.setContentType("text/plain");
			logger.error("Batch Master to download does not exists.");
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
}
