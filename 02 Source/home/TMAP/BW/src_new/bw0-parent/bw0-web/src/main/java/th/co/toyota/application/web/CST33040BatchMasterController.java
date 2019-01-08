/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST33040BatchMasterController.java
 * Program Description	    :  Batch Master screen controller.
 * Environment	 	        :  Java 7
 * Author					:  Manego
 * Version					:  1.0
 * Creation Date            :  Aug 2, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

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

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST33040BatchMasterService;
import th.co.toyota.application.web.form.CST33040BatchMasterForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.BatchMaster;

/**
 * The On Demand Batch Master Screen would be used to register On Demand batch
 * programs that will be managed by the TMAP Batch Framework , Operations such
 * as Add,Delete and Edit can be on batch online screen , The screens in the
 * TMAP Standard System Template
 * 
 * @author Manego
 * 
 */
@Controller
@RequestMapping(value = "/master/batchMaster")
public class CST33040BatchMasterController extends CommonBaseController {
	
	/** A file logger instance. */
	final Logger logger = LoggerFactory
			.getLogger(CST33040BatchMasterController.class);
	
	/** A batch master view name. */
	final String viewName = "WST33040";
	
	/** A report name use by batch master screen to download. */
	final String reportName = "BatchMaster";

	/** Batch master service. */
	@Autowired
	private IST33040BatchMasterService service;

	/** A system name. */
	@Value("${projectCode}")
	private String systemName;
	
	/**
	 * Initial Operation (Screen open)
	 * 
	 * @param request A http request object.
	 * @return A {@link ModelAndView} object.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initials(final HttpServletRequest request) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		// firstly, define the JSON object to display priority & concurrency on screen.
		final Object priorities = service.retrieveBatchMasterPriority();
		final Object concurrencies = service.retrieveBatchMasterConcurrency();

		String priorityJSON = "";
		String concurrencyJSON = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			priorityJSON = mapper.writeValueAsString(priorities);
			concurrencyJSON = mapper.writeValueAsString(concurrencies);
		} catch (Exception e) {
			logger.warn("Error encountered in JSON conversion.", e);
		}

		// define the module and view for initail screen.
		final ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("payload", payload);
		mv.addObject("priorityJSON", priorityJSON);
		mv.addObject("concurrencyJSON", concurrencyJSON);
		return mv;
	}

	/**
	 * Performs the search operation The value inputed in Project Code
	 * textbox,Batch ID,Batch Name can be available to retrieve in capital
	 * letter and in small letter.
	 * <p>
	 * Displays the result on the detail part of the screen
	 * 
	 * @param form A {@link CST33040BatchMasterForm} object.
	 * @param request A http request object.
	 * @param context A request context.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	Object searchBatchMaster(final CST33040BatchMasterForm form,
			final HttpServletRequest request, final RequestContext context) {
		logger.info("Searching Information.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		// list the batch master details for given search.
		final List<BatchMaster> infoList = service.listBatchInfo(
				form.getBatchId(), form.getBatchName(), form.getProjectCode(),
				NumberUtils.toInt(form.getPriorityLevel(), -1),
				NumberUtils.toInt(form.getConcurrency(), -1));

		payload.setBatchMasterList(infoList);
		return payload;
	}

	/**
	 * add the batch master details action will performs:
	 * <ol>
	 * <li>Validate inputted value.
	 * <ul>
	 * <li>if inputted Project Code includes invalid character, Formating, 'the
	 * system will display error message. MSG = MSTD0043AERR: Invalid {Project
	 * Code}.
	 * <li>If Project code is Lower Case Letter '- Convert Project Code
	 * Character in the Text Box to Upper Case
	 * <li>If inputted Batch ID includes invalid character, Formating, 'the
	 * system will display error message. MSG = MSTD0043AERR: Invalid {Batch
	 * ID}.
	 * <li>If inputted Batch Name includes invalid character, Formating, 'the
	 * system will display error message. MSG = MSTD0043AERR: Invalid {Batch
	 * Name}.
	 * </ul>
	 * 
	 * <li>Duplicate Check
	 * <ul>
	 * <li>If inputted Project Code is duplicated with existing data, the system
	 * will display error message </br> MSG = MSTD0039AERR: Duplication found
	 * for {Project Code} = {Project Code}.
	 * <li>If inputted Batch ID is duplicated with existing data, the system
	 * will display error message </br> MSG = MSTD0039AERR: Duplication found
	 * for {Batch ID} = {Batch ID}.
	 * </ul>
	 * <li>Insert Create By From Current user session
	 * <li>Insert Create Date from system date
	 * <li>Save new data in the DB.
	 * <li>After add successfully, display message (MSTD0101AINF: Saving data is
	 * completed successfully.)
	 * </ol>
	 * 
	 * @param form A {@link CST33040BatchMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public Object addBatchMaster(HttpServletRequest request,
			CST33040BatchMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		// verify validation errors.
		final Set<ConstraintViolation<CST33040BatchMasterForm>> errors = validator
				.validate(form);
		if (errors.size() > 0) {
			logger.error("Form validators returned {} number of errors.",
					errors.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request),
					new CST33040BatchMasterForm()));
			return payload;
		}

		// saving the batch master details in DB.
		final CSC22110UserInfo userInfo = getUserInSession(request);
		try {
			service.addBatchMasterInfo(form.getBatchId(),
					form.getProjectCode(), form.getBatchName(),
					NumberUtils.toInt(form.getPriorityLevel()),
					NumberUtils.toInt(form.getConcurrency()), 0,
					form.getOwner(), form.getShell(), form.getSupportId(),
					userInfo.getUserId());

			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SAVE_SUCCESSFUL, new String[] {},
					RequestContextUtils.getLocale(request)));
		} catch (final SystemAlreadyExistsException e) {
			final String msg = messageSource.getMessage(
					CST30000Messages.ERROR_SYSTEM_ALREADY_EXISTS, new String[] {
							form.getProjectCode() + "-" + form.getBatchId(),
							form.getBatchName() },
					RequestContextUtils.getLocale(request));
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
					+ userInfo.getUserId()
					+ "}  Error message from common Library: "
					+ msg);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(msg);
		} catch (Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
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
	 * Delete data in the DB.
	 * <ul>
	 * <li>If concurrency error occurs, the system will display error message.
	 * MSG = MSTD0115AERR: Error in Delete because of concurrency check.
	 * <li>If any error occurs, the system will display error message. MSG =
	 * MSTD0115AERR: Undefine error.
	 * </ul>
	 * 
	 * @param form A {@link CST33040BatchMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public Object deleteBatchMaster(final HttpServletRequest request,
			final CST33040BatchMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		final CSC22110UserInfo userInfo = getUserInSession(request);
		
		// deleting batch master details.
		try {
			final DateTime dtUpdateDate = new DateTime(Long.parseLong(form
					.getUpdateDate()));
			service.deleteBatchMasterInfo(form.getProjectCode(),
					form.getBatchId(), dtUpdateDate.toDate());

			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_DELETION_SUCCESS, null,
					RequestContextUtils.getLocale(request)));
		} catch (final ConcurrencyException | SystemDoesNotExistsException e) {
			final String msg = messageSource.getMessage(
					CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK,
					new String[] {}, RequestContextUtils.getLocale(request));
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
					+ userInfo.getUserId()
					+ "}  Error message from common Library: "
					+ msg);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(msg);
		} catch (final Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
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
	 * Update the batch master details in db.
	 * <ul>
	 * <li>If concurrency error occurs, the system will display error message.
	 * MSG = MSTD0115AERR: Error in Delete because of concurrency check.
	 * <li>If any error occurs, the system will display error message. MSG =
	 * MSTD0115AERR: Undefine error.
	 * </ul>
	 * 
	 * @param form A {@link CST33040BatchMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Object updateBatchMaster(HttpServletRequest request,
			CST33040BatchMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		// verify the any validation errors.
		final Set<ConstraintViolation<CST33040BatchMasterForm>> errors = validator
				.validate(form);
		if (errors.size() > 0) {
			logger.error("Form validators returned {} number of errors.",
					errors.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request),
					new CST33040BatchMasterForm()));
			return payload;
		}

		// no validation errors, updating batch master details.
		final CSC22110UserInfo userInfo = getUserInSession(request);
		try {
			final DateTime dtUpdateDate = new DateTime(Long.parseLong(form
					.getUpdateDate()));

			service.updateBatchMasterInfo(form.getBatchId(),
					form.getProjectCode(), form.getBatchName(),
					NumberUtils.toInt(form.getPriorityLevel()),
					NumberUtils.toInt(form.getConcurrency()), form.getOwner(),
					form.getShell(), form.getSupportId(), userInfo.getUserId(),
					dtUpdateDate.toDate());
			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SAVE_SUCCESSFUL,
					new String[] { "User Information" },
					RequestContextUtils.getLocale(request)));
		} catch (final ConcurrencyException | SystemDoesNotExistsException e) {
			final String msg = messageSource.getMessage(
					CST30000Messages.ERROR_UPDATE_CONCURRENCY_CHECK,
					new String[] {}, RequestContextUtils.getLocale(request));
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
					+ userInfo.getUserId()
					+ "}  Error message from common Library: "
					+ msg);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(msg);
		} catch (final Exception e) {
			logger.error("{" + systemName + "}{"
					+ CST30000Constants.FUNCTION_ID_BATCH_MASTER + "}{"
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
	 * Function data is download to the excel format.
	 * 
	 * @param form A {@link CST33040BatchMasterForm} object.
	 * @param request A http request object.
	 * @param response A http response object.
	 * @return A {@link Payload} instance.
	 * @throws IOException If excel write fails.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody
	Object downloadBatchMasters(final CST33040BatchMasterForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		logger.info("Downloading Information.");
		Payload payload = null;

		HSSFWorkbook workbook = null;
		try {
			final DateTime dt = DateTime.now();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyyMMddHHmmss");

			workbook = service.listBatchMasterInfoToExcel(form.getBatchId(),
					form.getBatchName(), form.getProjectCode(),
					NumberUtils.toInt(form.getPriorityLevel(), -1),
					NumberUtils.toInt(form.getConcurrency(), -1));

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
