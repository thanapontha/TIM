package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import th.co.toyota.bw0.api.common.EmailNotification;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.model.common.Authorization;
import th.co.toyota.bw0.api.model.common.GetsudoMonthConfigInfo;
import th.co.toyota.bw0.util.ComboValue;
import th.co.toyota.st3.api.model.ModuleDetailInfo;

public interface IBW00000Repository {
//	public boolean isSeqMasterExist(String seqCode);
//	public void createSeqMaster(String seqCode, String seqKey, int startValue, String docNoFormat, int startMonth, String userId);
	public ModuleDetailInfo findModuleDetailInfo(String functionId) throws CommonErrorException;
	public String genAppId() throws CommonErrorException;
	public List<ComboValue> loadCombobox(Connection conn, String tableName, String[] selectField, String criteria, String orderBy) throws CommonErrorException;
	public List<ComboValue> loadComboboxWithAuth(Connection conn, String tableName, String[] selectField, String criteria, String orderBy, StringBuilder existConditionAuth)throws CommonErrorException;
	public List<Authorization> getUserAuth(Connection conn, String userCompany, String userId) throws CommonErrorException;
	public EmailNotification getEmailNotification(Connection conn, String vehiclePlant, String vehicleModel, String sendToCompany) throws CommonErrorException;
	public List<ComboValue> getTimingComboList (Connection conn) throws CommonErrorException;
	public GetsudoMonthConfigInfo getGetsudoConfigInfo(Connection conn, String getsudoMonth) throws CommonErrorException;
	public String getStatusOfLogUpload(String appId, Connection conn) throws CommonErrorException;
	public String getUpdateDateFromUploadtatusLog(String appId, Connection conn) throws CommonErrorException;
	public int insertLogUploadStatus(String uploadType, String userId, List<Object[]> dataList, String conditionDeleteUploadSts) throws CommonErrorException;
	public String getEffectiveCriteria(Connection conn, String getsudoMonth, String aliasTable) throws CommonErrorException;

	public Object[] getPrevGetsudoMonthAndTiming(Connection conn, String getsudoMonthCurrent, String timingCurrent) throws CommonErrorException;
	public Object[] getNextGetsudoMonthAndTiming(Connection conn, String getsudoMonthCurrent, String timingCurrent) throws CommonErrorException;
	public Object[] getGetsudoMonthDisplay(Connection conn, String getsudoMonth) throws CommonErrorException;
	public String getGetsudoMonthDisplayStr(Connection conn, String getsudoMonth) throws CommonErrorException;
	
	public List<String> getVehiclePlantMaster(Connection conn, String getsudoMonth) throws CommonErrorException;
	public List<String> getUnitPlantMaster(Connection conn, String getsudoMonth)throws CommonErrorException;
	public List<String> getVehicleUnitRelationMaster(Connection conn, String getsudoMonth) throws CommonErrorException;
	public int insertLogDetail(Connection conn, List<Object[]> dataList, String type) throws CommonErrorException;
	public void deleteLogDetail(Connection conn, String[] params) throws CommonErrorException;
	public HashMap<String,Object> getTableMeataData(String tableName) throws Exception;
	public int getRowPerPage(String screenId);
	public int insertRundownKompoSts(Connection conn, String[] paramDel, Object[] data)
			throws Exception;
	public HashMap<String, String> getUserInfoForTestOnFTH(String ipAddress);
	public int insertRundownKompoSts(Connection conn, String[] paramDel, List<Object[]> datas)throws Exception;
	public Connection getConnection();
	
	int updateStatusOfLogUpload(String appId, String userId, String status, String message) throws CommonErrorException;
	int updateStatusOfLogUpload(Connection conn, String appId, String userId, String status, String message, Timestamp transactionUpdateDt) throws CommonErrorException;
	
	public Object executeQuery(Connection conn, String sql, int totalSelectCol) throws Exception;

	public String genSQLOverlap(String tcFrom, String tcTo, String tableForCheck, String whereCriteria);
	public String genSQLExistVehiclePlantUnitRelation(String tcTo, String whereCriteria);		
	public int getTotalActiveRecordSize(Connection conn, StringBuilder sql, List<Object> parameter) throws Exception;
	public boolean worksheetAlreadyCompleted(Connection conn, String getsudoMonth, String timing, String vehiclePlant, String vehicleModel) throws Exception;

}
