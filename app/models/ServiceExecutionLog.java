package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import utils.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	private static final String GET_ALL_SERVICE_LOG = Constants.URL_HOST
			+ Constants.CMU_BACKEND_PORT + Constants.GET_ALL_SERVICE_LOG;

//	private static final String GET_A_SERVICE_LOG = Constants.URL_HOST
//			+ Constants.CMU_BACKEND_PORT + Constants.NEW_GET_A_SERVICE_LOG;
//
//	private static final String EXECUTION_LOG_QUERY = Constants.URL_HOST
//			+ Constants.CMU_BACKEND_PORT + Constants.SERVICE_EXECUTION_LOG
//			+ Constants.SERVICE_EXECUTION_LOG_QUERY;

	/**
	 * Generate the list of all service log
	 * 
	 * @return a list of all the service log
	 */
	public static List<ServiceExecutionLog> all() {

		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();

		JsonNode serviceLogNode = RESTfulCalls.getAPI(GET_ALL_SERVICE_LOG);

		// if no value is returned or error or is not json array
		if (serviceLogNode == null || serviceLogNode.has("error")
				|| !serviceLogNode.isArray()) {
			return serviceLog;
		}

		// parse the json string into object
		for (int i = 0; i < serviceLogNode.size(); i++) {
			JsonNode json = serviceLogNode.path(i);

			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
			serviceLog.add(newServiceLog);
		}
		return serviceLog;
	}

	private static ServiceExecutionLog deserializeJsonToServiceLog(JsonNode json) {
		ServiceExecutionLog newServiceLog = new ServiceExecutionLog();
		newServiceLog.setId(json.get("id").asLong());
		newServiceLog.setServiceId(json.get("climateService").get("id")
				.asLong());
		newServiceLog.setPurpose(json.get("purpose").asText());
		newServiceLog.setUserName(json.get("user").get("firstName").asText()
				+ " " + json.get("user").get("lastName").asText());
		newServiceLog.setServiceConfigurationId(json
				.get("serviceConfiguration").get("id").asText());
		newServiceLog.setExecutionStartTime(json.findPath("executionStartTime")
				.asText());
		newServiceLog.setExecutionEndTime(json.findPath("executionEndTime")
				.asText());
		//newServiceLog.setDatasetLogId(json.findPath("datasetLogId").asText());

		return newServiceLog;
	}

}
