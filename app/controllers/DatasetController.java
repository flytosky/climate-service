package controllers;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
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
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;
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
	
}
