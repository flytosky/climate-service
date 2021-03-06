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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import org.apache.commons.lang3.StringEscapeUtils;

import models.ClimateService;
import models.Dataset;
import models.ServiceConfigurationItem;
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
import com.fasterxml.jackson.databind.ObjectMapper;
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
		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(allClimateServices.render(climateServicesList,
					climateServiceForm));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService oneService = deserializeJsonToClimateService(json);
			climateServicesList.add(oneService);
		}

		return ok(allClimateServices.render(climateServicesList,
				climateServiceForm));
	}

	public static Result addClimateService() {
		// Form<ClimateService> cs = climateServiceForm.bindFromRequest();
		JsonNode json = request().body().asJson();
		String name = json.path("name").asText();
		String purpose = json.path("purpose").asText();
		String url = json.path("url").asText();
		String scenario = json.path("scenario").asText();
		String versionNo = json.path("version").asText();
		String rootServiceId = json.path("rootServiceId").asText();

		JsonNode response = null;
		ObjectNode jsonData = Json.newObject();
		try {

			String originalClimateServiceName = name;
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
			jsonData.put("purpose", purpose);
			jsonData.put("url", url);
			DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssz");
			// get current date time with Date()
			Date date = new Date();
			jsonData.put("createTime", dateFormat.format(date));
			jsonData.put("scenario", scenario);
			jsonData.put("versionNo", versionNo);
			jsonData.put("rootServiceId", rootServiceId);

			// POST Climate Service JSON data
			response = RESTfulCalls.postAPI(Constants.URL_HOST
					+ Constants.CMU_BACKEND_PORT
					+ Constants.ADD_CLIMATE_SERVICE, jsonData);

			// flash the response message
			System.out.println("***************" + response);
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
		String outputButton = request().body().asJson().get("pageOutput")
				.toString();
		String dataListContent = request().body().asJson()
				.get("dataListContent").toString();

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
		savePage(str, name, purpose, url, outputButton, dataListContent);

		// flash the response message
		Application.flashMsg(response);
		return ok("Climate Service Page has been saved succussfully!");
	}

	public static Result ruleEngineData() {
		JsonNode result = request().body().asJson();
		// System.out.println("ticking!");
		System.out.println(result);

		return ok("good");
	}

	public static Result addAllParameters() {
		JsonNode result = request().body().asJson();
		System.out.println(result);
		System.out.println("--------------------------");
		Iterator<JsonNode> ite = result.iterator();

		while (ite.hasNext()) {

			JsonNode tmp = ite.next();
			System.out.println(tmp);
			JsonNode response = RESTfulCalls.postAPI(
					Constants.URL_HOST + Constants.CMU_BACKEND_PORT
							+ Constants.ADD_ALL_PARAMETERS, tmp);
			System.out.println("=========" + response);
		}

		return ok("good");
	}

	public static void savePage(String str, String name, String purpose,
			String url, String outputButton, String dataListContent) {
		System.out.println("output button test: " + outputButton);
		// Remove delete button from preview page
		String result = str
				.replaceAll(
						"<td><button type=\\\\\"button\\\\\" class=\\\\\"btn btn-danger\\\\\" onclick=\\\\\"Javascript:deleteRow\\(this,\\d+\\)\\\\\">delete</button></td>",
						"");

		dataListContent = StringEscapeUtils.unescapeJava(dataListContent);
		result = StringEscapeUtils.unescapeJava(result);
		outputButton = StringEscapeUtils.unescapeJava(outputButton);
		System.out.println("output button test: " + outputButton);

		// remove the first char " and the last char " of result, name and
		// purpose
		dataListContent = dataListContent.substring(1,
				dataListContent.length() - 1);
		result = result.substring(1, result.length() - 1);
		outputButton = outputButton.substring(1, outputButton.length() - 1);

		name = name.substring(1, name.length() - 1);
		purpose = purpose.substring(1, purpose.length() - 1);

		String putVarAndDataList = Constants.putVar + dataListContent;
		System.out.println("putVarAndDataList: " + putVarAndDataList);

		String str11 = Constants.htmlHead1;
		// System.out.println("head1: " + str11);
		String str12 = Constants.htmlHead2;
		// System.out.println("head2: " + str12);
		String str13 = Constants.htmlHead3;
		// System.out.println("head3: " + str13);
		String str14 = Constants.htmlHead4;

		String str21 = Constants.htmlTail1;
		String str22 = Constants.htmlTail2;
		String str23 = Constants.htmlTail3;

		result = str11 + putVarAndDataList + str12 + name + str13 + purpose
				+ str14 + result + str21 + url.substring(1, url.length() - 1)
				+ str22 + outputButton + str23;

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

		JsonNode climateServicesNode = RESTfulCalls.getAPI(Constants.URL_HOST
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
			ClimateService newService = deserializeJsonToClimateService(json);
			climateServices.add(newService);
		}

		return ok(mostRecentlyAddedServices.render(climateServices));
	}

	public static Result mostPopularServices() {
		List<ClimateService> climateServices = new ArrayList<ClimateService>();

		JsonNode climateServicesNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_MOST_POPULAR_CLIMATE_SERVICES_CALL);

		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(mostPopularServices.render(climateServices));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService newService = deserializeJsonToClimateService(json);
			climateServices.add(newService);
		}

		return ok(mostPopularServices.render(climateServices));
	}


	public static Result getUserByEmail() {
		JsonNode result = request().body().asJson();
		String email = result.get("email").toString();
		email = email.substring(1, email.length()-1);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode queryJson = mapper.createObjectNode();
		queryJson.put("email", email);

		System.out.println("SEE getUserByEmail: " + queryJson);

		JsonNode userIdNode = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.GET_USER_BY_EMAIL, queryJson);

		// parse the json string into object
		String id = userIdNode.findPath("id").asText();
		System.out.println("SEE getUserByEmail ID: " + id);

		return ok(id);
	}


	public static Result getServiceByKeyword() {
		System.out.println("xxxx");
		JsonNode result = request().body().asJson();
		System.out.println(result);
		String userId = result.get("userId").toString();
		userId = userId.substring(1,userId.length()-1);

		String url = "http://einstein.sv.cmu.edu:9043/getTopKUserBasedCFRecommendedServiceByUsername?username="+userId+"&top_num=5";

		JsonNode serviceNode = RESTfulCalls.getAPI(url);

		// parse the json string into object
		String res = "";
		for (int i = 0; i < serviceNode.size(); i++) {
			JsonNode json = serviceNode.path(i);
			String serviceName = json.findPath("service").asText();
			res += serviceName + "!";
		}

		System.out.println("SEE getUserByEmail ID: " + res);

		return ok(res);
	}

	public static Result recommendationSummary(String userId, int id, String keyword) {

		keyword = keyword.replaceAll(" ", "%20");

		List<String> userBasedDataset = new ArrayList<String>();

		List<String> itemBasedDataset = new ArrayList<String>();

		List<String> featureBasedDataset = new ArrayList<String>();

		List<String> userBasedDatasetHybrid = new ArrayList<String>();

		List<ClimateService> climateServices = new ArrayList<ClimateService>();

		List<Dataset> dataSetsList = new ArrayList<Dataset>();

		List<User> usersList = new ArrayList<User>();

		List<User> userSimilarList = new ArrayList<User>();




		JsonNode usersNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_USERS);

		JsonNode usersSimilarNode = RESTfulCalls.getAPI(Constants.URL_SERVER
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_TOP_K_SIMILAR_USERS + id + "/k/" + "12" + "/json");

		// if no value is returned or error or is not json array
		if (usersNode == null || usersNode.has("error")
				|| !usersNode.isArray()) {
			return ok(recommendationSummary.render(climateServices, dataSetsList, usersList, userBasedDataset, featureBasedDataset, itemBasedDataset, userId, userSimilarList, userBasedDatasetHybrid, keyword));
		}


		JsonNode climateServicesNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_MOST_POPULAR_CLIMATE_SERVICES_CALL);

		// if no value is returned or error or is not json array
		if (climateServicesNode == null || climateServicesNode.has("error")
				|| !climateServicesNode.isArray()) {
			return ok(recommendationSummary.render(climateServices, dataSetsList, usersList, userBasedDataset, featureBasedDataset, itemBasedDataset, userId, userSimilarList, userBasedDatasetHybrid, keyword));
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService newService = deserializeJsonToClimateService(json);
			climateServices.add(newService);
		}


		// parse the json string into object
		for (int i = 0; i < usersNode.size(); i++) {
			JsonNode json = usersNode.path(i);
			User oneUser = new User();
			oneUser.setId(json.findPath("id").asLong());
			oneUser.setUserName(json.findPath("userName").asText());
			oneUser.setPassword(json.findPath("password").asText());
			oneUser.setFirstName(json.findPath("firstName").asText());
			oneUser.setMiddleInitial(json.findPath("middleInitial").asText());
			oneUser.setLastName(json.findPath("lastName").asText());
			oneUser.setAffiliation(json.findPath("affiliation").asText());
			oneUser.setEmail(json.findPath("email").asText());
			oneUser.setResearchFields(json.findPath("researchFields").asText());

			usersList.add(oneUser);
		}

		for (int i = 0; i < usersSimilarNode.size(); i++) {
			JsonNode json = usersSimilarNode.path(i);
			User oneUser = new User();
			oneUser.setId(json.findPath("id").asLong());
			oneUser.setUserName(json.findPath("userName").asText());
			oneUser.setPassword(json.findPath("password").asText());
			oneUser.setFirstName(json.findPath("firstName").asText());
			oneUser.setMiddleInitial(json.findPath("middleInitial").asText());
			oneUser.setLastName(json.findPath("lastName").asText());
			oneUser.setAffiliation(json.findPath("affiliation").asText());
			oneUser.setEmail(json.findPath("email").asText());
			oneUser.setResearchFields(json.findPath("researchFields").asText());

			userSimilarList.add(oneUser);
		}

		int k = Integer.MAX_VALUE; // Set the first popular K datasets
		dataSetsList = DatasetController.queryFirstKDatasetsWithoutClimateService("", "", "", "", "", new Date(0), new Date(), k);



		JsonNode userBased = RESTfulCalls.getAPI(Constants.URL_SERVER
				+ Constants.URL_FLASK
				+ Constants.GET_TOP_K_USER_BASED_DATASET1 + userId
				+ Constants.GET_TOP_K_USER_BASED_DATASET2 + 10);

		for (int i = 0; i<userBased.size(); i++) {
			userBasedDataset.add(userBased.path(i).findValue("dataset").toString());
		}

		JsonNode userBasedHybrid = RESTfulCalls.getAPI(Constants.URL_SERVER
				+ Constants.URL_FLASK
				+ Constants.GET_TOP_K_USER_BASED_DATASET_HYBRID1 + userId
				+ Constants.GET_TOP_K_USER_BASED_DATASET_HYBRID2 + 10
				+ Constants.GET_TOP_K_USER_BASED_DATASET_HYBRID3 + keyword);

		if (userBasedHybrid == null || userBasedHybrid.has("error")
				|| !userBasedHybrid.isArray()) {
			return ok(recommendationSummary.render(climateServices, dataSetsList, usersList, userBasedDataset, featureBasedDataset, itemBasedDataset, userId, userSimilarList, userBasedDatasetHybrid, "No related topic with '" + keyword + "'!"));
		}

		for (int i = 0; i<userBasedHybrid.size(); i++) {
			userBasedDatasetHybrid.add(userBasedHybrid.path(i).findValue("dataset").toString());
		}


		JsonNode itemBased = RESTfulCalls.getAPI(Constants.URL_SERVER
				+ Constants.URL_FLASK
				+ Constants.GET_TOP_K_ITEM_BASED_DATASET1 + userId
				+ Constants.GET_TOP_K_ITEM_BASED_DATASET2 + 10);

		for (int i = 0; i<itemBased.size(); i++) {
			itemBasedDataset.add(itemBased.path(i).findValue("dataset").toString());
		}

		JsonNode featureBased = RESTfulCalls.getAPI(Constants.URL_SERVER
				+ Constants.URL_FLASK
				+ Constants.GET_TOP_K_FEATURE_BASED_DATASET1 + userId
				+ Constants.GET_TOP_K_FEATURE_BASED_DATASET2 + 10);

		for (int i = 0; i<featureBased.size(); i++) {
			featureBasedDataset.add(featureBased.path(i).findValue("dataset").toString());
		}

		System.out.println("--------------------------");
		System.out.println(userId);
//		JsonNode test = userBased.path(0);
//		System.out.println(test.findValue("dataset"));
		System.out.println(userBasedDataset);
		System.out.println("--------------------------Beeping");

		return ok(recommendationSummary.render(climateServices, dataSetsList, usersList, userBasedDataset, featureBasedDataset, itemBasedDataset, userId, userSimilarList, userBasedDatasetHybrid, keyword));
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
			ClimateService newService = deserializeJsonToClimateService(json);
			climateServices.add(newService);
		}

		return ok(mostRecentlyUsedServices.render(climateServices));
	}

	public static Result replaceFile() {
		File result = request().body().asRaw().asFile();
		System.out.println("result: " + request().body().asRaw().asFile());

		// String content = readFile(result.getName(), StandardCharsets.UTF_8);
		System.out.println("result body: " + result.toString());

		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					result.getAbsolutePath()));
			StringBuilder sb = new StringBuilder();
			line = br.readLine();
			//int count = 0;
			String fileNameLine = "<h2 class=\"text-center\">";
			while (line != null ) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();

//				if (line.length() > 23)
//					System.out.println("pair1" + line.substring(0, 24));
//					System.out.println("pair2" + fileNameLine);
				if (line.length()>= 24 && line.substring(0, 24).equals(fileNameLine))
					break;
				//count++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TEMPOARY SOLUTION : get the fileName from the html page
		System.out.println("original Name" + line);
		String tempName = line.substring(24, line.length() - 5);
		String fileName = "public/html/service"
				+ tempName.substring(0, 1).toUpperCase()
				+ tempName.substring(1) + ".html";
		System.out.println("fileName: " + fileName);

		// replace the page in the frontend Server
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
		// executeReplace(result);

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

	public static ClimateService deserializeJsonToClimateService(JsonNode json) {

		ClimateService oneService = new ClimateService();
		oneService.setName(json.path("name").asText());
		oneService.setPurpose(json.path("purpose").asText());
		// URL here is the dynamic page url
		String name = json.path("name").asText();
		String url = json.path("url").asText();
		// Parse NASA URL
		if (url.contains("/cmac/web") || name.length() == 0 || name == null) {
			oneService.setUrl(url);
		} else {
			String pageUrl = Constants.URL_SERVER
					+ Constants.LOCAL_HOST_PORT + "/assets/html/service"
					+ name.substring(0, 1).toUpperCase()
					+ name.substring(1) + ".html";
			oneService.setUrl(pageUrl);
		}
		// newService.setCreateTime(json.path("createTime").asText());
		oneService.setScenario(json.path("scenario").asText());
		oneService.setVersionNo(json.path("versionNo").asText());
		oneService.setRootServiceId(json.path("rootServiceId").asLong());
		oneService.setImageURL();

		return oneService;
	}

	// Get all climate Services
	public static Result searchClimateServices() {
		return ok(searchClimateService.render(climateServiceForm));
	}


	public static Result getSearchResult(){
		Form<ClimateService> cs = climateServiceForm.bindFromRequest();
		ObjectNode jsonData = Json.newObject();

		String name = "";
		String purpose = "";
		String scenario = "";
		String url = "";
		String versionNo = "";

		try {
			name = cs.field("Climate Service Name").value();
			purpose = cs.field("Purpose").value();
			url = cs.field("Url").value();
			scenario = cs.field("Scenario").value();
			versionNo = cs.field("Version Number").value();

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}

		List<ClimateService> response = queryClimateService(name, purpose, url, scenario, versionNo);
		return ok(climateServiceList.render(response));
	}

	public static List<ClimateService> queryClimateService(String name, String purpose, String url, String scenario, String versionNo) {

		List<ClimateService> climateService = new ArrayList<ClimateService>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode queryJson = mapper.createObjectNode();
		queryJson.put("name", name);
		queryJson.put("purpose", purpose);
		queryJson.put("url", url);
		queryJson.put("scenario", scenario);
		queryJson.put("versionNo", versionNo);

		JsonNode climateServiceNode = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT + Constants.QUERY_CLIMATE_SERVICE, queryJson);
		// parse the json string into object
		for (int i = 0; i < climateServiceNode.size(); i++) {
			JsonNode json = climateServiceNode.path(i);
			ClimateService newClimateService = deserializeJsonToClimateService(json);
			climateService.add(newClimateService);
		}
		return climateService;
	}
}
