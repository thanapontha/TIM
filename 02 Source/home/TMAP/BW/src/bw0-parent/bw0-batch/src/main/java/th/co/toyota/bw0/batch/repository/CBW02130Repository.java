package th.co.toyota.bw0.batch.repository;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.common.CommonSQLAdapter;
import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;

@Repository
public class CBW02130Repository implements IBW02130Repository {
	final Logger logger = LoggerFactory.getLogger(CBW02130Repository.class);
	
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	@Override
	public int insertDataToStaging(Connection conn, List<Object[]> dataList, String userId) throws CommonErrorException{
		int inserted = 0;
		boolean completed = false;
		boolean closeConnection = true;
		try{
			StringBuilder insSQL = new StringBuilder();
			insSQL.append("INSERT INTO TB_S_KOMPO  ");
			insSQL.append(" ( GETSUDO_MONTH,  ");
			insSQL.append("   TIMING,  ");
			insSQL.append("   VEHICLE_PLANT,  ");
			insSQL.append("   VEHICLE_MODEL,  ");
			insSQL.append("   UNIT_PLANT,  ");
			insSQL.append("   UNIT_MODEL,  ");
			insSQL.append("   FILE_ID,  ");
			insSQL.append("   FILE_NAME,  ");
			insSQL.append("   IMPORTER,  ");
			insSQL.append("   RUNDOWN_KEY,  ");
			insSQL.append("   EXPORTER,  ");
			insSQL.append("   ORDER_DT,  ");
			insSQL.append("   VANNING_VOLUME,  ");
			insSQL.append("   VANNING_DT,  ");
			insSQL.append("   LOADING_DT,  ");
			insSQL.append("   UNLOADING_DT,  ");
			insSQL.append("   PROD_DT,  ");
			insSQL.append("   PROD_VOLUME,  ");
			insSQL.append("   UPLOAD_FILE_NAME,  ");
			insSQL.append("   CREATE_BY,  ");
			insSQL.append("   RUNNING_NO,  ");
			insSQL.append("   APL_ID  ");
			insSQL.append(" )  ");
			insSQL.append(" VALUES  ");
			insSQL.append(" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ");
			insSQL.append("   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ");
			insSQL.append("   ?, ? ");
			insSQL.append(" )  ");
			
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			conn.setAutoCommit(false);
			
			//insert data to staging
			CommonSQLAdapter adapter = new CommonSQLAdapter();
			if(dataList!=null && !dataList.isEmpty()){
				inserted = adapter.execute(conn, insSQL.toString() , dataList.toArray());
			}
			completed = true;
			return inserted;
		}catch (Exception e) {
			throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			CommonUtility.closeConnection(conn, null, null, closeConnection, completed);
		}
	}

}
