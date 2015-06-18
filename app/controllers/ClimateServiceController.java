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

import models.ClimateService;
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
		return ok(addAClimateService.render(climateServiceForm));
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
			System.out.println("All climate services format has error!");
		}

		// parse the json string into object
		for (int i = 0; i < climateServicesNode.size(); i++) {
			JsonNode json = climateServicesNode.path(i);
			ClimateService oneService = new ClimateService();
			oneService.setName(json.path("name").asText());
			oneService.setPurpose(json.path("purpose").asText());
			// URL here is the dynamic page url
			String name = json.path("name").asText();
			String pageUrl = Constants.URL_HOST + Constants.LOCAL_HOST_PORT + "/assets/html/service" + 
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

	public static Result addClimateService() {
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
		return redirect(routes.ClimateServiceController.addAClimateService());
	}

	public static Result serviceModels() {
		JsonNode jsonData = request().body().asJson();
		System.out.println("JSON data: " + jsonData);
		String url = jsonData.get("climateServiceCallUrl").toString();
		System.out.println("JPL climate service model call url: " + url);

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

		System.out.println("page string: " + str);
		System.out.println("climate service name: " + name);

		ObjectNode jsonData = Json.newObject();
		jsonData.put("pageString", str);

		// POST Climate Service JSON data to CMU 9020 backend
		// One copy in backend and one copy in frontend
		JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.SAVE_CLIMATE_SERVICE_PAGE, jsonData);

		// save page in front-end
		savePage(str, name, purpose, url);

		// flash the response message
		Application.flashMsg(response);
		return ok("Climate Service Page has been saved succussfully!");
	}

	public static void savePage(String str, String name, String purpose,
			String url) {

		// Remove delete button from preview page
		String result = str
				.replaceAll(
						"<td><button type=\\\\\"button\\\\\" class=\\\\\"btn btn-danger\\\\\" onclick=\\\\\"Javascript:deleteRow\\(this\\)\\\\\">delete</button></td>",
						"");

		result = StringEscapeUtils.unescapeJava(result);

		// remove the first char " and the last char " of result, name and
		// purpose
		result = result.substring(1, result.length() - 1);
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

		result = str11 + name + str12 + purpose + str13 + result + str21
				+ url.substring(1, url.length() - 1) + str22;

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

}
