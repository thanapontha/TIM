/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.web.master
 * Program ID 	            :  CST33060Controller.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  October 10, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.web.master;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST33060SystemMasterService;
import th.co.toyota.application.web.CommonBaseController;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.util.JsonStringToObjectConverter;
import th.co.toyota.bw0.web.master.form.SystemMasterForm;
import th.co.toyota.bw0.web.master.service.SystemMasterService;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;

import com.google.common.base.Strings;

@Controller
@RequestMapping("master/systemMaster")
public class SystemMasterController extends CommonBaseController {
	private static final String VIEW_NAME = "WST33060";

	@Autowired
	private JsonStringToObjectConverter jsonConverter;
	
	@Autowired
	private SystemMasterService service;
	
	@Autowired
	private IST33060SystemMasterService serviceSt3;
	
	SystemMasterController(){
		this.logger = LoggerFactory.getLogger(SystemMasterController.class);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initial(HttpServletRequest request, SystemMasterForm form) {
		logger.info("Initial form is initated.");
		
		ModelAndView mv = new ModelAndView(VIEW_NAME);
		Payload payload = new XmlPayload();
		ServiceStatus status = ServiceStatus.OK;
		try {	
			payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
			payload.setStatus(ServiceStatus.OK);
			CSC22110UserInfo userInfo = getUserInSession(request);
			
			mv.addObject(AppConstants.MV_USER_COMPANY, this.getUserCompany(userInfo));
			mv.addObject(AppConstants.MV_USER, userInfo);
			mv.addObject(AppConstants.MV_FORM, form);
			mv.addObject(AppConstants.MV_PAYLOAD, payload);
			
			int rowsPerPage = commonRepository.getRowPerPage(VIEW_NAME);
			form.setRowsPerPage(rowsPerPage);
			
			service.loadCombobox(form);
		}catch (CommonErrorException e){
			logger.error(ExceptionUtils.getStackTrace(e));
			status = ServiceStatus.NG;
			payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			status = ServiceStatus.NG;
			payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.getMessage() },
					RequestContextUtils.getLocale(request)));
		}
		payload.setStatus(status);
		return mv;
	}
	
	@RequestMapping(value="/search", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object searchData(SystemMasterForm form, HttpServletRequest request, RequestContext context) {
		logger.info("Searching Information.");
		
		Payload payload = new XmlPayload();
		ServiceStatus status = ServiceStatus.OK;
		try{
			Set<ConstraintViolation<SystemMasterForm>> errors = validator.validate(form);
			List<String> errorList = form.validate(messageSource, RequestContextUtils.getLocale(request), AppConstants.ACTION_SEARCH, payload);

			if ((!errors.isEmpty()) || (!errorList.isEmpty())) {
				errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
				status = ServiceStatus.NG;
				payload.addErrorMessages(errorList);
			}else{	
				payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
				boolean isFound = service.searchAllData(form, payload);
				
				service.loadCombobox(form);
				List<ComboValue> subCategoryList = service.loadSubCategory(form.getCategorySearch());
				form.setSubCategoryList(subCategoryList);
				payload.setObjectForm(form);
				
				form.setMessageResult(request.getParameter("messageResult"));
				if (!isFound && Strings.isNullOrEmpty(form.getMessageResult())) {
					status = ServiceStatus.NG;
					payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, new String[] {},
							Locale.getDefault()));
				}
			}
		} catch (CommonErrorException e){
			logger.error(ExceptionUtils.getStackTrace(e));
			status = ServiceStatus.NG;
			payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			status = ServiceStatus.NG;
			payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.getMessage() },
					RequestContextUtils.getLocale(request)));
		}
		payload.setStatus(status);
		return payload;
		
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object deleteObject(@RequestParam("dataList[]") String[] objects, 
											 @RequestParam("categorySearch") String categorySearch,
											 @RequestParam("subCategorySearch") String subCategorySearch,  
											 @RequestParam("codeSearch") String codeSearch,  
											 SystemMasterForm form, HttpServletRequest request){
		logger.info("Delete UnitPlantMaster");
		Payload payload = new XmlPayload();
		List<String> errorList = new ArrayList<>();
		List<String> warnList = new ArrayList<>();
		try{
			payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
			Set<ConstraintViolation<SystemMasterForm>> errors = validator.validate(form);
			
			Object[] objChk = service.deleteObject(form, objects);
			errorList = (List<String>)objChk[0];
			warnList = (List<String>)objChk[1];
			if (!errors.isEmpty() || !errorList.isEmpty()){
				errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessages(errorList);
			}else{
				if(!warnList.isEmpty()){
					payload.addWarningMessages(warnList);
				}
				String message = messageSource.getMessage(CST30000Messages.INFO_DELETION_SUCCESS, null, RequestContextUtils.getLocale(request));
				
				form.setCategorySearch(categorySearch);
				form.setSubCategory(subCategorySearch);
				form.setCodeSearch(codeSearch);
				
				service.searchAllData(form, payload);
				form.setMessageResult(message);
				payload.setStatus(ServiceStatus.OK);
				payload.addInfoMessage(message);

			}
		} catch (CommonErrorException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			String message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.getMessage() },
					RequestContextUtils.getLocale(request));
			payload.addErrorMessage(message);
		}
		return payload;
	}
	
	@RequestMapping(value="/submitAdd", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object submitAddObject(SystemMasterForm form, HttpServletRequest request) {
		Payload payload = new XmlPayload();
		try{			
			payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
			Set<ConstraintViolation<SystemMasterForm>> errors = validator.validate(form);
			List<String> errorList = form.validate(messageSource, RequestContextUtils.getLocale(request), AppConstants.ACTION_SAVE_ADD, payload);
			
			if (!errors.isEmpty() || !errorList.isEmpty()){
				errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessages(errorList);
			}else{					
				errorList = service.submitAddObject(form, getUserInSession(request));
				if ((!errors.isEmpty()) || (!errorList.isEmpty())) {
					errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
					payload.setStatus(ServiceStatus.NG);
					payload.addErrorMessages(errorList);
				} else {
					form.setCategorySearch(form.getCategory());
					
					payload.setObjectForm(form);
					String message = messageSource.getMessage(CST30000Messages.INFO_SAVE_SUCCESSFUL, null,
							RequestContextUtils.getLocale(request));
					payload.setStatus(ServiceStatus.OK);
					payload.addInfoMessage(message);
					form.setMessageResult(message);
					
					service.loadCombobox(form);
					List<ComboValue> subCategoryList = service.loadSubCategory(form.getCategorySearch());
					form.setSubCategoryList(subCategoryList);
					payload.setObjectForm(form);
				}
			}
		} catch (CommonErrorException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			if (Strings.isNullOrEmpty(e.getDisplayMessage())) {
				payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
			} else {
				payload.addErrorMessage(e.getDisplayMessage());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.toString() },
					RequestContextUtils.getLocale(request)));
		}
		return payload;
	}
	
	@RequestMapping(value="/submitEdit", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object submitEditObject(SystemMasterForm form, HttpServletRequest request) {
		logger.info("Edit UnitPlantMaster");
		Payload payload = new XmlPayload();
		try {
			payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
			
			form.setSubCategorySearch(form.getSubCategory());
			form.setCategorySearch(form.getCategory());
			
			Set<ConstraintViolation<SystemMasterForm>> errors = validator.validate(form);
			List<String> errorList = form.validate(messageSource, RequestContextUtils.getLocale(request), AppConstants.ACTION_SAVE_EDIT, payload);

			if (!errors.isEmpty() || !errorList.isEmpty()){
				errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessages(errorList);
			}else{
				Object[] objChk = service.submitEditObject(form, getUserInSession(request));
				errorList = (List<String>)objChk[0];
				List<String> warnList = (List<String>)objChk[1];
				if (!errors.isEmpty() || !errorList.isEmpty()){
					errorList.addAll(processErrorMessageFromValidator(errors.toArray(), RequestContextUtils.getLocale(request), new SystemMasterForm()));
					payload.setStatus(ServiceStatus.NG);
					payload.addErrorMessages(errorList);
				}else{
					if(!warnList.isEmpty()){
						payload.addWarningMessages(warnList);
					}
					payload.setObjectForm(form);
					String message = messageSource.getMessage(CST30000Messages.INFO_SAVE_SUCCESSFUL, null,
							RequestContextUtils.getLocale(request));
					payload.setStatus(ServiceStatus.OK);
					payload.addInfoMessage(message);
					form.setMessageResult(message);
				}
			}
		} catch (CommonErrorException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			if (Strings.isNullOrEmpty(e.getDisplayMessage())) {
				payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
			} else {
				payload.addErrorMessage(e.getDisplayMessage());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.toString() },
					RequestContextUtils.getLocale(request)));
		}
		return payload;

	}
	
	
	@RequestMapping(value="/categoryListChange", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object categoryListChange(@RequestParam ("categorySearch") String categorySearch, SystemMasterForm form, HttpServletRequest request) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(VIEW_NAME, payload, RequestContextUtils.getLocale(request));
		List<ComboValue> subCategoryList = new ArrayList<>();
		try {
			subCategoryList = service.loadSubCategory(categorySearch);
		} catch (CommonErrorException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault()));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { e.getMessage() }, RequestContextUtils.getLocale(request)));
		}
		return 	subCategoryList;
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces = "text/plain")
	public @ResponseBody Object  downloadSystems(SystemMasterForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.info("Downloading Information.");
		Payload payload = null;

		HSSFWorkbook workbook = null;
		try {
			DateTime dt = DateTime.now();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

			workbook = serviceSt3.listSystemMasterInfoToExcel(form.getCategorySearch(), form.getSubCategorySearch(), form.getCodeSearch());
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ "SystemMaster" + "_" + format.format(dt.toDate()) + ".xls");
			workbook.write(response.getOutputStream());
		} catch (SystemDoesNotExistsException | UnableToCreateExcelForDowloadException e) {
			
			payload = new XmlPayload();
			payload = populatePayloadForDisplay(VIEW_NAME, payload,
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
