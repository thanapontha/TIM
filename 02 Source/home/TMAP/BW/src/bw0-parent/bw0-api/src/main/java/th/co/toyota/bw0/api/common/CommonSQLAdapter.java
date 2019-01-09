package th.co.toyota.bw0.api.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import th.co.toyota.bw0.util.FormatUtil;

public class CommonSQLAdapter {
	public int execute(Connection con, String stQuery, Object oParams) throws Exception {
	    try(PreparedStatement ps = con.prepareStatement(stQuery); ) {
	      if ((oParams instanceof Object[]) && (((Object[])oParams)[0] instanceof Object[])) {
	        Object[] obTmp = (Object[])oParams;
	        for (int i = 0; i < obTmp.length; i++) {
	          assignParams(ps, obTmp[i]);
	          ps.addBatch();
	        }
	        
	        int[] intResultBatch = ps.executeBatch();
	        return intResultBatch==null?0:intResultBatch.length;
	      }
	      
	      assignParams(ps, oParams);
	      return ps.executeUpdate();
	    }
	  }

	private void assignParams(PreparedStatement ps, Object oParams) throws SQLException
	  {
	    Object[] arrParam = null;
	    Object arrParamValue = null;
	    if (oParams == null) {
	      return;
	    }
	    if ((oParams instanceof Object[])) {
	        arrParam = (Object[])oParams;
	      for (int i = 0; i < arrParam.length; i++) {
	          arrParamValue = arrParam[i];
	        if ((arrParamValue instanceof java.sql.Date)) {
		  	    ps.setDate(i + 1, (java.sql.Date)arrParamValue);
	  	    }else if ((arrParamValue instanceof java.sql.Timestamp)) {
		  	    ps.setTimestamp(i + 1, (java.sql.Timestamp)arrParamValue);
	  	    }else if ((arrParamValue instanceof java.util.Date)) {
	  	    	ps.setDate(i + 1, FormatUtil.convert((java.util.Date)arrParamValue));
	        }else if (arrParamValue != null) {
	          if ((arrParamValue instanceof Long)) {
	            ps.setLong(i + 1, ((Long)arrParamValue).longValue());
	          } else if ((arrParamValue instanceof Double)) {
	            ps.setDouble(i + 1, ((Double)arrParamValue).doubleValue());
	          } else if ((arrParamValue instanceof String)) {
	            ps.setString(i + 1, (String)arrParamValue);
	          } else if ((arrParamValue instanceof Integer)) {
	            ps.setInt(i + 1, ((Integer)arrParamValue).intValue());
	          } else if ((arrParamValue instanceof BigDecimal)) {
	            ps.setBigDecimal(i + 1, (BigDecimal)arrParamValue);
	          }
	        }else {
	          ps.setObject(i + 1, arrParamValue);
	        }
	      }
	    }else {
	      ps.setObject(1, oParams);
	    }
	  }
}
