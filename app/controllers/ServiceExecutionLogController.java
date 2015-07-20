package controllers;

import java.util.ArrayList;
import java.util.List;

import models.ServiceExecutionLog;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.RESTfulCalls;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceExecutionLogController extends Controller {
	
	private static final String GET_ALL_SERVICE_LOG = Constants.URL_HOST
			+ Constants.CMU_BACKEND_PORT + Constants.GET_ALL_SERVICE_LOG;
	
	final static Form<ServiceExecutionLog> serviceLogForm = Form
			.form(ServiceExecutionLog.class);
	
	public static Result getServiceLog() {
		
		List<ServiceExecutionLog> serviceLogList = new ArrayList<ServiceExecutionLog>();		
		JsonNode serviceLogNode = RESTfulCalls.getAPI(GET_ALL_SERVICE_LOG);
		
		// if no value is returned or error or is not json array
		if (serviceLogNode == null || serviceLogNode.has("error")
				|| !serviceLogNode.isArray()) {
			return ok(serviceLog.render(serviceLogList, serviceLogForm));
		}

		// parse the json string into object
		for (int i = 0; i < serviceLogNode.size(); i++) {
			JsonNode json = serviceLogNode.path(i);
			ServiceExecutionLog newServiceLog = new ServiceExecutionLog();
			newServiceLog.setId(json.get("id").asLong());
			newServiceLog.setServiceId(json.get("climateService").get("id").asLong());
			newServiceLog.setPurpose(json.get("purpose").asText());
			newServiceLog.setUserName(json.get("user").get("firstName").asText()
					+ " " + json.get("user").get("lastName").asText());
			newServiceLog.setServiceConfigurationId(json
					.get("serviceConfiguration").get("id").asText());
			newServiceLog.setExecutionStartTime(json.findPath(
					"executionStartTime").asText());
			newServiceLog.setExecutionEndTime(json.findPath("executionEndTime").asText());
			serviceLogList.add(newServiceLog);
		}		
		return ok(serviceLog.render(serviceLogList, serviceLogForm));
	}
	
	public static Result searchServiceLog() {
		return ok(searchServiceLog.render(serviceLogForm));
	}

}
