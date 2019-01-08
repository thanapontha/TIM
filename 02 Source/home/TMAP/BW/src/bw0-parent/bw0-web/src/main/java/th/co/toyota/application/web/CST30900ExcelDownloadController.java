/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST30900ExcelDownloadController.java
 * Program Description	    :  Controller for download data from database
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  Apr 7, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST30900ExcelDownloadService;
import th.co.toyota.application.web.form.CST30900ExcelDownloadForm;
import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.download.CST30091Downloader;
import th.co.toyota.st3.api.exception.EntityMappingException;
import th.co.toyota.st3.api.exception.FileDoesNotExistException;
import th.co.toyota.st3.api.exception.FileFormatInvalidException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.OverLimitException;
import th.co.toyota.st3.api.exception.PostODBFailedException;
import th.co.toyota.st3.api.exception.SequenceCodeDoesNotExistException;
import th.co.toyota.st3.api.exception.UnsupportedQuerySyntaxException;
import th.co.toyota.st3.api.model.BaseEntity;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.TableRoleMap;

import com.google.common.base.Strings;

/**
 * The Excel Screen would be used by Application adminstrators or Application
 * owner to dowload data for each table in the Database . User can choose out
 * put file format such as Excel fie format (both xls and xlsx formats), CSV
 * file format and then User can save criteria are bookmarked for keeping
 * history criteria.
 * <p>
 * This screen will be a common screen for all applications and therefore will
 * be located in a separate menu in the application menu bar selection. This
 * will be one of the screens in the Development Standard System Template.
 * 
 * 
 * @author Sira
 * 
 */
@Controller
@RequestMapping(value = "/common/excelDownload")
@SuppressWarnings("all")
public class CST30900ExcelDownloadController extends CommonBaseController {
	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST30900ExcelDownloadController.class);

	/** Excel download view name. */
	final String viewName = "WST30900";
	/** Report name use by this screen.*/
	final String reportName = "Excel Download";

	/** Form data validator. */
	protected @Autowired Validator validator;
	
	/** Excel download service. */
	private @Autowired IST30900ExcelDownloadService serviceExcelDownload;
	
	/** Downloader. */
	protected @Autowired CST30091Downloader downloader;
	
	/**
	 * Initialize all search parameters
	 * <ul>
	 * <li>System checks the role ID of login user.
	 * <li>Table / View combo box: retrieve items from "TB_M_TABLE_ROLE_MAP"
	 * Table according to user role.
	 * <li>Bookmarks combo box : retrieve items from "TB_M_SETTING_INFO"
	 * according to user role.
	 * <li>Screen display only Header Section, the Details Section will be
	 * hidden.
	 * <li>Initial values of the fields in this mode: please refer to
	 * "Item Desc" and "Screen Layout" sheets.
	 * </ul>
	 * 
	 * @param request A HttpRequest.
	 * @return {@link ModelAndView}
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initial(HttpServletRequest request) {
		logger.info("Initial form is initated.");
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
		payload.setStatus(ServiceStatus.OK);
		
		SimpleDateFormat dFmt = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT);
		DateTime date = DateTime.now();
		
		List<String> userRole = getUserRole(request);
		List<TableRoleMap> tableRoleMapValue = serviceExcelDownload.getTableList(userRole);
		List<SettingInfo> settingInfoValue = (List<SettingInfo>) listBookmarkByTable(request, "");
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("form", new CST30900ExcelDownloadForm());
		mv.addObject("payload", payload);
		mv.addObject("currentDate", dFmt.format(date.toDate()));
		mv.addObject("listTableName", tableRoleMapValue);
		mv.addObject("listSettingInfo", settingInfoValue);
		
		return mv;
	}
	
	/**
	 * Bookmarks combo box : retrieve items from "TB_M_SETTING_INFO" according
	 * to user role.
	 * 
	 * @param selectedTableName Selected table/view name.
	 * @param request A HttpRequest.
	 * @return A {@link Payload} instance
	 */
	@RequestMapping( value = "/listBookmarkByTable", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody Object listBookmarkByTable(HttpServletRequest request, String selectedTableName) {
		selectedTableName = selectedTableName==null?"":selectedTableName.trim();
		if(selectedTableName.equals("")){
			logger.info("Load bookmark list.");
		}else{
			logger.info("Load bookmark list by table name.");
		}

		return serviceExcelDownload.getBookmarkList(getUserRole(request), selectedTableName);
	}
	
	/**
	 * Performs the retrieve table or view operation
	 * <ol>
	 * <li>If Selected value in Table/View Combo box is not blank: </br> System
	 * retrieve meta data of the selected Table / View from database and display
	 * in detail list on the screen.
	 * <ul>
	 * <li>Display all Column names of target Table/View in "Field" column on
	 * the screen.
	 * <li>The Data Type of each column will be displayed in "Data Type" column
	 * on the screen.
	 * <li>For the columns which are primary key of selected Table, system will
	 * display "PK" in "PK" column on the screen.
	 * <li>For more information about items specification, please refer to
	 * "Item Desc" sheet.
	 * </ul>
	 * If metadata of selected Table/View can not be found, system displays
	 * error message and stops processing:</br> MSTD1026AERR: Table/View {name}
	 * is not Existed in current DB schema.
	 * </br>
	 * Reload Bookmarks combo box by retrieving items from "TB_M_SETTING_INFO"
	 * according to user role and selected Table/View
	 * 
	 * <li>If Selected value in Table/View Combo box is blank </br> Search
	 * criteria and search result is reset and the screen displays is changed
	 * into screen layout as Initial Mode.
	 * 
	 * <li>By default, Checkbox "Big Data? " is Checked.
	 * </ol>
	 * 
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance.
	 */
	@RequestMapping(value = "/changeTableView", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Object changeTableView(HttpServletRequest request) {
		logger.info("Load metadata of table, Change table/view combobox.");
		String modelClass = (String)request.getParameter("modelClass");
		String tableName = (String)request.getParameter("tableName");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
		
		List<SettingInfo> listTableView;
		try {
			listTableView = serviceExcelDownload.loadTableViewInfo(modelClass);
			
			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_006 by Thanapon 19/05/2015
			List<SettingInfo> listTableViewSorted = new ArrayList<SettingInfo>();
			List<SettingInfo> listTableViewPK = new ArrayList<SettingInfo>();
			List<SettingInfo> listTableViewFields = new ArrayList<SettingInfo>();
			List<SettingInfo> listTableViewBaseFields = new ArrayList<SettingInfo>();
			if(listTableView != null){
				for(int i=0; i<listTableView.size(); i++){
					SettingInfo settingObj = listTableView.get(i);
					if(settingObj.getPkOption() != null && settingObj.getPkOption().equals("PK")){
						listTableViewPK.add(settingObj);
					}else{
						boolean baseFields = false;
						Class myClass = new BaseEntity().getClass();
						for (Field field : myClass.getDeclaredFields()) {
							Column column = field.getAnnotation(Column.class);
							if (column != null) {
								String fieldName = column.name();
								if(fieldName.equals(settingObj.getFieldName())){
									listTableViewBaseFields.add(settingObj);
									baseFields = true;
									break;
								}
							}
						}
						if(baseFields == false){
							listTableViewFields.add(settingObj);
						}
					}
				}
				Collections.sort(listTableViewPK,  new Comparator<SettingInfo>() { 
						            public int compare(SettingInfo i1, SettingInfo i2) { 
						                return (i2.getFieldName().equals(i1.getFieldName()) ? 0 : 1); 
						            } 
						        }); 				
				for(int i=0; i<listTableViewPK.size(); i++){
					SettingInfo settingObj = listTableViewPK.get(i);
					listTableViewSorted.add(settingObj);
				}
				Collections.sort(listTableViewFields,  new Comparator<SettingInfo>() { 
						            public int compare(SettingInfo i1, SettingInfo i2) { 
						                return (i2.getFieldName().equals(i1.getFieldName()) ? 0 : 1); 
						            } 
						        }); 
				for(int i=0; i<listTableViewFields.size(); i++){
					SettingInfo settingObj = listTableViewFields.get(i);
					listTableViewSorted.add(settingObj);
				}
				Collections.sort(listTableViewBaseFields,  new Comparator<SettingInfo>() { 
						            public int compare(SettingInfo i1, SettingInfo i2) { 
						                return (i2.getFieldName().equals(i1.getFieldName()) ? 0 : 1); 
						            } 
						        }); 
				for(int i=0; i<listTableViewBaseFields.size(); i++){
					SettingInfo settingObj = listTableViewBaseFields.get(i);
					listTableViewSorted.add(settingObj);
				}
				
			}
			payload.setBookmarksList(listTableViewSorted);
			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_006 by Thanapon 19/05/2015
			//Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 25/05/2015
			List<SystemInfo> mandatoryFieldList = new ArrayList<SystemInfo>();
			if(listTableViewSorted !=null && listTableViewSorted.size()>0 && !Strings.isNullOrEmpty(tableName)){
				mandatoryFieldList = getMandatoryFieldList(tableName);
			}
			payload.setMandatoryFieldList(mandatoryFieldList);
			//end Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 25/05/2015
			
			if (listTableView != null && listTableView.size() > 0) {
				logger.info("Metadata of entity model class {0}. Total retrieve data {1}", modelClass, listTableView.size());
				payload.setStatus(ServiceStatus.OK);
			} else {
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_MAPPING_TABLE_MODEL, new String[] {tableName},
																	RequestContextUtils.getLocale(request)));
			}
		} catch (SQLSyntaxErrorException e) {
			logger.error(e.getMessage());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			payload.setStatus(ServiceStatus.NG);
			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
			payload.addErrorMessage(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_TABLE_VIEW_DOES_NOT_EXIST, new String[] {tableName},
					RequestContextUtils.getLocale(request)));
			//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
		} catch (Exception e) {
			final String message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
					new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			logger.error(message);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(message);
		}
	
		
		return payload;
	}

	/**
	 * Performs the retrieve bookmarks operation
	 * <ul>
	 * <li>If Selected value in Bookmarks Combo box in not blank </br> System
	 * will query data from TB_M_SETTING_INFO table using selected bookmarks id
	 * and display data on the screen
	 * 
	 * <pre>
	 * Searching criteria:																																																													
	 * 				V_SETTING_ID											=		[Sreen]. Bookmarks
	 * </pre>
	 * 
	 * </br> For more information about items specification, please refer to
	 * "Item Desc" sheet.
	 * 
	 * <li>If Selected value in Bookmarks Combo box is blank </br> Items on
	 * screen will be reset to initial values in Table / View Retrieve Mode.
	 * (Please refer to "Item Desc" sheet - section C.)
	 * 
	 * <li>By default, Checkbox "Big Data? " is Checked.
	 * </ul>
	 * 
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance.
	 */
	@RequestMapping(value = "/changeBookmark", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Object changeBookmark(HttpServletRequest request) {
		logger.info("Load bookmark when change combobox.");
		String settingId = request.getParameter("bookmarkID").toString();
		if(settingId.equals("")){
			return changeTableView(request);
		}else{
			Payload payload = new XmlPayload();
			try {
				payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
				
				List<SettingInfo> listBookmarkDetail = serviceExcelDownload.getBookmarkDetails(getUserRole(request), settingId);
				
				payload.setBookmarksList(listBookmarkDetail);
				
				//Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 25/05/2015
				List<SystemInfo> mandatoryFieldList = new ArrayList<SystemInfo>();
				if(listBookmarkDetail !=null && listBookmarkDetail.size()>0){
					SettingInfo settingInfo = listBookmarkDetail.get(0);
					String tableName = settingInfo.getTableName();
					 if(!Strings.isNullOrEmpty(tableName)){
						 mandatoryFieldList = getMandatoryFieldList(tableName);
					 }
				}
				payload.setMandatoryFieldList(mandatoryFieldList);
				//end Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 25/05/2015
				
				payload.setStatus(ServiceStatus.OK);
			} catch (Exception e) {
				final String message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
							new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
				logger.error(message);
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(message);
			}
			
			return payload;
		}
	}

	/**
	 * When user checks select value on "Save BookMarks" check box
	 * <ol>
	 * <li>If user selected neither Table/View combo box nor BookMarks combo
	 * box, system displays error message and stop processing. </br>
	 * MSTD0063AERR: No data to be saved
	 * <li>Perform validation for inputed information. If there is error, system
	 * will stop processing.
	 * <li>Perform save value on the screen insert into "tb_m_setting_info"
	 * table (Please refer to "Data Map" sheet for more details)
	 * <ul>
	 * <li>If "Report Name " is blank , system will set BookMark id =Table
	 * name_yyMMddhhmm
	 * <li>If "Report Name " has values , system will set BookMark id =Values
	 * in Report Name_yyMMddhhmm
	 * </ul>
	 * <li>Display message MSG = MSTD0081AINF : BookMark <Bookmark Name>
	 * Generation is completed successfully .
	 * <li>Set "Save BookMarks" = Unchecked
	 * <ol>
	 * @param form A {@link CST30900ExcelDownloadForm}
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance
	 */
	@RequestMapping(value = "/saveBookmark", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Object saveBookmark(HttpServletRequest request, CST30900ExcelDownloadForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));

		Set<ConstraintViolation<CST30900ExcelDownloadForm>> errors = validator.validate(form);
		List<String> errorList = serviceExcelDownload.validateForm(messageSource, RequestContextUtils.getLocale(request), form);

		if (errors.size() > 0 || errorList.size() > 0) {
			errorList.addAll(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST30900ExcelDownloadForm()));
			logger.error("Form validators returned {} number of errors.", errorList.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(errorList);
			
			return payload;
		}
		
		String newSettingId = "";
		List<SettingInfo> bookmarkList = new ArrayList<SettingInfo>();
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm");
		for (int i=0; i<form.getField().length; i++) {
			SettingInfo setting = new SettingInfo();
			
			newSettingId = form.getTableName() + "_" + format.format(new Date());
			if (form.getReportName().isEmpty() == false) {
				newSettingId = form.getReportName() + "_" + format.format(new Date());
			}
			setting.setSettingID(newSettingId);
			setting.setReportName(form.getReportName());
			setting.setFieldName(form.getDbFieldName()[i]);
			setting.setObjFieldName(form.getField()[i]);
			setting.setPkOption(form.getPk()[i]);
			setting.setDataType(form.getDataType()[i]);
			setting.setDisplayOption(form.getChkDisplayOption()[i]);
			setting.setCriteria(form.getCriteria()[i]);
			setting.setLogicalOpr(form.getOperationLogic()[i]);
			setting.setReportTitle(form.getReportTitle());
			setting.setTableName(form.getTableName());
			setting.setEntryName(form.getModelClassName());
			setting.setOrderDisp(form.getOrderDisp()[i]);
			setting.setSort(form.getSort()[i]);
			setting.setStartColumn(form.getStartColumn());
			setting.setStartRow(form.getStartRow());
			setting.setDisplayName(form.getDisplayName()[i]);
			setting.setRoleID(getUserRole(request).get(0));

			bookmarkList.add(setting);
		}
		
		boolean isSaveBookmark = serviceExcelDownload.saveBookmark(bookmarkList, 
				getUserRole(request));
		if (isSaveBookmark) {
			payload.addInfoMessage(messageSource.getMessage(
					CST30000Messages.INFO_SAVE_BOOKMARK, new String[]{newSettingId}, RequestContextUtils.getLocale(request)));
		}
		
		return payload;
	}
	
	/**
	 * User click Excel (xls/xlsl) or CSV Download button on screen
	 * <ol>
	 * <li>Perform validation for inputed information. If there is error, system
	 * will stop processing.
	 * <li>System generate the data retrieving statement from selected
	 * Table/View name and specified criteria.
	 * <li>Retrieve data from DB and check the result sizing
	 * <li>In case of of data found, system displays error message: </br>
	 * MSTD0059AERR: No data found
	 * <li>In case of existing data, system gets the number of records
	 * limitation configuration (for xls/xlsx/csv file) from standard config
	 * (standard.properties)
	 * <ul>
	 * <li>If number of result records > limitation records and Big Data check
	 * box is unchecked, system displays error message: </br> MSTD1066AERR:
	 * {Table/View with more than <limitation number> records} cannot be
	 * downloaded online. Please download using Big Data.
	 * <li>Otherwise, system displays confirmation Message: MSTD0114ACFM :
	 * {result record} Found, Do you wish to proceed? </br> If user confirms to
	 * process, go to step 2. Otherwise, system will do nothing.
	 * </ul>
	 * </ol>
	 * @param response A HttpResponse object.
	 * @param form A {link CST30900ExcelDownloadForm} form.
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance
	 */
	@RequestMapping(value = "/downloadReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object downloadReport(HttpServletRequest request, HttpServletResponse response, CST30900ExcelDownloadForm form) {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
		
		//Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 27/05/2015
		String tableName = form.getTableName();
		List<SystemInfo> listSystemInfo = serviceExcelDownload.querySystemMaster(false,tableName);
		if (listSystemInfo!=null && listSystemInfo.size()>0){ 
			String objCheck = "BATCH";
			if (Strings.isNullOrEmpty(form.getWST30900BigData())) {
				objCheck = "ONLINE";
			}
			boolean canDownload = false;
			for(int i=0; i< listSystemInfo.size(); i++){
				SystemInfo sysInfo = listSystemInfo.get(i);
				String value = sysInfo.getValue()==null?"":sysInfo.getValue().trim();
				if(value.equalsIgnoreCase(objCheck)){
					canDownload = true;
					break;
				}
			}
			if(canDownload == false){
				payload.setStatus(ServiceStatus.NG);
				if(objCheck.equals("BATCH")){
					String message = messageSource.getMessage(CST30000Messages.ERROR_DOWNLOAD_ONLINE_ONLY, 
							new String[]{tableName}, RequestContextUtils.getLocale(request));
					payload.addErrorMessage(message);		
				}else{
					String message = messageSource.getMessage(CST30000Messages.ERROR_DOWNLOAD_BATCH_ONLY, 
							new String[]{tableName}, RequestContextUtils.getLocale(request));
					payload.addErrorMessage(message);	
				}			
				return payload;
			}
		}
		//end Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_010 by Thanapon 27/05/2015
		
		//Validate input 
		Set<ConstraintViolation<CST30900ExcelDownloadForm>> errors = validator.validate(form);		
		List<String> errorList = serviceExcelDownload.validateForm(messageSource, RequestContextUtils.getLocale(request), form);

		if (errors.size() > 0 || errorList.size() > 0) {
			errorList.addAll(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST30900ExcelDownloadForm()));
			logger.error("Form validators returned {} number of errors.", errorList.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(errorList);
			
			return payload;
		}
		
		try {
			downloader.checkDefaultPath();
			int totalFound = serviceExcelDownload.getRecordCount(form);
			payload.setTotalRecord(totalFound);
			payload.setStatus(ServiceStatus.OK);
		} catch (Exception e) {
			String message = "";
			if (e instanceof NoDataFoundException ) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, 
						null, RequestContextUtils.getLocale(request));
			} else if (e instanceof ClassNotFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MAPPING_TABLE_MODEL, 
						new String[]{form.getTableName()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof OverLimitException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_EXCEL_DOWNLOAD_LIMIT, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof FileDoesNotExistException) {
				message = e.getMessage();
			} else {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			}
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(message);
		}		
		return payload;
	}
	
	/**
	 * Perform download
	 * <ol>
	 * <li>If "Report Name","Report Title,Start Row,Start Column has value </br>
	 * Set file name = "Report Name".xls/xlsx/csv </br> Set report title = "Report Title"
	 * </br> Set start row = "Start Row" </br> Set start column = "Start Column"
	 * <li>Otherwise, </br> Set file name = {Table Name_}yyMMddhhmm.xls/xlsx/csv </br>
	 * Set report title = {Table Name}
	 * <li>If "Big Data? " check box is selected.
	 * <ul>
	 * <li>System invokes ondemand download batch framework to download data in
	 * Zip file format
	 * <li>Display message MSG = MSTD4001AINF: ODB Excel posted with Document
	 * No: {generated document ID} User will go to On Demand Excel Download
	 * screen to download the generated data file with same document ID later.
	 * </ul>
	 * <li>Otherwise, system generates excel file with retrieved data then shows
	 * popup download confirmation dialog.
	 * <ul>
	 * <li>Save -- Save excel file to local computer
	 * <li>Open -- Open excel file.
	 * <li>Cancel -- Close popup
	 * </ul>
	 * </ol>
	 * 
	 * @param form A {@link CST30900ExcelDownloadForm} from.
	 * @param response A HttpResponse object.
	 * @param request A HttpRequest.
	 * @return {@link Payload} instance.
	 * @throws Exception
	 */
	@RequestMapping(value = "/confirmDownload", method = RequestMethod.POST, produces="application/json")//produces = "text/plain")
	public @ResponseBody Object confirmDownloadReport(CST30900ExcelDownloadForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload, RequestContextUtils.getLocale(request));
		
		//Validate input 
		Set<ConstraintViolation<CST30900ExcelDownloadForm>> errors = validator.validate(form);
		List<String> errorList = serviceExcelDownload.validateForm(messageSource, RequestContextUtils.getLocale(request), form);

		if (errors.size() > 0 || errorList.size() > 0) {
			errorList.addAll(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST30900ExcelDownloadForm()));
			logger.error("Form validators returned {} number of errors.", errorList.size());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessages(errorList);
			
			return payload;
		}
		
		String reportFile = "";
		try {
//			if (Strings.isNullOrEmpty(form.getWST30900BigData())) {
//				// Online download
//				reportFile = serviceExcelDownload.generateReport(form, getUserInSession(request), getUserRole(request));
//				downloader.download(reportFile, form.getFileType(), response);
//				response.flushBuffer();
//				payload = null;
//			} else {
				// ODB download
				String docId = serviceExcelDownload.requestODBReport(form, getUserInSession(request), getUserRole(request));
				payload.setStatus(ServiceStatus.OK);
				payload.addInfoMessage(messageSource.getMessage(CST30000Messages.INFO_ODB_EXCEL_POSTED, 
						new String[] {docId}, RequestContextUtils.getLocale(request)));
//			}
			
		} catch (Exception e) {
			String message = "";
			if (e instanceof NoDataFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, 
						null, RequestContextUtils.getLocale(request));
			} else if (e instanceof FileNotFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, 
						new String[]{reportFile}, RequestContextUtils.getLocale(request));
			} else if (e instanceof IOException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof ClassNotFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MAPPING_TABLE_MODEL, 
						new String[]{form.getTableName()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof UnsupportedQuerySyntaxException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof FileDoesNotExistException) {
				message = e.getMessage();
			} else if (e instanceof FileFormatInvalidException) {
				message = e.getMessage();
			} else if (e instanceof PostODBFailedException) {			
				message = e.getMessage();
			} else if (e instanceof EntityMappingException) {
				message = e.getMessage();
			} else if (e instanceof SequenceCodeDoesNotExistException) {
				message = e.getMessage();
			} else {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			}
			logger.error(message);
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(message);
		} 
		
		return payload;
	}
	
	/**
	 * Action use for online download. For more details see @see
	 * #confirmDownloadReport(CST30900ExcelDownloadForm, HttpServletRequest,
	 * HttpServletResponse)
	 * 
	 * @param form A {@link CST30900ExcelDownloadForm}
	 * @param request A HttpRequest.
	 * @param response A HttpResponse object.
	 * @return {@link Payload} instance.
	 * @throws Exception
	 */
	@RequestMapping(value = "/confirmFileDownload", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody Object confirmFileDownload(CST30900ExcelDownloadForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//Validate input 
		Set<ConstraintViolation<CST30900ExcelDownloadForm>> errors = validator.validate(form);
		List<String> errorList = serviceExcelDownload.validateForm(messageSource, RequestContextUtils.getLocale(request), form);

		if (errors.size() > 0 || errorList.size() > 0) {
			errorList.addAll(processErrorMessageFromValidator(
					errors.toArray(), RequestContextUtils.getLocale(request), new CST30900ExcelDownloadForm()));
			logger.error("Form validators returned {} number of errors.", errorList.size());
			return StringUtils.join(errorList, ",");
		}
		
		String reportFile = "";
		try {
			if (Strings.isNullOrEmpty(form.getWST30900BigData())) {
				// Online download
				reportFile = serviceExcelDownload.generateReport(form, getUserInSession(request), getUserRole(request));
				downloader.download(reportFile, form.getFileType(), response);
				response.flushBuffer();
			}
		} catch (Exception e) {
			String message = "";
			if (e instanceof NoDataFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, 
						null, RequestContextUtils.getLocale(request));
			} else if (e instanceof FileNotFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, 
						new String[]{reportFile}, RequestContextUtils.getLocale(request));
			} else if (e instanceof IOException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof ClassNotFoundException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_MAPPING_TABLE_MODEL, 
						new String[]{form.getTableName()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof UnsupportedQuerySyntaxException) {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			} else if (e instanceof FileDoesNotExistException) {
				message = e.getMessage();
			} else if (e instanceof FileFormatInvalidException) {
				message = e.getMessage();
			} else if (e instanceof PostODBFailedException) {			
				message = e.getMessage();
			} else if (e instanceof EntityMappingException) {
				message = e.getMessage();
			} else if (e instanceof SequenceCodeDoesNotExistException) {
				message = e.getMessage();
			} else {
				message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						new String[]{e.getMessage()}, RequestContextUtils.getLocale(request));
			}
			logger.error(message);
			return message;
		} 
		
		return reportFile;
	}

	/**
	 * Method use to get the current login user roles.
	 * 
	 * @param request A HttpRequest.
	 * @return List of user roles.
	 */
	private List<String> getUserRole(HttpServletRequest request) {		
		CSC22110AccessControlList roles = getAccessControlList(request);
		roles.getRoleList();
		
		return roles.getRoleList();
	}
	
	/**
	 * Get the mandatory fields from the table name.
	 * @param tableName A table/view name.
	 * @return List of {@link SystemInfo}
	 */
	private List<SystemInfo> getMandatoryFieldList(String tableName){
		List<SystemInfo> mandatoryFieldList = new ArrayList<SystemInfo>();
		List<SystemInfo> listSystemInfo = serviceExcelDownload.querySystemMaster(true, tableName);
		if (listSystemInfo!=null && listSystemInfo.size()>0){
			for(int i=0; i< listSystemInfo.size(); i++){
				SystemInfo sysInfo = listSystemInfo.get(i);
				String value = sysInfo.getValue();
				if(!Strings.isNullOrEmpty(value)){
					String[] values = value.split(";");
					for(String madaVal : values) {
						if(!Strings.isNullOrEmpty(madaVal)){
							SystemInfo sysObj = new SystemInfo();
							sysObj.setValue(madaVal);
							mandatoryFieldList.add(sysObj);								
						}
					}
				}
			}	
		}
		return mandatoryFieldList;
	}
	
}
