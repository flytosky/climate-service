package controllers;

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
	
	
	public static Result getKnowledgeGraph() {
		String parameter1 = "User";
		String parameter2 = "Dataset";
		String parameter3 = "Service";
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
		String combination = parameter1 + parameter2 + groupName;
		long id = json.path("id").asLong();
		JsonNode response = null;
		ObjectNode jsonData = Json.newObject();
		try {
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
