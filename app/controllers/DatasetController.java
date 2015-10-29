package controllers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.ClimateService;
import models.Dataset;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

public class DatasetController extends Controller {
	final static Form<Dataset> dataSetForm = Form
			.form(Dataset.class);
	
	public static Result searchDataset(){
		return ok(searchDataSet.render(dataSetForm));
	}
	
//	public static Result mostPopularDatasets() {
//		List<Dataset> datasets = queryFirstKDatasets("", "", "", "", "", new Date(0), new Date(), 1);
//		return ok(dataSetListPopular.render(dataSetForm, datasets));
//	}
	
	public static Result showAllDatasets() {
		List<Dataset> dataSetsList = new ArrayList<Dataset>();
		JsonNode dataSetsNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_DATASETS);
		// if no value is returned or error or is not json array
		if (dataSetsNode == null || dataSetsNode.has("error")
				|| !dataSetsNode.isArray()) {
			System.out.println("All oneDatasets format has error!");
			return ok(allDatasets.render(dataSetsList,
					dataSetForm));
		}

		// parse the json string into object
		for (int i = 0; i < dataSetsNode.size(); i++) {
			JsonNode json = dataSetsNode.path(i);
			Dataset oneDataset = deserializeJsonToDataSet(json);
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
		int k = 5; // Set the first popular K datasets
		List<Dataset> datasetsTopK = queryFirstKDatasets(dataSetName, agency, instrument, physicalVariable, gridDimension, dataSetStartTime, dataSetEndTime, k);
		return ok(dataSetList.render(response, dataSetForm, datasetsTopK));
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

public static List<Dataset> queryFirstKDatasets(String dataSetName, String agency, String instrument, String physicalVariable, String gridDimension, Date dataSetStartTime, Date dataSetEndTime, int k) {
	
	List<Dataset> dataset = new ArrayList<Dataset>();
	ObjectMapper mapper = new ObjectMapper();
	ObjectNode queryJson = mapper.createObjectNode();
	queryJson.put("name", dataSetName);
	queryJson.put("agencyId", agency);
	queryJson.put("instrument", instrument);
	queryJson.put("physicalVariable", physicalVariable);
	queryJson.put("gridDimension", gridDimension);
	queryJson.put("k", k);
	if (dataSetEndTime != null) {
		queryJson.put("dataSetEndTime", dataSetEndTime.getTime());
	}
	if (dataSetStartTime != null) {
		queryJson.put("dataSetStartTime", dataSetStartTime.getTime());
	}
	JsonNode dataSetNode = RESTfulCalls.postAPI(Constants.URL_HOST
			+ Constants.CMU_BACKEND_PORT + Constants.GET_MOST_K_POPULAR_DATASETS_CALL, queryJson);
	if (dataSetNode == null || dataSetNode.has("error")
			|| !dataSetNode.isArray()) {
		return dataset;
	}

	// parse the json string into object
	for (int i = 0; i < dataSetNode.size(); i++) {
		JsonNode json = dataSetNode.path(i);
		Dataset newDataSet = deserializeJsonToDataSet(json);
		long id = newDataSet.getId();
		JsonNode climateSetNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.GET_TOP_K_USED_CLIMATE_SERVICES_BY_DATASET_ID + "/" + id);
		List<ClimateService> climateServices = new ArrayList<ClimateService>();
		for (int j = 0; j < climateSetNode.size(); j++) {
			JsonNode json1 = climateSetNode.path(j);
			ClimateService oneService = ClimateServiceController.deserializeJsonToClimateService(json1);
			climateServices.add(oneService);
		}
		newDataSet.setClimateServices(climateServices);
		dataset.add(newDataSet);
	}
	return dataset;
}

	public static Dataset deserializeJsonToDataSet(JsonNode json) {
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
		newDataSet.setDataSourceNameinWebInterface(json.get("dataSourceNameinWebInterface").asText());
		newDataSet.setVariableNameInWebInterface(json.get("variableNameInWebInterface").asText());
		newDataSet.setDataSourceInputParameterToCallScienceApplicationCode(json.get("dataSourceInputParameterToCallScienceApplicationCode").asText());
		newDataSet.setVariableNameInputParameterToCallScienceApplicationCode(json.get("variableNameInputParameterToCallScienceApplicationCode").asText());
		newDataSet.setAgencyURL(json.findPath("agencyURL").asText());
		newDataSet.setInstrumentURL(json.findPath("instrument").findPath("instrumentURL").asText());
		String startTime = json.get("startTime").asText();
		String endTime = json.get("endTime").asText();
		Date tmpStartTime = null;
		Date tmpEndTime = null;
		
		try {
			tmpStartTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(startTime);
			
			if (tmpStartTime != null) {
				newDataSet.setStartTime(new SimpleDateFormat("YYYY-MM").format(tmpStartTime));
			}
	    } catch (ParseException e){	    
	    	System.out.println(e);
	    }
		
		try {
			tmpEndTime = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")).parse(endTime);
			
			if (tmpEndTime != null) {
				newDataSet.setEndTime(new SimpleDateFormat("YYYY-MM").format(tmpEndTime));
			}
	    } catch (ParseException e){	    
	    	System.out.println(e);
	    }
		
		DateTime dateTimeFrom = new DateTime(tmpStartTime);  
        DateTime dateTimeTo = new DateTime(tmpEndTime); 
        Period period = new Period(dateTimeFrom, dateTimeTo).minusYears(1);
        PeriodFormatter formatter = new PeriodFormatterBuilder()  
        .appendYears().appendSuffix(" year "," years ")
        .appendMonths().appendSuffix(" month "," months ")
        .printZeroNever()  
        .toFormatter();
		String duration = String.valueOf(formatter.print(period));
		newDataSet.setDuration(duration);
		
		return newDataSet;
	}
	
}
