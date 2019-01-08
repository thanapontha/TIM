package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.util.Map;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public interface CommonAPIRepository {
	String genAppId() throws CommonErrorException;
	Connection getConnection();
	Map<String,Object> getTableMeataData(String tableName) throws Exception;
}
