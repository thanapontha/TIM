package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.util.List;

import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;


public interface IBW03060Repository {
	List<SystemInfo> querySystemMasterInfo(SystemInfoId infoId);
	List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory);
	SystemInfo findSystemMasterInfo(SystemInfoId infoId);
	List<SystemInfo> searchSystemMaster(String category, String subCategory,
			String remark);
	public String findSystemMasterValue(Connection conn, String category, String subCategory, String code);
	List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory, boolean isSortValue, String sortMethod);
}
