package models;

public class ServiceConfiguration {
	
	private long id;
	private ClimateService climateService;
	private long userId;
	private String userName;
	private String runTime;

	public ServiceConfiguration() {
	}
	
	public ServiceConfiguration(ClimateService climateService,
			long userId, String runtime){
		super();
		this.climateService = climateService;
		this.userId = userId;
		this.runTime = runtime;
	}

	public long getId() {
		return id;
	}

	public ClimateService getClimateService() {
		return climateService;
	}

	public long getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setClimateServiceId(ClimateService climateService) {
		this.climateService = climateService;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}


	@Override
	public String toString() {
		return "ServiceConfiguration [id=" + id + ", climateservice="
				+ climateService + ", userId=" + userId + ", runTime=" + runTime
				+ "]";
	}


    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of ServiceConfiguration or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof ServiceConfiguration)) {
            return false;
        }

        // typecast o to ServiceConfiguration so that we can compare data members
        ServiceConfiguration c = (ServiceConfiguration) o;

        if (this.getId() == c.getId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode(){
        return (int)id;
    }
}