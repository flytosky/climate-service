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
		JsonNode response = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.GET_DATASET_AND_USER);
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
	
	public static Result getSpecifiedKnowledgeGraph() {
		JsonNode json = request().body().asJson();
		String parameter1 = json.path("param1").asText();
		String parameter2 = json.path("param2").asText();
		String parameter3 = json.path("param3").asText();
		System.out.println(parameter1 + "*******" + parameter2 + "*******" + parameter3);
		JsonNode response = null;
		//ObjectNode jsonData = Json.newObject();
		try {
//			jsonData.put("param1", parameter1);
//			jsonData.put("param2", parameter2);
//			jsonData.put("param3", parameter2);
//			response = RESTfulCalls.postAPI(Constants.URL_HOST
//					+ Constants.CMU_BACKEND_PORT + Constants.GET_DATASET_AND_USER, jsonData);
			if(parameter1.equals("User") && parameter2.equals("Dataset")) {
				response = RESTfulCalls.getAPI(Constants.URL_SERVER
						+ Constants.CMU_BACKEND_PORT + Constants.GET_DATASET_AND_USER);
			}else if(parameter1.equals("User") && parameter2.equals("Service")) {
				response = RESTfulCalls.getAPI(Constants.URL_SERVER
						+ Constants.CMU_BACKEND_PORT + Constants.GET_SERVICE_AND_USER);
			}else if(parameter1.equals("Dataset") && parameter2.equals("Service")) {
				response = RESTfulCalls.getAPI(Constants.URL_SERVER
						+ Constants.CMU_BACKEND_PORT + Constants.GET_DATASET_AND_SERVICE);
			}
			Application.flashMsg(response);
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
