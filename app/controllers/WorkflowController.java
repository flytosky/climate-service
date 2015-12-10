package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.*;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WorkflowController extends Controller {
	final static Form<ServiceExecutionLog> serviceLogForm = Form
			.form(ServiceExecutionLog.class);
	
	public static Result getServiceLogsByUser() {
		Form<ServiceExecutionLog> se = serviceLogForm.bindFromRequest();
		List<ServiceExecutionLog> serviceExecutionLogs = new ArrayList<ServiceExecutionLog>();
		ObjectNode jsonData = Json.newObject();
		String userName = "";
		String serviceName = "";
		String startTime = "";
		String endTime = "";
		
		Date executionStartTime = null, executionEndTime= null;
		
		try {
			userName = se.field("User Name").value();
			serviceName = se.field("Service Name").value();
			startTime = se.field("Execution Start Time").value();
			endTime = se.field("Execution End Time").value();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

			if (!startTime.isEmpty()) {
				try {
					executionStartTime = simpleDateFormat.parse(startTime);
				} catch (ParseException e) {
					System.out.println("Wrong Date Format :" + startTime);
					return badRequest("Wrong Date Format :" + startTime);
				}
			}
			if (!endTime.isEmpty()) {
				try {
					executionEndTime = simpleDateFormat.parse(endTime);
				} catch (ParseException e) {
					System.out.println("Wrong Date Format :" + endTime);
					return badRequest("Wrong Date Format :" + endTime);
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}

		jsonData.put("userName", userName);
		jsonData.put("serviceName", serviceName);
		jsonData.put("startTime", executionStartTime.getTime());
		jsonData.put("endTime", executionEndTime.getTime());
		
		JsonNode serviceExecutionLogNode = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.SEARCH_EXECUTION_LOGS_BY_USER, jsonData);
		if (serviceExecutionLogNode == null || serviceExecutionLogNode.has("error")
				|| !serviceExecutionLogNode.isArray()) {
			return ok(searchServiceLogResult.render(serviceExecutionLogs));
		}
		
		for (int i = 0; i < serviceExecutionLogNode.size(); i++) {
			JsonNode json = serviceExecutionLogNode.path(i);
			ServiceExecutionLog newServiceLog = deserializeJsonToServiceLog(json);
			serviceExecutionLogs.add(newServiceLog);
		}
		
		return ok(searchServiceLogResult.render(serviceExecutionLogs));
	}
	
	private static ServiceExecutionLog deserializeJsonToServiceLog(JsonNode json) {
		ServiceExecutionLog newServiceLog = new ServiceExecutionLog();
		newServiceLog.setId(json.get("id").asLong());
		newServiceLog.setServiceId(json.get("climateService").get("id").asLong());
		String serviceName = json.get("climateService").get("name").asText();
		newServiceLog.setServiceName(serviceName);
		newServiceLog.setPurpose(json.get("purpose").asText());
		newServiceLog.setUserName(json.get("user").get("firstName").asText()
				+ " " + json.get("user").get("lastName").asText());
		newServiceLog.setServiceConfigurationId(json.get("serviceConfiguration").get("id").asText());
		
		String executionStartTime = json.findPath("executionStartTime").asText();
		String executionEndTime = json.findPath("executionEndTime").asText();
		
		String datasetStudyStartTime = json.findPath("datasetStudyStartTime").asText();
		String datasetStudyEndTime = json.findPath("datasetStudyEndTime").asText();
		
		Date tmpTime = null;
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(executionStartTime);		
			if (tmpTime != null) {
				newServiceLog.setExecutionStartTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(tmpTime));
			}
	    } catch (ParseException e) {	    
	    }
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(executionEndTime);			
			if (tmpTime != null) {
				newServiceLog.setExecutionEndTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(tmpTime));
			}
	    } catch (ParseException e) {	    
	    }
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(datasetStudyStartTime);
			
			if (tmpTime != null) {
				newServiceLog.setDataSetStartTime(new SimpleDateFormat("YYYY-MM").format(tmpTime));
			}
	    } catch (ParseException e){	    
//	    	e.printStackTrace();
	    }
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(datasetStudyEndTime);
			
			if (tmpTime != null) {
				newServiceLog.setDataSetEndTime(new SimpleDateFormat("YYYY-MM").format(tmpTime));
			}
	    } catch (ParseException e){	    
//	    	e.printStackTrace();
	    }
		
		newServiceLog.setDatasetLogId(json.findPath("datasetLogId").asText());
		if(json.get("url") != null) {
			String pageUrl = Constants.URL_SERVER
					+ Constants.LOCAL_HOST_PORT + "/assets/html/service"
					+ serviceName.substring(0, 1).toUpperCase()
					+ serviceName.substring(1) + ".html" + json.get("url").asText();
			newServiceLog.setUrl(pageUrl);
		}
		
		return newServiceLog;
	}
}
