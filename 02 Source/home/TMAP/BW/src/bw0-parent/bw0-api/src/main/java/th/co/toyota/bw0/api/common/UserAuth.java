package th.co.toyota.bw0.api.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.model.common.Authorization;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;

@Component
public class UserAuth {
	@Autowired
	private IBW00000Repository commonRepository;
	
	@Transient
	private StringBuilder conditionGetPlantAndModel;
	
	public List<Authorization> allowVehiclePlantAndModel(Connection conn, String userCompany, String userId, String joinWithField){
		return allowVehiclePlantAndModels(conn, userCompany, userId, joinWithField, "M");
	}
	
	public List<Authorization> allowVehiclePlantAndModel(Connection conn, String userCompany, String userId, String[] joinWithField, String aliasTable){
		return allowVehiclePlantAndModels(conn, userCompany, userId, joinWithField, aliasTable);
	}
	
	private List<Authorization> allowVehiclePlantAndModels(Connection conn, String userCompany, String userId, Object joinWithField, String aliasTable){
		List<Authorization> ls = new ArrayList<Authorization>();
		try {
			StringBuilder cdt = new StringBuilder();
			List<Authorization> auList = commonRepository.getUserAuth(conn, userCompany, userId);
			if(auList!=null && !auList.isEmpty()){
				HashMap<String,Authorization> map = new HashMap<String,Authorization>();
				for(Authorization au : auList){
					String key = au.getVehiclePlant()+":"+au.getVehicleModel();
					if(map.containsKey(key) == false){
						if(cdt.length()>0){
							cdt.append(" OR ");
						}
						if(AppConstants.ALL.equalsIgnoreCase(au.getVehiclePlant()) && AppConstants.ALL.equalsIgnoreCase(au.getVehicleModel())){
							cdt.append(" (U.VEHICLE_PLANT = U.VEHICLE_PLANT AND U.VEHICLE_MODEL = U.VEHICLE_MODEL) ");
						}else if(AppConstants.ALL.equalsIgnoreCase(au.getVehiclePlant()) && !AppConstants.ALL.equalsIgnoreCase(au.getVehicleModel())){
							cdt.append(" (U.VEHICLE_PLANT = U.VEHICLE_PLANT AND U.VEHICLE_MODEL = '"+au.getVehicleModel()+"') ");
						}else if(!AppConstants.ALL.equalsIgnoreCase(au.getVehiclePlant()) && AppConstants.ALL.equalsIgnoreCase(au.getVehicleModel())){
							cdt.append(" (U.VEHICLE_PLANT = '"+au.getVehiclePlant()+"' AND U.VEHICLE_MODEL = U.VEHICLE_MODEL) ");
						}else {
							cdt.append(" (U.VEHICLE_PLANT = '"+au.getVehiclePlant()+"' AND U.VEHICLE_MODEL = '"+au.getVehicleModel()+"') ");
						}
						
						map.put(key, au);
						ls.add(au);
					}
				}
			}
			
			StringBuilder cdtAll = new StringBuilder();
			cdtAll.append(" AND EXISTS ");
			cdtAll.append("      (SELECT 'x' FROM (SELECT DISTINCT U.VEHICLE_PLANT, ");
			cdtAll.append("                                     U.VEHICLE_MODEL, ");
			cdtAll.append("                                     U.UNIT_PLANT, ");
			cdtAll.append("                                     U.UNIT_MODEL ");
			cdtAll.append("                        FROM TB_M_VEHICLE_UNIT_RELATION U ");
			cdtAll.append("                      WHERE  ");
			if(cdt.length()>0){ 
				cdtAll.append( cdt.toString());
			}else{ 
				cdtAll.append(" 1 = 0 ");
			}		                        
			cdtAll.append("                   ) T  ");
			if(joinWithField instanceof String[] ){
				String[] conditions = (String[])joinWithField;
				for(int i=0;i<conditions.length; i++){
					String fieldName = conditions[i];
					if(i==0){
						cdtAll.append("  WHERE T."+fieldName+" = "+aliasTable+"."+fieldName+"  ");
					}else{
						cdtAll.append("  AND T."+fieldName+" = "+aliasTable+"."+fieldName+"  ");
					}
				}
				
			}else{
				cdtAll.append("        WHERE T."+joinWithField+" = "+aliasTable+"."+joinWithField+"  ");
			}
			cdtAll.append("       )  ");
			
			this.setConditionGetPlantAndModel(cdtAll);
		} catch (CommonErrorException e) {
			e.printStackTrace();
		}
		return ls;
	}

	public StringBuilder getConditionGetPlantAndModel() {
		return conditionGetPlantAndModel;
	}

	public void setConditionGetPlantAndModel(StringBuilder conditionGetPlantAndModel) {
		this.conditionGetPlantAndModel = conditionGetPlantAndModel;
	}
}
