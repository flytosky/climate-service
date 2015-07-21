package models;

public class Parameter {
	
	private long id;
	private long climateServiceId;
	private long indexInService;
	private String name;
	private String dataRange;
	private String rule;
	private String purpose;
	
	public Parameter() {
	}

	public Parameter(long climateServiceId, long indexInService,
			String name, String dataRange,
			String rule, String purpose) {
		super();
		this.climateServiceId = climateServiceId;
		this.indexInService = indexInService;
		this.name = name;
		this.dataRange = dataRange;
		this.rule = rule;
		this.purpose = purpose;
	}

	public long getId() {
		return id;
	}

	public long getClimateServiceId() {
		return climateServiceId;
	}

	public void setClimateServiceId(long climateServiceId) {
		this.climateServiceId = climateServiceId;
	}

	public long getIndexInService() {
		return indexInService;
	}

	public void setIndexInService(long indexInService) {
		this.indexInService = indexInService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataRange() {
		return dataRange;
	}

	public void setDataRange(String dataRange) {
		this.dataRange = dataRange;
	}
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Override
	public String toString() {
		return "Parameter [id=" + id + ", climateServiceId=" + climateServiceId
				+ ", indexInService=" + indexInService + ", name=" + name
				+ ", dataRange=" + dataRange
				+ ", rule=" + rule + ", purpose=" + purpose + "]";
	}
	
}