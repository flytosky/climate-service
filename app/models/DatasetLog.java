package models;

public class DatasetLog {
	
	private long id;
	private long serviceExecutionLogId;
	private String datasetName;
	private String datasetSource;
	private String datasetVariable;
	private String userName;
	private String plotUrl;
	private String dataUrl;
	private long originalDatasetId;
	private long outputDatasetId;
	
	private String datasetStudyStartTime;
	private String datasetStudyEndTime;
	
	public DatasetLog() {
		
	}
	
	public DatasetLog(long serviceExecutionLogId, String datasetName,
			String datasetSource, String datasetVariable, String userName,
			String plotUrl, String dataUrl, long originalDatasetId,
			long outputDatasetId, String datasetStudyStartTime,
			String datasetStudyEndTime) {
		super();
		this.serviceExecutionLogId = serviceExecutionLogId;
		this.datasetName = datasetName;
		this.userName = userName;
		this.plotUrl = plotUrl;
		this.dataUrl = dataUrl;
		this.datasetSource = datasetSource;
		this.datasetVariable = datasetVariable;
		this.originalDatasetId = originalDatasetId;
		this.outputDatasetId = outputDatasetId;
		this.datasetStudyStartTime = datasetStudyStartTime;
		this.datasetStudyEndTime = datasetStudyEndTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getServiceExecutionLogId() {
		return serviceExecutionLogId;
	}

	public void setServiceExecutionLogId(long serviceExecutionLogId) {
		this.serviceExecutionLogId = serviceExecutionLogId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getDatasetSource() {
		return datasetSource;
	}

	public void setDatasetSource(String datasetSource) {
		this.datasetSource = datasetSource;
	}

	public String getDatasetVariable() {
		return datasetVariable;
	}

	public void setDatasetVariable(String datasetVariable) {
		this.datasetVariable = datasetVariable;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPlotUrl() {
		return plotUrl;
	}

	public void setPlotUrl(String plotUrl) {
		this.plotUrl = plotUrl;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public long getOriginalDatasetId() {
		return originalDatasetId;
	}

	public void setOriginalDatasetId(long originalDatasetId) {
		this.originalDatasetId = originalDatasetId;
	}

	public long getOutputDatasetId() {
		return outputDatasetId;
	}

	public void setOutputDatasetId(long outputDatasetId) {
		this.outputDatasetId = outputDatasetId;
	}

	public String getDatasetStudyStartTime() {
		return datasetStudyStartTime;
	}

	public void setDatasetStudyStartTime(String datasetStudyStartTime) {
		this.datasetStudyStartTime = datasetStudyStartTime;
	}

	public String getDatasetStudyEndTime() {
		return datasetStudyEndTime;
	}

	public void setDatasetStudyEndTime(String datasetStudyEndTime) {
		this.datasetStudyEndTime = datasetStudyEndTime;
	}

	@Override
	public String toString() {
		return "DatasetLog [id=" + id + ", serviceExecutionLog="
				+ serviceExecutionLogId + ", datasetName=" + datasetName
				+ ", datasetSource=" + datasetSource + ", datasetVariable="
				+ datasetVariable + ", userName=" + userName + ", plotUrl="
				+ plotUrl + ", dataUrl=" + dataUrl + ", originalDatasetId="
				+ originalDatasetId + ", outputDatasetId=" + outputDatasetId
				+ ", datasetStudyStartTime=" + datasetStudyStartTime
				+ ", datasetStudyEndTime=" + datasetStudyEndTime + "]";
	}
	
}
