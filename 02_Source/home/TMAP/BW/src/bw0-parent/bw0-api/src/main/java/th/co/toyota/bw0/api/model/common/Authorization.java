package th.co.toyota.bw0.api.model.common;

public class Authorization {
	public String vehiclePlant;
	public String vehicleModel;
	public String userID;
	public String email;

    public Authorization(String vehiclePlant, String vehicleModel, String userID, String email) {
    	this.vehiclePlant = vehiclePlant;
    	this.vehicleModel = vehicleModel;
    	this.userID = userID;
    	this.email = email;
    }

	public String getVehiclePlant() {
		return vehiclePlant;
	}

	public void setVehiclePlant(String vehiclePlant) {
		this.vehiclePlant = vehiclePlant;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}