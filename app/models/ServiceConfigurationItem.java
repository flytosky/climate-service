package models;

public class ServiceConfigurationItem {
	
	private long id;
	private long serviceConfigurationId;
	private Parameter parameter;
	private String value;
	private String parameterName;
	private String parameterRule;

	public String getParameterRule() {
		return parameterRule;
	}

	public void setParameterRule(String parameterRule) {
		this.parameterRule = parameterRule;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setId(long id) {
		this.id = id;
	}

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
