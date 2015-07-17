package controllers;

import models.BugReport;
import models.ClimateService;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import java.net.URLEncoder;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class BugReportController extends Controller {
	final static Form<BugReport> bugReportForm = Form.form(BugReport.class);

	public static Result reports() {
		return ok(bugReporting.render(bugReportForm));
	}

	public static Result newReport() {
		Form<BugReport> filledForm = bugReportForm.bindFromRequest();
		
		ObjectNode jsonData = Json.newObject();
		try {
			jsonData.put("title", filledForm.get().getTitle());
			jsonData.put("name", filledForm.get().getName());
			jsonData.put("email", filledForm.get().getEmail());
			jsonData.put("organization", filledForm.get().getOrganization());
			jsonData.put("description", filledForm.get().getDescription());
			jsonData.put("solved", 0);
			
			// POST Climate Service JSON data
			JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT 
					+ Constants.ADD_BUG_REPORT, jsonData);
			
			// flash the response message
			Application.flashMsg(response);
			
			return redirect(routes.BugReportController.list());
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		
		return ok(bugReporting.render(filledForm));
		
	}

	public static Result list() {
		List<BugReport> bugReportList = new ArrayList<BugReport>();
		
		JsonNode bugReportsNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_BUG_REPORTS);
		
		// if no value is returned or error or is not json array
		if (bugReportsNode == null || bugReportsNode.has("error")
				|| !bugReportsNode.isArray()) {
			return ok(bugs.render(bugReportList));
		}
		
		for (int i = 0; i < bugReportsNode.size(); i++) {
			JsonNode json = bugReportsNode.path(i);
			BugReport oneBugReport = new BugReport();
			oneBugReport.setId(json.get("id").asLong());
			oneBugReport.setTitle(json.get("title").asText());			
			oneBugReport.setName(json.get("name").asText());
			oneBugReport.setEmail(json.get("email").asText());
			oneBugReport.setOrganization(json.get("organization").asText());
			oneBugReport.setDescription(json.get("description").asText());
			oneBugReport.setSolved(json.get("solved").asInt());
			bugReportList.add(oneBugReport);
		}
		
		return ok(bugs.render(bugReportList));
	}
	
	public static Result deleteReport() {
		DynamicForm df = DynamicForm.form().bindFromRequest();
		
		try {
			int id = Integer.valueOf(df.field("idHolder").value());
			
			JsonNode response = RESTfulCalls.deleteAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT
					+ Constants.DELETE_ONE_BUG_REPORT 
					+ id);
			
			// flash the response message
			Application.flashMsg(response);
			
			return redirect(routes.BugReportController.list());

		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}
		return redirect(routes.BugReportController.list());
	}
	
	public static Result solveReport() {
		DynamicForm df = DynamicForm.form().bindFromRequest();
		
		ObjectNode jsonData = Json.newObject();
		try {
			long id = Long.valueOf(df.field("idHolder").value());
			JsonNode bugReportsNode = RESTfulCalls.getAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT
					+ Constants.GET_BUG_REPORT_BY_ID + id);
			
			jsonData.put("title", bugReportsNode.get("title").asText());
			jsonData.put("name", bugReportsNode.get("name").asText());
			jsonData.put("email", bugReportsNode.get("email").asText());
			jsonData.put("organization", bugReportsNode.get("organization").asText());
			jsonData.put("description", bugReportsNode.get("description").asText());
			jsonData.put("solved", 1);
			
			JsonNode response = RESTfulCalls.putAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT 
					+ Constants.UPDATE_BUG_REPORT + id, jsonData);
			
			// flash the response message
			Application.flashMsg(response);
			
			return redirect(routes.BugReportController.list());
			
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}
		return redirect(routes.BugReportController.list());
	}


}