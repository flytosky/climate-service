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
//
//	/**
//	 * Generate a new list of all service log which sync userId according to the
//	 * porpose
//	 * 
//	 * @return a list of all the service log
//	 */
//	public static List<ServiceExecutionLog> syncDataByPurpose() {
//		RESTfulCalls.getAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT
//				+ Constants.SERVICE_EXECUTION_LOG + "replaceUser");
//
//		return all();
//	}
//
//	public static List<ServiceExecutionLog> searchDataSource(String dataSource) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithParameter/model/"
//						+ dataSource + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		// Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchVariableName(
//			String variableName) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithParameter/var/"
//						+ variableName + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchExecutionPurpose(
//			String purpose) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls.getAPIParameter(
//				"http://localhost:9008/getExecutionLogByPurpose", "purpose",
//				purpose);
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchUserId(String userId) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithParameter/"
//						+ userId + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchTime(String start, String end) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithParameterRange/startT/"
//						+ start + "/endT/" + end + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchLatitude(String start,
//			String end) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithParameterRange/start%20lat%20(deg)/"
//						+ start + "/end%20lat%20(deg)/" + end + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> searchMultiDimension(
//			String dataSource, String variableName, String executionPurpose,
//			String userId, String startTime, String endTime) {
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		if (dataSource.equals("")) {
//			dataSource = "null";
//		}
//		if (variableName.equals("")) {
//			variableName = "null";
//		}
//		if (executionPurpose.equals("")) {
//			executionPurpose = "null";
//		}
//		if (userId.equals("")) {
//			userId = "null";
//		}
//		if (startTime.equals("")) {
//			startTime = "null";
//		}
//		if (endTime.equals("")) {
//			endTime = "null";
//		}
//		JsonNode serviceLogNode = RESTfulCalls
//				.getAPI("http://localhost:9008/searchServiceLogsWithMultipleParameter/userid/"
//						+ userId
//						+ "/datasource/"
//						+ dataSource
//						+ "/variablename/"
//						+ variableName
//						+ "/startyearmonth/"
//						+ startTime
//						+ "/endyearmonth/"
//						+ endTime
//						+ "/executionpurpose/" + executionPurpose + "/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		Logger.info(serviceLogNode.toString());
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	public static List<ServiceExecutionLog> search(String userId,
//			long startTime, long endTime) {
//
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//
//		startTime = startTime / 1000;
//		endTime = endTime / 1000;
//
//		JsonNode serviceLogNode = RESTfulCalls.getAPI(GET_A_SERVICE_LOG
//				+ userId + "/" + startTime + "" + "/" + endTime + "" + "/"
//				+ Constants.FORMAT);
//		// JsonNode serviceLogNode = APICall
//		// .callAPI("http://localhost:9008/getServiceExecutionLogs/123/1415904302/1415904357/json");
//		// Logger.debug(GET_A_SERVICE_LOG + userId+ "/" +startTime + "" + "/"
//		// +endTime + "" + "/" + util.Constants.FORMAT);
//		// if no value is returned or error or is not json array
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}
//
//	/**
//	 * Delete a service log
//	 * 
//	 * @return
//	 */
//	public static JsonNode deleteServiceLog(String confId)
//			throws UnsupportedEncodingException {
//		return RESTfulCalls.deleteAPI(Constants.URL_HOST
//				+ Constants.CMU_BACKEND_PORT
//				+ "serviceExecutionLog/deleteServiceExecutionLogs/" + confId);
//	}
//
//	/**
//	 * Generate a list of climate service names
//	 * 
//	 * @return a list of climate service names
//	 */
//	public static List<String> allServiceId() {
//		List<ServiceExecutionLog> allList = all();
//		List<String> resultList = new ArrayList<String>();
//		for (ServiceExecutionLog element : allList) {
//			String elementName = element.getServiceId();
//			if (elementName != null)
//				resultList.add(elementName);
//		}
//		return resultList;
//	}
//
//	/**
//	 * Search Service Execution Logs in the backend. All parameters are optional
//	 * 
//	 * @param userId
//	 * @param startTime
//	 * @param endTime
//	 * @param executionPurpose
//	 * @param parameters
//	 *            A key-value pair list of all parameters
//	 * @return
//	 */
//	public static List<ServiceExecutionLog> queryExecutionLogs(String userId,
//			Date startTime, Date endTime, String executionPurpose,
//			Map<String, String> parameters) {
//
//		List<ServiceExecutionLog> serviceLog = new ArrayList<ServiceExecutionLog>();
//		ObjectMapper mapper = new ObjectMapper();
//		ObjectNode queryJson = mapper.createObjectNode();
//		if (userId != null && !userId.isEmpty()) {
//			queryJson.put("userId", userId);
//		}
//		if (startTime != null) {
//			queryJson.put("executionStartTime", startTime.getTime());
//		}
//		if (endTime != null) {
//			queryJson.put("executionEndTime", endTime.getTime());
//		}
//		if (executionPurpose != null && !executionPurpose.isEmpty()) {
//			queryJson.put("purpose", executionPurpose);
//		}
//
//		if (parameters != null) {
//			ObjectNode paramsNode = mapper.createObjectNode();
//			for (String paramName : parameters.keySet()) {
//				String paramValue = parameters.get(paramName);
//				if (paramValue != null && !paramValue.isEmpty())
//					paramsNode.put(paramName, paramValue);
//			}
//			if (paramsNode.size() > 0) {
//				queryJson.set("parameters", paramsNode);
//			}
//		}
//
//		JsonNode serviceLogNode = RESTfulCalls.postAPI(EXECUTION_LOG_QUERY,
//				queryJson);
//		if (serviceLogNode == null || serviceLogNode.has("error")
//				|| !serviceLogNode.isArray()) {
//			return serviceLog;
//		}
//
//		// parse the json string into object
//		for (int i = 0; i < serviceLogNode.size(); i++) {
//			JsonNode json = serviceLogNode.path(i);
//			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
//			serviceLog.add(newServiceLog);
//		}
//		return serviceLog;
//	}

}
