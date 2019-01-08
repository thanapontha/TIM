/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.web.master.service
 * Program ID 	            :  CST33060Service.java
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
package th.co.toyota.bw0.web.master.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.toyota.application.model.Payload;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.common.repository.CommonRepository;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.web.master.form.SystemMasterForm;
import th.co.toyota.bw0.web.master.repository.SystemMasterRepository;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.model.SystemInfo;

@Service
public class SystemMasterService {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String sep = "\\" + AppConstants.CHECKBOX_SEPERATER;
	
	@Autowired
	private SystemMasterRepository repository;
	
	@Autowired
	private CommonRepository commonRepository;
	
	public List<String> submitAddObject(SystemMasterForm form, CSC22110UserInfo userInfo) throws Exception {
		try(Connection conn = commonRepository.getConnection()){
			List<String> errorList = new ArrayList<>();
			if (errorList.isEmpty()) {
				repository.addObject(conn, form, userInfo);
			}
			return errorList;
		}
	}

	public Object[] submitEditObject(SystemMasterForm form, CSC22110UserInfo userInfo) throws Exception {
		try(Connection conn = commonRepository.getConnection()){
			List<String> errorList = new ArrayList<>();
			List<String> warnList = new ArrayList<>();

			if (errorList.isEmpty()) {
				repository.editObject(conn, form, userInfo);
			}
			return new Object[]{errorList, warnList};
		}
	}
	
	public Object[] deleteObject(SystemMasterForm activeform, String[] objects) throws Exception {
		try(Connection conn = commonRepository.getConnection()){
			
			List<String> errorList = new ArrayList<>();
			List<String> warnList = new ArrayList<>();
			
			List<Object[]> dataList = new ArrayList<>();
			for (String objD : objects) {
				String[] updateKeySet = objD.split(sep);
				Object[] arrayObj = new Object[4];
				if ((updateKeySet != null) && (updateKeySet.length >= 4)) {
					String category = updateKeySet[0];
					String subCategory = updateKeySet[1];
					String code = updateKeySet[2];
					String updatedate = updateKeySet[3];
					
					arrayObj[0] = category;
					arrayObj[1] = subCategory;
					arrayObj[2] = code;
					arrayObj[3] = updatedate;
					dataList.add(arrayObj);
				}
			}
			repository.deleteObject(conn, dataList, activeform);
			return new Object[]{errorList, warnList};
		}
	}
	
	public void loadCombobox(SystemMasterForm form) throws Exception {
		try(Connection conn = commonRepository.getConnection()){
			repository.loadComboboxs(conn, form);
		}
	}

	public List<ComboValue> loadSubCategory(String categorySearch) throws Exception {
		try(Connection conn = commonRepository.getConnection()){
			return repository.loadSubCategory(conn, categorySearch);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean searchAllData(SystemMasterForm form, Payload payload) throws Exception {
		boolean foundData = false;
		try(Connection conn = commonRepository.getConnection()){
			Object[] query = repository.generateSearchQuery(form);
			StringBuilder sql = (StringBuilder) query[0];
			List<Object> parameter = (List<Object>) query[1];
			int totalRows = commonRepository.getTotalActiveRecordSize(conn, sql, parameter);
			int firstResult = form.getFirstResult();
			int rowsPerPage = form.getRowsPerPage();
			int first = firstResult - (firstResult % rowsPerPage);
			if (first >= totalRows) {
				first = totalRows - rowsPerPage;
				if (first < 0) {
					first = 0;
				}
			}
			firstResult = first;
			if (totalRows > 0) {				
				List<SystemInfo> ls = repository.searchObjectList(conn, sql, parameter, firstResult, rowsPerPage);
				payload.setObjectForm(form);
				if ((ls != null) && (!ls.isEmpty())) {
					payload.setObjectsInfoList(ls);
					foundData = true;
				} else {
					payload.setObjectsInfoList(null);
				}
			}
			payload.setFirstResult(firstResult);
			payload.setRowsPerPage(rowsPerPage);
			payload.setTotalRows(totalRows);
		}
		return foundData;
	}

}
