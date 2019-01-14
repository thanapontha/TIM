package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import th.co.toyota.bw0.api.common.EmailNotification;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.st3.api.model.ModuleDetailInfo;

public interface CommonAPIRepository {
	Connection getConnection();
	ModuleDetailInfo findModuleDetailInfo(String functionId) throws CommonErrorException;
	String genAppId() throws CommonErrorException;
	List<ComboValue> loadCombobox(Connection conn, String tableName, String[] selectField, String criteria, String orderBy) throws CommonErrorException;
	EmailNotification getEmailNotification(Connection conn, String vehiclePlant, String vehicleModel, String sendToCompany) throws CommonErrorException;
	HashMap<String,Object> getTableMeataData(String tableName) throws Exception;
	int getRowPerPage(String screenId);
	HashMap<String, String> getUserInfoForTestOnFTH(String ipAddress);
	Object executeQuery(Connection conn, String sql, int totalSelectCol) throws Exception;
	int getTotalActiveRecordSize(Connection conn, StringBuilder sql, List<Object> parameter) throws Exception;
}
