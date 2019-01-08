/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST33060SystemMasterController.java
 * Program Description	    :  System master controller.
 * Environment	 	        :  Java 7
 * Author					:  danilo
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import th.co.toyota.application.service.IST33060SystemMasterService;
import th.co.toyota.application.web.form.CST33060SystemMasterForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

/**
 * This screen is used to add, edit, delete and download the system master. User
 * can select which system master is shown by the specified criteria.
 * 
 * @author danilo
 * 
 */
@Controller
@RequestMapping(value = "/master/systemMasterOld")
public class CST33060SystemMasterController extends CommonBaseController {
	
	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33060SystemMasterController.class);
	
	/** A system master view name. */
	final String viewName = "WST33060";
	
	/** A report name use by this screen. */
	final String reportName = "SystemMaster";
	
	/** System master service. */
	@Autowired
	private IST33060SystemMasterService service;

	/** A spring form validator. */
	@Autowired
	protected Validator validator;

	/**
	 * Initial screen for system master.
	 * <ul>
	 * <li>Display Category ComboBox with CATEGORY field from DB (Default is
	 * <Select>) and Code textbox with "blank".
	 * <li>Display SubCategory ComboBox with &lt;All&gt;.
	 * </ul>
	 * 
	 * @param request A http request object.
	 * @return A {@link ModelAndView} instance.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initials(HttpServletRequest request) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		Object category = listCategory();
		
		String categoryJSON = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			categoryJSON = mapper.writeValueAsString(category);
		} catch (Exception e) {
			logger.error("Error encountered in JSON conversion.", e);
		}
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("payload", payload);
		mv.addObject("categoryJSON", categoryJSON);
		return mv;
	}

	/**
	 * Search the system master details:
	 * <ul>
	 * <li>It validate inputed value. If inputed Code includes special
	 * characters (!@#$%^&*) the system will display error message.
	 * (MSTD0043AERR: Invalid Code : )
	 * <li>It get value from critEria field and search data from DB and display
	 * result in the data grid.
	 * <li>If search result is No data, the system will display error message.
	 * (MSTD0059AERR: No data found.)
	 * </ul>
	 * 
	 * @param form A {@link CST33060SystemMasterForm} object.
	 * @param request A http request object.
	 * @param context A request context.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping( method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object searchSystem(CST33060SystemMasterForm form,HttpServletRequest request,RequestContext context) {
		logger.info("Searching Information.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		List<SystemInfo> infoList = service.listSystemMasterInfo(form.getCategory()
					, form.getSubCategory(), form.getCode());
		
		payload.setListCategory(infoList);
		return payload;
	}
	
	/**
	 * List out the all category and sub-categories.
	 * 
	 * @return A {@link HashMap} category-sub category map.
	 */
	@RequestMapping( value = "/listCategory", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody Object listCategory() {
		List<SystemInfoId> list = service.retrieveSystemMasterInfoId();
		HashMap<String, List<String>> category = new HashMap<String, List<String>>();
		for(int i = 0; i < list.size(); ++i) {
			String strCategory = list.get(i).getCategory();
			if ( ! category.containsKey(strCategory) )
					category.put(strCategory, new ArrayList<String>());
			category.get(strCategory).add(list.get(i).getSubCategory());
		}
		
		return category;
	}
	
	/**
	 * Add the new System master details, it will perform follwoing steps:
	 * <ol>
	 * <li>Category validation
	 * <ul>
	 * <li>Category is blank : the system will display error message.
	 * (MSTD0031AERR: Category should not be empty.)
	 * <li>Category includes special characters (!@#$%^&*) :system will display
	 * error message. (MSTD0043AERR: Invalid Category )
	 * <li>Category value in lower case , system will change to upper case
	 * automatically.
	 * </ul>
	 * <li>SubCategory validation
	 * <ul>
	 * <li>SubCategory is blank : the system will display error message.
	 * (MSTD0031AERR: Category should not be empty.)
	 * <li>SubCategory includes special characters (!@#$%^&*) :system will
	 * display error message. (MSTD0043AERR: Invalid Category )
	 * <li>SubCategory value in lower case , system will change to upper case
	 * automatically.
	 * </ul>
	 * <li>Code validation
	 * <ul>
	 * <li>Code is blank: system will display error message. (MSTD0031AERR: Code
	 * should not be empty.)
	 * <li>Code includes special characters (!@#$%^&*) : system will display
	 * error message. (MSTD0043AERR: Invalid Code )
	 * <li>Lower case in Code, System will change to upper case automatically.
	 * </ul>
	 * <li>Value validation
	 * <ul>
	 * <li>Value is blank: system will display error message. (MSTD0031AERR:
	 * Value should not be empty.)
	 * <li>Value includes special characters (!@#$%^&*): system will display
	 * error message. (MSTD0043AERR: Invalid Value ).
	 * </ul>
	 * <li>Others
	 * <ul>
	 * <li>Default value of Status checkbox is mark (Active).
	 * <li>If user mark Status checkbox, the value of Status is Active.
	 * <li>If user don't mark Status checkbox, the value of Status is Inactive.
	 * <li>If inputed Category, SubCategory and Code is duplicated with
	 * existing data, the system will display error message MSTD0039AERR:
	 * Duplication found for Category, SubCateogry and Code.)
	 * </ul>
	 * <li>Data of Create by gets from user session and Data of Create Date is
	 * system data.
	 * <li>Save new data in the DB. If successfully, go to previous mode.
	 * Display message (MSTD0101AINF: Saving data is completed successfully.)
	 * 
	 * 
	 * @param form A {@link CST33060SystemMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public Object add(HttpServletRequest request, CST33060SystemMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		Set<ConstraintViolation<CST33060SystemMasterForm>> errors = validator.validate(form);
		if (errors.size() > 0) {
			logger.error("Form validators returned {} number of errors.",
					errors.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST33060SystemMasterForm()));
			return payload;
		}

		try {
			CSC22110UserInfo userInfo = getUserInSession(request);
			service.addSystemMasterInfo(form.getCategory()
					, form.getSubCategory(), form.getCode(), form.getValue()
					, form.getRemark(), form.getStatus().charAt(0), userInfo.getUserId());

			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SAVE_SUCCESSFUL,
					new String[] {  }, RequestContextUtils.getLocale(request)));
		} catch (SystemAlreadyExistsException e){
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_SYSTEM_MASTER_EXISTED,
					new String[] { form.getCategory(), form.getSubCategory(), form.getCode()},
					RequestContextUtils.getLocale(request)));
			
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	/**
	 * It will delete the System master details from DB.It will perform
	 * following operations.
	 * <ol>
	 * <li>If user doesn't select any record, the system will display error
	 * message.(MSTD0105AINF: Select one record to be deleted.)
	 * <li>Pop up message to confirm. (MSTD0001ACFM : Are you sure you want to
	 * delete the record ?)
	 * <li>If concurrency error occurs, the system will display error message.
	 * (MSTD0115AERR: Error in Delete because of concurrency check.)
	 * <li>Delete data in the DB.
	 * <li>After deleted successfully, go to previous mode.
	 * <li>Display message (MSTD0090AINF: Deletion process is completed
	 * successfully.)
	 * </ol>
	 * 
	 * @param form A {@link CST33060SystemMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public Object delete(HttpServletRequest request, CST33060SystemMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		try {
			DateTime dtUpdateDate = new DateTime(Long.parseLong(form.getUpdateDate()));
			service.deleteSystemMasterInfo(form.getCategory(), form.getSubCategory(), form.getCode(), dtUpdateDate.toDate());
			
			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_DELETION_SUCCESS, null,
					RequestContextUtils.getLocale(request)));
		} catch (ConcurrencyException | SystemDoesNotExistsException e) {
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_DELETE_CONCURRENCY_CHECK,
					new String[] {},
					RequestContextUtils.getLocale(request)));
					
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString()}, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}

	/**
	 * It will update the system master details in DB, it will perform folling
	 * checks.
	 * <ol>
	 * <li>Apply validation define in @see #add(HttpServletRequest,
	 * CST33060SystemMasterForm)
	 * <li>Data of Update by gets from user session and Data of Update Date is
	 * system date.
	 * 
	 * <li>Update new data in the DB.
	 * <li>If concurrency error occurs, the system will display error message.
	 * (MSTD0114AERR: Error in Update because of concurrency check.)
	 * <li>Go to previous mode if successfully and Display message
	 * (MSTD0101AINF: Saving data is completed successfully.)
	 * </ol>
	 * 
	 * @param form A {@link CST33060SystemMasterForm} object.
	 * @param request A http request object.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Object update(HttpServletRequest request, CST33060SystemMasterForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		Set<ConstraintViolation<CST33060SystemMasterForm>> errors = validator.validate(form);
		
		if (errors.size() > 0) {
			logger.error("Form validators returned {} number of errors.",
					errors.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST33060SystemMasterForm()));
			return payload;
		}

		CSC22110UserInfo userInfo = getUserInSession(request);
		try {
			DateTime dtUpdateDate = new DateTime(Long.parseLong(form.getUpdateDate()));
			service.updateSystemMasterInfo(form.getCategory()
					, form.getSubCategory(), form.getCode(), form.getValue()
					, form.getRemark(), form.getStatus().charAt(0), userInfo.getUserId(), dtUpdateDate.toDate());
			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SAVE_SUCCESSFUL,
					new String[] { "User Information" }, RequestContextUtils.getLocale(request)));
		} catch (ConcurrencyException | SystemDoesNotExistsException e) {
			e.printStackTrace();
			logger.error(e.toString() + "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UPDATE_CONCURRENCY_CHECK,
					new String[] { },
					RequestContextUtils.getLocale(request)));
		} catch (Exception e) {
			logger.error(e.toString() + "\r\n" + e.getStackTrace());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.toString() }, RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	/**
	 * Download system master details as a excel report, it will perform
	 * following operations.
	 * <ol>
	 * <li>System data is download to the excel format (data is base-on
	 * Searching filter conditions - criteria area).
	 * <li>If user doesn't select Category ComboBox , the system will display
	 * error message.(MSTD0031AERR: Category should not be empty.)
	 * <li>If data is empty, the system will display error
	 * message.(MSTD0059AERR: No data found)
	 * <li>File Name is TB_M_SYSTEM_YYMMDDHHmm
	 * <li>Excel file is included Category,SubCategory, Code, Value, Remark,
	 * Update By, Update Date, Create By, Create Date, Status data fields from
	 * TB_M_SYSTEM table.
	 * <li>System will show dialog message (Do you want to open or save this
	 * file?) and user should select button (Open, Save or Cancel) to continue.
	 * <li>After download successfully, go to previous mode.
	 * </ol>
	 * 
	 * @param form A {@link CST33060SystemMasterForm} object.
	 * @param request A http request object.
	 * @param response A http response object.
	 * @return A {@link Payload} instance.
	 * @throws IOException If fails to write excel file.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody Object  downloadSystems(CST33060SystemMasterForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.info("Downloading Information.");
		Payload payload = null;

		HSSFWorkbook workbook = null;
		try {
			DateTime dt = DateTime.now();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

			workbook = service.listSystemMasterInfoToExcel(form.getCategory(), form.getSubCategory(), form.getCode());
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ reportName + "_" + format.format(dt.toDate()) + ".xls");
			workbook.write(response.getOutputStream());
		} catch (SystemDoesNotExistsException | UnableToCreateExcelForDowloadException e) {
			
			payload = new XmlPayload();
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			
			response.setContentType("text/plain");
			logger.error("System Master to download does not exists.");
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
