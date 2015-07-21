package models;

public class ServiceConfigurationItem {
	
	private long id;
	private long serviceConfigurationId;
	private Parameter parameter;
	private String value;

	public ServiceConfigurationItem() {
	}
	
	public ServiceConfigurationItem(long serviceConfigurationId, Parameter parameter
			, String value) {
		super();
		this.serviceConfigurationId = serviceConfigurationId;
		this.parameter = parameter;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public long getServiceConfigurationId() {
		return serviceConfigurationId;
	}
	public void setServiceConfigurationId(long serviceConfigurationId) {
		this.serviceConfigurationId = serviceConfigurationId;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}	

	@Override
	public String toString() {
		return "ServiceConfigurationItem [id=" + id + ", serviceConfigurationId="
				+ serviceConfigurationId + ", parameter=" + parameter
				+ ", value=" + value + "]";
	}
}
