package controllers;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;

import models.Dataset;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.RESTfulCalls;
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DatasetController extends Controller {
	final static Form<Dataset> dataSetForm = Form
			.form(Dataset.class);
	
	public static Result searchDataset(){
		return ok(searchDataSet.render(dataSetForm));
	}
	
	public static Result showAllDatasets() {
		List<Dataset> dataSetsList = new ArrayList<Dataset>();
		JsonNode dataSetsNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_DATASETS);
		System.out.println("GET API: " + Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_DATASETS);
		// if no value is returned or error or is not json array
		if (dataSetsNode == null || dataSetsNode.has("error")
				|| !dataSetsNode.isArray()) {
			System.out.println("All oneDatasets format has error!");
		}

		// parse the json string into object
		for (int i = 0; i < dataSetsNode.size(); i++) {
			JsonNode json = dataSetsNode.path(i);
			Dataset oneDataset = new Dataset();
			oneDataset.setId(json.get("id").asLong());
			oneDataset.setName(json.get("name").asText());
			oneDataset.setAgencyId(json.get("agencyId").asText());
			oneDataset.setInstrument(json.get("instrument").get("name").asText());
			oneDataset.setPhysicalVariable(json.get("physicalVariable").asText());
			oneDataset.setCMIP5VarName(json.get("CMIP5VarName").asText());
			oneDataset.setUnits(json.get("units").asText());
			oneDataset.setGridDimension(json.get("gridDimension").asText());
			oneDataset.setSource(json.get("source").asText());
			oneDataset.setStatus(json.get("status").asText());
			oneDataset.setResponsiblePerson(json.get("responsiblePerson").asText());
			oneDataset.setComment(json.get("comment").asText());
			oneDataset.setDataSourceNameinWebInterface(json.get("dataSourceNameinWebInterface").asText());
			oneDataset.setVariableNameInWebInterface(json.get("variableNameInWebInterface").asText());
			oneDataset.setDataSourceInputParameterToCallScienceApplicationCode(json.get("dataSourceInputParameterToCallScienceApplicationCode").asText());
			oneDataset.setVariableNameInputParameterToCallScienceApplicationCode(json.get("variableNameInputParameterToCallScienceApplicationCode").asText());
			dataSetsList.add(oneDataset);
		}

		return ok(allDatasets.render(dataSetsList,
				dataSetForm));
	}
	
	public static Result getSearchResult(){
		Form<Dataset> dc = dataSetForm.bindFromRequest();
		ObjectNode jsonData = Json.newObject();
		String dataSetName = "";
		String agency = "";
		String instrument = "";
		String physicalVariable = "";
		String gridDimension = "";
		String startTime = "";
		String endTime = "";
		Date dataSetStartTime = new Date(0), dataSetEndTime = new Date();
		
		try {
			dataSetName = dc.field("Dataset Name").value();
			//Logger.info("data "+dataSource);
			agency = dc.field("Agency").value();
			instrument = dc.field("Instrument").value();
			physicalVariable = dc.field("Physical Variable").value();
			gridDimension = dc.field("Grid Dimension").value();
			startTime = dc.field("Dataset Start Time").value();
			endTime = dc.field("Dataset End Time").value();
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
			if (!startTime.isEmpty()) {
				try {
					dataSetStartTime = simpleDateFormat.parse(startTime);
					Date min = new Date(0);
					Date max = new Date();
					if (dataSetStartTime.before(min)) {
						dataSetStartTime = min;
					} else if (dataSetStartTime.after(max)) {
						dataSetStartTime = max;
					}
				} catch (ParseException e) {
					System.out.println("Wrong Date Format :" + startTime);
					return badRequest("Wrong Date Format :" + startTime);
				}
			}
			if (!endTime.isEmpty()) {
				try {
					dataSetEndTime = simpleDateFormat.parse(endTime);
					Date min = new Date(0);
					Date max = new Date();
					if (dataSetEndTime.before(min)) {
						dataSetEndTime = min;
					}
					else if (dataSetEndTime.after(max)) {
						dataSetEndTime = max;
					}
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

		List<Dataset> response = queryDataSet(dataSetName, agency, instrument, physicalVariable, gridDimension, dataSetStartTime, dataSetEndTime);
		return ok(dataSetList.render(response, dataSetForm));
	}
	
public static List<Dataset> queryDataSet(String dataSetName, String agency, String instrument, String physicalVariable, String gridDimension, Date dataSetStartTime, Date dataSetEndTime) {
		
		List<Dataset> dataset = new ArrayList<Dataset>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode queryJson = mapper.createObjectNode();
		queryJson.put("name", dataSetName);
		queryJson.put("agencyId", agency);
		queryJson.put("instrument", instrument);
		queryJson.put("physicalVariable", physicalVariable);
		queryJson.put("gridDimension", gridDimension);
		if (dataSetEndTime != null) {
			queryJson.put("dataSetEndTime", dataSetEndTime.getTime());
		}
		if (dataSetStartTime != null) {
			queryJson.put("dataSetStartTime", dataSetStartTime.getTime());
		}
		JsonNode dataSetNode = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.DATASET_QUERY, queryJson);
		if (dataSetNode == null || dataSetNode.has("error")
				|| !dataSetNode.isArray()) {
			return dataset;
		}

		// parse the json string into object
		for (int i = 0; i < dataSetNode.size(); i++) {
			JsonNode json = dataSetNode.path(i);
			Dataset newDataSet = deserializeJsonToDataSet(json);
			dataset.add(newDataSet);
		}
		return dataset;
	}

	private static Dataset deserializeJsonToDataSet(JsonNode json) {
		Dataset newDataSet = new Dataset();
		newDataSet.setId(json.get("id").asLong());
		newDataSet.setName(json.get("name").asText());
		newDataSet.setAgencyId(json.get("agencyId").asText());
		newDataSet.setInstrument(json.get("instrument").get("name").asText());
		newDataSet.setPhysicalVariable(json.get("physicalVariable").asText());
		newDataSet.setCMIP5VarName(json.get("CMIP5VarName").asText());
		newDataSet.setUnits(json.get("units").asText());
		newDataSet.setGridDimension(json.get("gridDimension").asText());
		newDataSet.setSource(json.get("source").asText());
		newDataSet.setStatus(json.get("status").asText());
		newDataSet.setResponsiblePerson(json.get("responsiblePerson").asText());
	//	dataset.setComments(json.get(""));
		newDataSet.setDataSourceNameinWebInterface(json.get("dataSourceNameinWebInterface").asText());
	//	Console.print("aaa"+dataset.getDataSourceName());
		newDataSet.setVariableNameInWebInterface(json.get("variableNameInWebInterface").asText());
		newDataSet.setDataSourceInputParameterToCallScienceApplicationCode(json.get("dataSourceInputParameterToCallScienceApplicationCode").asText());
		newDataSet.setVariableNameInputParameterToCallScienceApplicationCode(json.get("variableNameInputParameterToCallScienceApplicationCode").asText());
		String startTime = json.findPath("startTime").asText();
		String endTime = json.findPath("endTime").asText();
		Date tmpTime = null;
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(startTime);
			
			if (tmpTime != null) {
				newDataSet.setStartTime(new SimpleDateFormat("YYYYMM").format(tmpTime));
			}
	    } catch (ParseException e){	    
	    }
		
		try {
			tmpTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(endTime);
			
			if (tmpTime != null) {
				newDataSet.setEndTime(new SimpleDateFormat("YYYYMM").format(tmpTime));
			}
	    } catch (ParseException e){	    
	    	
	    }
		return newDataSet;
	}
}
