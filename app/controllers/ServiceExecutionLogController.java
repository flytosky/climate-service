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

public class ServiceExecutionLogController extends Controller {
	
	final static Form<ServiceExecutionLog> serviceLogForm = Form
			.form(ServiceExecutionLog.class);
	
	

	public static Result getConfigurationByConfId() {
		String dynamicUrl = "TwoDimSlice3D";
		
		List<ServiceConfigurationItem> serviceConfigItemList = new ArrayList<ServiceConfigurationItem>();	
		
		
		try {
			DynamicForm df = DynamicForm.form().bindFromRequest();
			String logId = df.field("logId").value();

			if (logId == null || logId.isEmpty()) {
				Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
				return notFound("confId is null or empty");
			}

			// Call API
			JsonNode response = RESTfulCalls.getAPI(Constants.URL_SERVER + Constants.CMU_BACKEND_PORT + Constants.SERVICE_EXECUTION_LOG + Constants.SERVICE_EXECUTION_LOG_GET + "/" + logId);
			System.out.println("Print service response: " + response);
			int configurationId = response.path("serviceConfiguration").path("id").asInt();
			
			JsonNode responseConfigItems = RESTfulCalls.getAPI(Constants.URL_SERVER + Constants.CMU_BACKEND_PORT + Constants.CONFIG_ITEM + Constants.GET_CONFIG_ITEMS_BY_CONFIG + "/" + configurationId);
			
			String serviceName = response.path("climateService").path("name").asText();
			
			for (int i = 0; i < responseConfigItems.size(); i++) {
				JsonNode json = responseConfigItems.path(i);
				ServiceConfigurationItem serviceConfigItem = new ServiceConfigurationItem();
				
				serviceConfigItem.setParameterName(json.get("parameter").get("name").asText());
				serviceConfigItem.setValue(json.findPath("value").asText());
				System.out.println("Print Parameter Name: " + json.get("parameter").get("name").asText());
				System.out.println("Print Parameter Value: " + json.findPath("value").asText());
				serviceConfigItemList.add(serviceConfigItem);
			}	
			
			
			
			System.out.println("Print service Name: "+serviceName);
			//System.out.println("Print service configs: " + responseConfigItems);
			
		}catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}
		Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		
		String body = parseServicePageBody(dynamicUrl);
		
		return ok(serviceDetail.render(body, serviceConfigItemList));
	}
	
	public static String parseServicePageBody(String serviceName) {
    	
    	String location = "public/html/service" + serviceName + ".html";
    	File htmlFile = new File(location);
    	String entireHtml = null;
		try {
			entireHtml = new Scanner(htmlFile).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	String body = entireHtml.substring(entireHtml.indexOf("<body>"), entireHtml.indexOf("</body>")+7);

		return body;
    }
	
	public static String handleServiceName(String temp){
		StringBuffer buffer = new StringBuffer();  
		buffer.append(temp);
		int count = buffer.indexOf("-");
		while(count != 0){
			int number = buffer.indexOf("-", count);
			count = number + 1;
			if(number != -1){
				char a = buffer.charAt(count);
				char b = (char) (a - 32);
				buffer.replace(count, count+1, b+"");
			}
		}
		temp = buffer.toString().replaceAll("-", "");
		temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);
		return temp;
	}
	
	public static Result getServiceLog() {
		
		List<ServiceExecutionLog> serviceLogList = new ArrayList<ServiceExecutionLog>();		
		JsonNode serviceLogNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.GET_ALL_SERVICE_LOG);
		
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
	
	public static Result getSearchServiceLog() {
		Form<ServiceExecutionLog> dc = serviceLogForm.bindFromRequest();
		ObjectNode jsonData = Json.newObject();
		String dataSource = "";
		String variableName = "";
		String executionPurpose = "";
		String userId = "";
		String startTime = "";
		String endTime = "";
		Date start = null, end= null;
		
		try {
			dataSource = dc.field("Data Source").value().replace("/", "_");
			variableName = dc.field("Variable Name").value();
			executionPurpose = dc.field("Execution Purpose").value();
			userId = dc.field("User Id").value().replace(" ", "%20");
			startTime = dc.field("Start Time").value();
			endTime = dc.field("End Time").value();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");

			if (!startTime.isEmpty()) {
				try {
					start = simpleDateFormat.parse(startTime);
				} catch (ParseException e) {
					System.out.println("Wrong Date Format :" + startTime);
					return badRequest("Wrong Date Format :" + startTime);
				}
			}
			if (!endTime.isEmpty()) {
				try {
					end = simpleDateFormat.parse(endTime);
				} catch (ParseException e) {
					System.out.println("Wrong Date Format :" + endTime);
					return badRequest("Wrong Date Format :" + endTime);
				}
			}

			if (variableName.equals("Total Cloud Fraction")) {
				variableName = "clt";
			} else if (variableName.equals("Leaf Area Index")) {
				variableName = "lai";
			} else if (variableName.equals("Surface Temperature")) {
				variableName = "ts";
			} else if (variableName.equals("Sea Surface Temperature")) {
				variableName = "tos";
			} else if (variableName.equals("Precipitation Flux")) {
				variableName = "pr";
			} else if (variableName.equals("Eastward Near-Surface Wind")) {
				variableName = "uas";
			} else if (variableName.equals("Northward Near-Surface Wind")) {
				variableName = "vas";
			} else if (variableName.equals("Near-Surface Wind Speed")) {
				variableName = "sfcWind";
			} else if (variableName.equals("Sea Surface Height")) {
				variableName = "zos";
			} else if (variableName.equals("Equivalent Water Height Over Land")) {
				variableName = "zl";
			} else if (variableName.equals("Equivalent Water Height Over Ocean")) {
				variableName = "zo";
			} else if (variableName.equals("Ocean Heat Content Anomaly within 700 m Depth")) {
				variableName = "ohc700";
			} else if (variableName.equals("Ocean Heat Content Anomaly within 2000 m Depth")) {
				variableName = "ohc2000";
			} else if (variableName.equals("Surface Downwelling Longwave Radiation")) {
				variableName = "rlds";
			} else if (variableName.equals("Surface Downwelling Shortwave Radiation")) {
				variableName = "rsds";
			} else if (variableName.equals("Surface Upwelling Longwave Radiation")) {
				variableName = "rlus";
			} else if (variableName.equals("Surface Upwelling Shortwave Radiation")) {
				variableName = "rsus";
			} else if (variableName.equals("Surface Downwelling Clear-Sky Longwave Radiation")) {
				variableName = "rldscs";
			} else if (variableName.equals("Surface Downwelling Clear-Sky Shortwave Radiation")) {
				variableName = "rsdscs";
			} else if (variableName.equals("Surface Upwelling Clear-Sky Shortwave Radiation")) {
				variableName = "rsuscs";
			} else if (variableName.equals("TOA Incident Shortwave Radiation")) {
				variableName = "rsdt";
			} else if (variableName.equals("TOA Outgoing Clear-Sky Longwave Radiation")) {
				variableName = "rlutcs";
			} else if (variableName.equals("TOA Outgoing Longwave Radiation")) {
				variableName = "rlut";
			} else if (variableName.equals("TOA Outgoing Clear-Sky Shortwave Radiation")) {
				variableName = "rsutcs";
			} else if (variableName.equals("TOA Outgoing Shortwave Radiation")) {
				variableName = "rsut";
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}

		//Data source and variable names are parameters
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("model", dataSource);
		parameters.put("var", variableName);

		List<ServiceExecutionLog> response = queryServiceExecutionLogs(userId, start, end, executionPurpose, parameters);
		return ok(searchServiceLogResult.render(response));
	}

	private static List<ServiceExecutionLog> queryServiceExecutionLogs(
			String userId, Date startTime, Date endTime,
			String executionPurpose, Map<String, String> parameters) {
	List<ServiceExecutionLog> serviceLogList = new ArrayList<ServiceExecutionLog>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode queryJson = mapper.createObjectNode();
		if (userId != null && !userId.isEmpty()) {
			queryJson.put("userId", userId);
		}
		if (startTime != null ) {
			queryJson.put("executionStartTime", startTime.getTime());
		}
		if (endTime != null) {
			queryJson.put("executionEndTime", endTime.getTime());
		}
		if (executionPurpose != null && !executionPurpose.isEmpty()) {
			queryJson.put("purpose", executionPurpose);
		}

		if (parameters != null) {
			ObjectNode paramsNode = mapper.createObjectNode();
			for (String paramName : parameters.keySet()) {
				String paramValue = parameters.get(paramName);
				if (paramValue != null && !paramValue.isEmpty())
					paramsNode.put(paramName, paramValue);
			}
			if (paramsNode.size() > 0) {
				queryJson.set("parameters", paramsNode);
			}
		}

		JsonNode serviceLogNode = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.QUERY_SERVICE_LOG, queryJson);
		if (serviceLogNode == null || serviceLogNode.has("error")
				|| !serviceLogNode.isArray()) {
			return serviceLogList;
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
		return serviceLogList;
	}

}
