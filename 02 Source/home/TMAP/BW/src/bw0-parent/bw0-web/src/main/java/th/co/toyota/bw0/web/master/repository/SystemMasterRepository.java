package th.co.toyota.bw0.web.master.repository;

import java.sql.Connection;
import java.util.List;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.common.form.CommonBaseForm;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.bw0.web.master.form.SystemMasterForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.model.SystemInfo;

public interface SystemMasterRepository {
	public boolean addObject(Connection conn, CommonBaseForm activeform, CSC22110UserInfo userInfo) throws CommonErrorException;
	public boolean editObject(Connection conn, CommonBaseForm activeform, CSC22110UserInfo userInfo) throws CommonErrorException;
	void deleteObject(Connection conn, List<Object[]> objectsDelete, CommonBaseForm activeform) throws CommonErrorException;
	public void loadComboboxs(Connection conn, CommonBaseForm activeform) throws CommonErrorException;
	public List<ComboValue> loadSubCategory(Connection conn, String category) throws CommonErrorException;
	Object[] generateSearchQuery(SystemMasterForm form) throws CommonErrorException;
	List<SystemInfo> searchObjectList(Connection conn, StringBuilder sql, List<Object> parameter, int firstResult, int rowsPerPage) throws CommonErrorException;
}
