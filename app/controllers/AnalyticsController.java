package controllers;

import models.ServiceExecutionLog;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.RESTfulCalls;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;

public class AnalyticsController extends Controller{

	final static Form<ServiceExecutionLog> serviceLogForm = Form
			.form(ServiceExecutionLog.class);
	
	
	public static Result getKnowledgeGraph() {
		JsonNode response = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.GET_DATASET_AND_USER);
		String resStr = response.toString();
		return ok(knowledgeGraph.render(resStr));
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
}
