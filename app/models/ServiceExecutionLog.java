package models;

public class ServiceExecutionLog {

	private long id;
	private long serviceId;
	private String userName;
	private String serviceConfigurationId;
	private String datasetLogId;
	private String purpose;
	private String executionStartTime;
	private String executionEndTime;
	private String plotUrl;
	private String dataUrl;

	public long getId() {
		return id;
	}

	public long getServiceId() {
		return serviceId;
	}

	public String getUserName() {
		return userName;
	}

	public String getServiceConfigurationId() {
		return serviceConfigurationId;
	}

	public String getDatasetLogId() {
		return datasetLogId;
	}

	public String getPurpose() {
		return purpose;
	}

	public String getExecutionStartTime() {
		return executionStartTime;
	}

	public String getExecutionEndTime() {
		return executionEndTime;
	}

	public String getPlotUrl() {
		return plotUrl;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setServiceConfigurationId(String serviceConfigurationId) {
		this.serviceConfigurationId = serviceConfigurationId;
	}

	public void setDatasetLogId(String datasetLogId) {
		this.datasetLogId = datasetLogId;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setExecutionStartTime(String executionStartTime) {
		this.executionStartTime = executionStartTime;
	}

	public void setExecutionEndTime(String executionEndTime) {
		this.executionEndTime = executionEndTime;
	}

	public void setPlotUrl(String plotUrl) {
		this.plotUrl = plotUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	
	@Override
	public String toString() {
		return "ServiceExecutionLog [id=" + id + ", serviceId="
				+ serviceId + ", userName=" + userName + ", serviceConfigurationId="
				+ serviceConfigurationId + ", purpose=" + purpose
				+ ", executionStartTime=" + executionStartTime
				+ ", executionEndTime=" + executionEndTime + ", plotUrl="
				+ plotUrl + ", dataUrl=" + dataUrl + "]";
	}
}
