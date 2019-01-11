/******************************************************
 * Program History
 * 
 * Project Name	            :  
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.api.service.common
 * Program ID 	            :  SystemMasterAPIService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  January 11, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.SystemMasterAPIRepository;

@Service
public class SystemMasterAPIService {

	@Autowired
	private SystemMasterAPIRepository systemMasterRepository;
	
	public List<SystemInfo> getSystemMaster(SystemInfoId id) {
		return systemMasterRepository.querySystemMasterInfo(id);
	}

	public List<SystemInfo> getSystemMaster(String category,
			String subCategory, String cd) {
		SystemInfoId id = new SystemInfoId();
		
		id.setCategory(category);
		id.setSubCategory(subCategory);
		id.setCode(cd);
		
		return systemMasterRepository.querySystemMasterInfo(id);
	}

	public List<SystemInfo> getSystemMaster(String category, String subCategory) {
		SystemInfoId id = new SystemInfoId();
		
		id.setCategory(category);
		id.setSubCategory(subCategory);
		
		return systemMasterRepository.querySystemMasterInfo(id);
	}

	public String getSystemMasterValue(String category, String subCategory,String cd) throws CommonErrorException {
		String value = "";
		SystemInfoId id = new SystemInfoId();
		
		id.setCategory(category);
		id.setSubCategory(subCategory);
		id.setCode(cd);
		
		SystemInfo systemInfo = systemMasterRepository.findSystemMasterInfo(id);
		if(systemInfo != null){
			value = systemInfo.getValue();
		}else{
			throw new CommonErrorException(MessagesConstants.A_ERROR_NOT_REGISTER, 
    				new String[]{"CATEGORY="+category+", SUB_CATEGORY="+subCategory+" and CD="+Strings.nullToEmpty(cd),"TB_M_SYSTEM"}, 
    				AppConstants.ERROR);
		}
			
		return value;
	}

}
