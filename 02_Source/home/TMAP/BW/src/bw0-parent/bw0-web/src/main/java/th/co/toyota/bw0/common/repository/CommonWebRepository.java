package th.co.toyota.bw0.common.repository;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.util.ComboValue;

public interface CommonWebRepository {
	String genAppId() throws CommonErrorException;
	Connection getConnection();
	Map<String, String> getUserInfoForTestOnDev(String ipAddress);
	Object executeQuery(Connection conn, String sql, int totalSelectCol) throws CommonErrorException;
	int getRowPerPage(String screenId);
	int getTotalActiveRecordSize(Connection conn, StringBuilder sql, List<Object> parameter) throws CommonErrorException;
	List<ComboValue> loadCombobox(Connection conn, String tableName, String[] selectField, String criteria, String orderBy) throws CommonErrorException;
	List<ComboValue> loadComboboxWithAuth(Connection conn, String tableName, String[] selectField, String criteria, String orderBy, StringBuilder existConditionAuth) throws CommonErrorException ;
}
