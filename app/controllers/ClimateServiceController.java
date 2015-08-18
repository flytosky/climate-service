package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import org.apache.commons.lang3.StringEscapeUtils;

import models.ClimateService;
import models.User;
import play.Logger;
import play.data.DynamicForm;
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

public class ClimateServiceController extends Controller {

	final static Form<ClimateService> climateServiceForm = Form
			.form(ClimateService.class); 
	
	public static Result addAClimateService() {
		return ok(registerAClimateService.render(climateServiceForm));
	}

	public static Result showAllClimateServices() {
		List<ClimateService> climateServicesList = new ArrayList<ClimateService>();
		JsonNode climateServicesNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_CLIMATE_SERVICES);
		System.out.println("GET API: " + Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_CLIMATE_SERVICES);
		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(allClimateServices.render(climateServicesList,
				climateServiceForm));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService oneService = new ClimateService();
			oneService.setName(json.path("name").asText());
			oneService.setPurpose(json.path("purpose").asText());
			// URL here is the dynamic page url
			String name = json.path("name").asText();
			String pageUrl = Constants.URL_SERVER + Constants.LOCAL_HOST_PORT + "/assets/html/service" + 
					name.substring(0, 1).toUpperCase() + name.substring(1) + ".html";
			oneService.setUrl(pageUrl);
			// newService.setCreateTime(json.path("createTime").asText());
			oneService.setScenario(json.path("scenario").asText());
			oneService.setVersionNo(json.path("versionNo").asText());
			oneService.setRootServiceId(json.path("rootServiceId").asLong());
			climateServicesList.add(oneService);
		}

		return ok(allClimateServices.render(climateServicesList,
				climateServiceForm));
	}

	public static void addClimateService() {
		Form<ClimateService> cs = climateServiceForm.bindFromRequest();

		ObjectNode jsonData = Json.newObject();
		try {

			String originalClimateServiceName = cs.field("name").value();
			String newClimateServiceName = originalClimateServiceName.replace(
					' ', '-');

			// name should not contain spaces
			if (newClimateServiceName != null
					&& !newClimateServiceName.isEmpty()) {
				jsonData.put("name", newClimateServiceName);
			}
			jsonData.put("creatorId", 1); // TODO, since we don't have
											// login/account id yet use a
											// default val
			jsonData.put("purpose", cs.field("purpose").value());
			jsonData.put("url", cs.field("url").value());
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			// get current date time with Date()
			Date date = new Date();
			jsonData.put("createTime", dateFormat.format(date));
			jsonData.put("scenario", cs.field("scenario").value());
			jsonData.put("versionNo", cs.field("version").value());
			jsonData.put("rootServiceId", cs.field("rootServiceId").value());

			// POST Climate Service JSON data
			JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT 
					+ Constants.ADD_CLIMATE_SERVICE, jsonData);

			// flash the response message
//			Application.flashMsg(response);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		//return null;
	}

	public static Result serviceModels() {
		JsonNode jsonData = request().body().asJson();
		System.out.println("JSON data: " + jsonData);
		String url = jsonData.get("climateServiceCallUrl").toString();
		System.out.println("JPL climate service model call url: " + url);
		
		// transfer JsonNode to Object
		ObjectNode object = (ObjectNode) jsonData;
		object.remove("climateServiceCallUrl");

		System.out.println("JSON data after removing: " + (JsonNode) object);

		// from JsonNode to java String, always has "" quotes on the two sides
		JsonNode response = RESTfulCalls.postAPI(
				url.substring(1, url.length() - 1), (JsonNode) object);
		System.out.println("Response: " + response);

		// flash the response message
		Application.flashMsg(response);
		System.out
				.println(ok("Climate Service model has been called successfully!"));
		// return jsonData
		return ok(response);
	}

	// send dynamic page string
	public static Result passPageStr() {
		String str = request().body().asJson().get("pageString").toString();
		String name = request().body().asJson().get("name").toString();
		String purpose = request().body().asJson().get("purpose").toString();
		String url = request().body().asJson().get("url").toString();
		String outputButton = request().body().asJson().get("pageOutput").toString();

		System.out.println("page string: " + str);
		System.out.println("climate service name: " + name);

		ObjectNode jsonData = Json.newObject();
		jsonData.put("pageString", str);

		// POST Climate Service JSON data to CMU 9020 backend
		// One copy in backend and one copy in frontend
		JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.SAVE_CLIMATE_SERVICE_PAGE, jsonData);
		
		
		System.out.println("WARNING!!!!!!");
		// save page in front-end
		savePage(str, name, purpose, url, outputButton);

		// flash the response message
		Application.flashMsg(response);
		return ok("Climate Service Page has been saved succussfully!");
	}

	public static void savePage(String str, String name, String purpose,
			String url, String outputButton) {
		System.out.println("output button test: " + outputButton);
		// Remove delete button from preview page
		String result = str
				.replaceAll(
						"<td><button type=\\\\\"button\\\\\" class=\\\\\"btn btn-danger\\\\\" onclick=\\\\\"Javascript:deleteRow\\(this\\)\\\\\">delete</button></td>",
						"");

		result = StringEscapeUtils.unescapeJava(result);
		outputButton = StringEscapeUtils.unescapeJava(outputButton);
		System.out.println("output button test: " + outputButton);
		
		// remove the first char " and the last char " of result, name and
		// purpose
		result = result.substring(1, result.length() - 1);
		outputButton = outputButton.substring(1, outputButton.length() - 1);
		
		name = name.substring(1, name.length() - 1);
		purpose = purpose.substring(1, purpose.length() - 1);

		String str11 = Constants.htmlHead1;
		// System.out.println("head1: " + str11);
		String str12 = Constants.htmlHead2;
		// System.out.println("head2: " + str12);
		String str13 = Constants.htmlHead3;
		// System.out.println("head3: " + str13);

		String str21 = Constants.htmlTail1;
		String str22 = Constants.htmlTail2;
		String str23 = Constants.htmlTail3;

		result = str11 + name + str12 + purpose + str13 + result + str21
				+ url.substring(1, url.length() - 1) + str22 + outputButton + str23;

		name = name.replace(" ", "");

		// Java file name cannot start with number and chars like '_' '-'...
		String location = "public/html/" + "service"
				+ name.substring(0, 1).toUpperCase() + name.substring(1)
				+ ".html";

		File theDir = new File("public/html");
		
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: public/html");
			boolean create = false;

			try {
				theDir.mkdir();
				create = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (create) {
				System.out.println("DIR created");
			}
		}

		try {
			File file = new File(location);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(result);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void flashMsg(JsonNode jsonNode) {
		Iterator<Entry<String, JsonNode>> it = jsonNode.fields();
		while (it.hasNext()) {
			Entry<String, JsonNode> field = it.next();
			flash(field.getKey(), field.getValue().asText());
		}
	}
	
	public static Result mostRecentlyAddedClimateServices() {
		
		List<ClimateService> climateServices = new ArrayList<ClimateService>();

		JsonNode climateServicesNode = RESTfulCalls
				.getAPI(Constants.URL_HOST
						+ Constants.CMU_BACKEND_PORT
						+ Constants.GET_MOST_RECENTLY_ADDED_CLIMATE_SERVICES_CALL);

		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(mostRecentlyUsedServices.render(climateServices));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService newService = new ClimateService();
			newService.setId(json.get("id").asLong());
			newService.setName(json.get("name").asText());
			newService.setPurpose(json.findPath("purpose").asText());
			//newService.setUrl(json.findPath("url").asText());
			String name = json.path("name").asText();
			String pageUrl = Constants.URL_SERVER + Constants.LOCAL_HOST_PORT + "/assets/html/service" + 
					name.substring(0, 1).toUpperCase() + name.substring(1) + ".html";
			newService.setUrl(pageUrl);
			//newService.setCreateTime(json.findPath("createTime").asText());
			newService.setScenario(json.findPath("scenario").asText());
			newService.setVersionNo(json.findPath("versionNo").asText());
			newService.setRootServiceId(json.findPath("rootServiceId").asLong());
			climateServices.add(newService);
		}
		
		return ok(mostRecentlyAddedServices.render(climateServices));
	}
	
	public static Result mostPopularServices() {
		List<ClimateService> climateServices = new ArrayList<ClimateService>();

		JsonNode climateServicesNode = RESTfulCalls
				.getAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT + Constants.GET_MOST_POPULAR_CLIMATE_SERVICES_CALL);

		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(mostPopularServices.render(climateServices));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService newService = new ClimateService();
			newService.setId(json.get("id").asLong());
			newService.setName(json.get("name").asText());
			newService.setPurpose(json.findPath("purpose").asText());
			//newService.setUrl(json.findPath("url").asText());
			String name = json.path("name").asText();
			String pageUrl = Constants.URL_SERVER + Constants.LOCAL_HOST_PORT + "/assets/html/service" + 
					name.substring(0, 1).toUpperCase() + name.substring(1) + ".html";
			newService.setUrl(pageUrl);
			//newService.setCreateTime(json.findPath("createTime").asText());
			newService.setScenario(json.findPath("scenario").asText());
			newService.setVersionNo(json.findPath("versionNo").asText());
			newService.setRootServiceId(json.findPath("rootServiceId").asLong());
			climateServices.add(newService);
		}
		
		return ok(mostPopularServices.render(climateServices));
	}
	
	public static Result mostRecentlyUsedClimateServices() {
		
		List<ClimateService> climateServices = new ArrayList<ClimateService>();

		JsonNode climateServicesNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_MOST_RECENTLY_USED_CLIMATE_SERVICES_CALL);
		
		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(mostRecentlyUsedServices.render(climateServices));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService newService = new ClimateService();
			newService.setId(json.get("id").asLong());
			newService.setName(json.get("name").asText());
			newService.setPurpose(json.findPath("purpose").asText());
			//newService.setUrl(json.findPath("url").asText());
			String name = json.path("name").asText();
			String pageUrl = Constants.URL_SERVER + Constants.LOCAL_HOST_PORT + "/assets/html/service" + 
					name.substring(0, 1).toUpperCase() + name.substring(1) + ".html";
			newService.setUrl(pageUrl);
			//newService.setCreateTime(json.findPath("createTime").asText());
			newService.setScenario(json.findPath("scenario").asText());
			newService.setVersionNo(json.findPath("versionNo").asText());
			newService.setRootServiceId(json.findPath("rootServiceId").asLong());
			climateServices.add(newService);
		}
		
		return ok(mostRecentlyUsedServices.render(climateServices));
	}
	
	public static Result replaceFile() {
	  	File result =  request().body().asRaw().asFile();
	  	System.out.println("result: " + request().body().asRaw().asFile());
	  	
//	  	String content = readFile(result.getName(), StandardCharsets.UTF_8);
	  	System.out.println("result body: "+result.toString());
	  	
	  	String line = "";
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(result.getAbsolutePath()));
	        StringBuilder sb = new StringBuilder();
	        line = br.readLine();
	        int count = 0;
	        while (line != null && count < 22) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	            //System.out.println("line:  "+line.toString());
	            count++;
	        }
	        br.close();
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    //TEMPOARY SOLUTION : get the fileName from the html page
	    String tempName = line.substring(24, line.length()-5);		  
	    String fileName = "public/html/service" + tempName.substring(0, 1).toUpperCase() + tempName.substring(1)+ ".html";
	    System.out.println("fileName: " + fileName);
	  	
	  	//replace the page in the frontend Server
	  	try {		  		
		  	Path newPath = Paths.get(fileName);
		  	Files.move(result.toPath(), newPath, REPLACE_EXISTING);
	  	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	  	//executeReplace(result);		
		
	  	
	  	return ok("File uploaded");
	}

public static void executeReplace(String result) {
	
	try {
		String path = "public/html/se.html";			
		File theDir = new File("public/html");		
		
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: public/html");
			boolean create = false;

			try {
				theDir.mkdir();
				create = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (create) {
				System.out.println("DIR created");
			}
		}
		
		
		File file = new File(path);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));			
		output.write(result);			
		output.close();
		System.out.println("Beeping!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
}
	
}
