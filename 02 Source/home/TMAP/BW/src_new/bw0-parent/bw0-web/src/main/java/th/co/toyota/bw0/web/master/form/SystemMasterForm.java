package th.co.toyota.bw0.web.master.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import th.co.toyota.application.model.Payload;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.common.form.CommonBaseForm;
import th.co.toyota.bw0.util.ComboValue;

public class SystemMasterForm extends CommonBaseForm{
	private String categorySearch;
	private String subCategorySearch;
	private String codeSearch;
	
	private String category;
	private String subCategory;
	private String code;
	private String value;
	private String remark;
	private String status;
	
	List<ComboValue> categoryList;
	List<ComboValue> subCategoryList;
	
	private String messageResult;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getCategorySearch() {
		return categorySearch;
	}
	public void setCategorySearch(String categorySearch) {
		this.categorySearch = categorySearch;
	}
	public String getSubCategorySearch() {
		return subCategorySearch;
	}
	public void setSubCategorySearch(String subCategorySearch) {
		this.subCategorySearch = subCategorySearch;
	}
	
	public String getCodeSearch() {
		return codeSearch;
	}
	public void setCodeSearch(String codeSearch) {
		this.codeSearch = codeSearch;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<ComboValue> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(List<ComboValue> categoryList) {
		this.categoryList = categoryList;
	}
	public List<ComboValue> getSubCategoryList() {
		return subCategoryList;
	}
	public void setSubCategoryList(List<ComboValue> subCategoryList) {
		this.subCategoryList = subCategoryList;
	}
	public String getMessageResult() {
		return messageResult;
	}
	public void setMessageResult(String messageResult) {
		this.messageResult = messageResult;
	}
	/**
	 * @param messageSource
	 * @param locale
	 * @param actionSearch
	 * @param payload
	 * @return
	 */
	public List<String> validate(MessageSource messagesource, Locale locale, String action, Payload payload) throws Exception {
		List<String> errors = new ArrayList<String>();
		try{
			if(AppConstants.ACTION_SAVE_ADD.equals(action) || AppConstants.ACTION_SAVE_EDIT.equals(action)){
				
				/*
				if(StringUtils.isEmpty(this.unitModel) && AppConstants.ACTION_SAVE_ADD.equals(action)){
					errors.add(messagesource.getMessage(CST30000Messages.ERROR_MESSAGE_EMPTY_FIELD,
							new String[]{messagesource.getMessage("BW0.WBW04130.Label.UnitModel", null, null)},
								locale));
					
					payload.setFocusId("btnPopupSubsreen");
				}
				
				
				
				boolean tcFromValid = true;
				boolean tcToValid = true;
				if(FormatUtil.isValidDate(this.tcFrom) == false){
					errors.add(messagesource.getMessage(CST30000Messages.ERROR_INVALID_DATE_FORMAT,
							new String[]{messagesource.getMessage("BW0.WBW04130.Label.TCFrom", null, null), StringUtils.upperCase(AppConstants.DATE_SHOW_IN_SCREEN)},
								locale));
					tcFromValid = false;
					if(Strings.isNullOrEmpty(payload.getFocusId())){
						payload.setFocusId(TC_FROM);
					}
				}
				if(!Strings.isNullOrEmpty(this.tcTo) && FormatUtil.isValidDate(this.tcTo) == false){
					errors.add(messagesource.getMessage(CST30000Messages.ERROR_INVALID_DATE_FORMAT,
							new String[]{messagesource.getMessage("BW0.WBW04130.Label.TCTo", null, null), StringUtils.upperCase(AppConstants.DATE_SHOW_IN_SCREEN)},
								locale));
					tcToValid = false;
					if(Strings.isNullOrEmpty(payload.getFocusId())){
						payload.setFocusId(TC_TO);
					}
				}
				
				if(tcFromValid && tcToValid){
					Date tcFormChk = FormatUtil.convertStringToDate(this.tcFrom);
					Date tcToChk = FormatUtil.convertStringToDate(AppConstants.DEFULAT_DATE, AppConstants.DATE_STRING_SCREEN_FORMAT);
					if(!Strings.isNullOrEmpty(this.tcTo)){
						tcToChk = FormatUtil.convertStringToDate(this.tcTo);
					}
					
					Date currentDate = FormatUtil.getCurrentMonth();
					
					if(AppConstants.ACTION_SAVE_ADD.equals(action)){
						if(!Strings.isNullOrEmpty(this.tcFrom) && !(FormatUtil.compareDate(tcFormChk,currentDate) != -1)){ // form >= current
							errors.add(messagesource.getMessage(MessagesConstants.A_ERROR_MUST_BE_GREATER_THAN_OR_EQUAL,
									new String[]{messagesource.getMessage("BW0.WBW04130.Label.TCFrom", null, null), "Current Date"},
										locale));
							if(Strings.isNullOrEmpty(payload.getFocusId())){
								payload.setFocusId(TC_FROM);
							}
						}			
					}
				
					if((!Strings.isNullOrEmpty(this.tcFrom) && !Strings.isNullOrEmpty(this.tcTo)) && !(FormatUtil.compareDate(tcFormChk,tcToChk) != 1)){ // form <= to
						errors.add(messagesource.getMessage(MessagesConstants.A_ERROR_MUST_BE_GREATER_THAN_OR_EQUAL,
								new String[]{messagesource.getMessage("BW0.WBW04130.Label.TCTo", null, null),
											messagesource.getMessage("BW0.WBW04130.Label.TCFrom", null, null)},
											locale));
						if(Strings.isNullOrEmpty(payload.getFocusId())){
							payload.setFocusId(TC_TO);
						}
					}if(!Strings.isNullOrEmpty(this.tcTo) && !(FormatUtil.compareDate(tcToChk,currentDate) != -1)){ // to >= current
						errors.add(messagesource.getMessage(MessagesConstants.A_ERROR_MUST_BE_GREATER_THAN_OR_EQUAL,
								new String[]{messagesource.getMessage("BW0.WBW04130.Label.TCTo", null, null), "Current Date"},
											locale));
						if(Strings.isNullOrEmpty(payload.getFocusId())){
							payload.setFocusId(TC_TO);
						}
					}
				}*/
			}
		}catch(Exception e){
			throw e;
		}
		return errors;
	}
}
