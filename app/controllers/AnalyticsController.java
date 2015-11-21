package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.ServiceExecutionLog;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AnalyticsController extends Controller{

	final static Form<ServiceExecutionLog> serviceLogForm = Form
			.form(ServiceExecutionLog.class);
	
	
	public static Result getKnowledgeGraph(String param1, String param2, String param3) {
		String parameter1 = param1;
		String parameter2 = param2;
		String parameter3 = param3;
		JsonNode response = null;
		ObjectNode jsonData = Json.newObject();
		try {
			jsonData.put("param1", parameter1);
			jsonData.put("param2", parameter2);
			jsonData.put("param3", parameter3);
			response = RESTfulCalls.postAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT + Constants.GET_RELATIONAL_GRAPH, jsonData);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		String resStr = response.toString();
		return ok(knowledgeGraph.render(resStr));
	}
	
	public static Result getRelationalKnowledgeGraph() {
		return ok(relationalKnowledgeGraph.render());
	}
	
	public static Result getRecommend() {
		JsonNode response = RESTfulCalls.getAPI("http://einstein.sv.cmu.edu:9026/api/sgraph");
		String resStr = response.toString();
		return ok(recommend.render(resStr));
	}
	
	public static Result getDatasetRecommend() {
		JsonNode response = RESTfulCalls.getAPI("http://einstein.sv.cmu.edu:9026/api/dgraph");
		String resStr = response.toString();
		return ok(dataRecommend.render(resStr));
	}
	
	public static Result getScientistRecommend() {
		JsonNode response = RESTfulCalls.getAPI("http://einstein.sv.cmu.edu:9026/api/scgraph");
		String resStr = response.toString();
		return ok(dataRecommend.render(resStr));
	}
	
	public static Result getLogGraph() {
		JsonNode response = RESTfulCalls.getAPI("http://einstein.sv.cmu.edu:9026/api/ugraph");
		String resStr = response.toString();
		return ok(recommend.render(resStr));
	}
	
	public static Result getSearchAndGenerateWorkflow() {
		return ok(searchGenerateWorkflow.render(serviceLogForm));
	}
	
	public static Result getShortestPath() {
		JsonNode response = null;
		JsonNode json = request().body().asJson();
		String startId = json.path("startId").asText();
		String endId = json.path("endId").asText();
		ObjectNode jsonData = Json.newObject();
		try {
			jsonData.put("startId", startId);
			jsonData.put("endId", endId);
			
			response = RESTfulCalls.getAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT + Constants.GET_SHORTEST_PATH + startId + "/target/" + endId + "/json");

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		return ok(response);
	}

	
	
	public static Result getSpecifiedKnowledgeGraph() {
		JsonNode json = request().body().asJson();
		String parameter1 = json.path("param1").asText();
		String parameter2 = json.path("param2").asText();
		String parameter3 = json.path("param3").asText();
		JsonNode response = null;
		ObjectNode jsonData = Json.newObject();
		try {
			jsonData.put("param1", parameter1);
			jsonData.put("param2", parameter2);
			jsonData.put("param3", parameter3);
			response = RESTfulCalls.postAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT + Constants.GET_RELATIONAL_GRAPH, jsonData);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		return ok(response);
	}
	
	public static Result getPartKnowledgeGraph() {
		JsonNode json = request().body().asJson();
		String parameter1 = json.path("param1").asText();
		String parameter2 = json.path("param2").asText();
		String groupName = json.path("groupName").asText();
		
		
		String startTime = json.path("startTime").asText();
		String endTime = json.path("endTime").asText();
		
		long id = json.path("id").asLong();
		
		ObjectNode jsonData = Json.newObject();
		
		Date executionStartTime = null, executionEndTime= null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		
		if (!startTime.isEmpty()) {
			try {
				executionStartTime = simpleDateFormat.parse(startTime);
		        jsonData.put("executionStartTime", executionStartTime.getTime());
			} catch (ParseException e) {
				System.out.println("Wrong Date Format :" + startTime);
				return badRequest("Wrong Date Format :" + startTime);
			}
		}
		if (!endTime.isEmpty()) {
			try {
				executionEndTime = simpleDateFormat.parse(endTime);
				jsonData.put("executionEndTime", executionEndTime.getTime());
			} catch (ParseException e) {
				System.out.println("Wrong Date Format :" + endTime);
				return badRequest("Wrong Date Format :" + endTime);
			}
		}
		
		String combination = parameter1 + parameter2 + groupName;
		
		JsonNode response = null;
		
		try {
			jsonData.put("id", id);
			if(!startTime.isEmpty() || !endTime.isEmpty()) {
				response = RESTfulCalls.postAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/datasetLog/queryDatasets", jsonData);
			}else {
				switch(combination) {
				case "UserDatasetuser":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneUserWithAllDatasetAndCountByUserId/" + id + "/json");
					break;
				case "UserDatasetdataset":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneDatasetWithAllUserAndCountByDatasetId/" + id + "/json");
					break;
				case "UserServiceuser":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneUserWithAllServiceAndCountByUserId/" + id + "/json");
					break;
				case "UserServiceservice":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneServiceWithAllUserAndCountByServiceId/" + id + "/json");
					break;
				case "DatasetServiceservice":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneServiceWithAllDatasetAndCountByServiceId/" + id + "/json");
					break;
				case "DatasetServicedataset":
					response = RESTfulCalls.getAPI(Constants.URL_HOST
							+ Constants.CMU_BACKEND_PORT + "/analytics/getOneDatasetWithAllServiceAndCountByDatasetId/" + id + "/json");
					break;
				default:
					break;
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		return ok(response);
	}
}
