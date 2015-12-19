package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.DatasetLog;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.RESTfulCalls;
import views.html.allDatasetLogs;

public class DatasetLogController extends Controller {
	final static Form<DatasetLog> datasetLogForm = Form.form(DatasetLog.class);
	
	public static Result getAllDatasetLogs(){
		List<DatasetLog> datasetLogsList = new ArrayList<DatasetLog>();
		JsonNode datasetLogNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_DATASETLOGS);
		
		// if no value is returned or error or is not json array
		if (datasetLogNode == null || datasetLogNode.has("error")
				|| !datasetLogNode.isArray()) {
			return ok(allDatasetLogs.render(datasetLogsList, datasetLogForm));
		}

		// parse the json string into object
		for (int i = 0; i < datasetLogNode.size(); i++) {
			JsonNode json = datasetLogNode.path(i);
			DatasetLog oneLog = deserializeJsonToDatasetLog(json);
			datasetLogsList.add(oneLog);
		}

		return ok(allDatasetLogs.render(datasetLogsList, datasetLogForm));
	}
	
	private static DatasetLog deserializeJsonToDatasetLog(JsonNode json) {
		DatasetLog newDatasetLog = new DatasetLog();
		newDatasetLog.setId(json.get("id").asLong());
		newDatasetLog.setDatasetId(json.get("dataset").get("id").asLong());
		newDatasetLog.setServiceExecutionLogId(json.get("serviceExecutionLog").get("id").asLong());
		newDatasetLog.setDatasetName(json.get("dataset").get("name").asText());
		newDatasetLog.setDatasetSource(json.get("dataset").get("dataSourceNameinWebInterface").asText());
		newDatasetLog.setDatasetVariable(json.get("dataset").get("variableNameInWebInterface").asText());
		newDatasetLog.setUserName(json.get("user").get("firstName").asText()
				+ " " + json.get("user").get("lastName").asText());
		
		String datasetStudyStartTime = json.get("datasetStudyStartTime").asText();
		String datasetStudyEndTime = json.get("datasetStudyEndTime").asText();
		
		Date tmpTime = null;
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(datasetStudyStartTime);
			
			if (tmpTime != null) {
				newDatasetLog.setDatasetStudyStartTime(new SimpleDateFormat("YYYY-MM").format(tmpTime));
			}
	    } catch (ParseException e){	    
//	    	e.printStackTrace();
	    }
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(datasetStudyEndTime);
			
			if (tmpTime != null) {
				newDatasetLog.setDatasetStudyEndTime(new SimpleDateFormat("YYYY-MM").format(tmpTime));
			}
	    } catch (ParseException e){	    
//	    	e.printStackTrace();
	    }
		
		if(json.get("serviceExecutionLog").get("url") != null) {
			String serviceName = json.get("serviceExecutionLog").get("climateService").get("name").asText();
			String pageUrl = Constants.URL_SERVER
					+ Constants.LOCAL_HOST_PORT + "/assets/html/service"
					+ serviceName.substring(0, 1).toUpperCase()
					+ serviceName.substring(1) + ".html" + json.get("serviceExecutionLog").get("url").asText();
			newDatasetLog.setUrl(pageUrl);
		}
		
		return newDatasetLog;
	
	}
}
