package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import models.ClimateService;
import models.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
import play.data.*;
import utils.Constants;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

public class Application extends Controller {
	
	public static Form<Login> loginForm = Form
			.form(Login.class);
	
	final static Form<User> userForm = Form
			.form(User.class);
	
	public static class Login {

	    public String email;
	    public String password;
	    
	    public String validate() {
	    	ObjectNode jsonData = Json.newObject();
	    	jsonData.put("email", email);
	    	jsonData.put("password", password);
	    	// POST Climate Service JSON data
	    	JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT 
	    				+ Constants.IS_USER_VALID, jsonData);
	        if (response.get("success") == null) {
	          return "Invalid user or password";
	        }
	        return null;
	    }
	    
	}
	
	public static Result home() {
		return ok(home.render());
	}

	public static Result login() {
	    return ok(login.render(loginForm));
	}
	
	public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.home());
    }
	
	public static Result authenticate() {
	    loginForm = loginForm.bindFromRequest();
	    if (loginForm.hasErrors()) {
	        return badRequest(login.render(loginForm));
	    } else {
	        session().clear();
	        session("email", loginForm.get().email);
	        return redirect(routes.Application.home());
	    }
	}
    
    public static void flashMsg(JsonNode jsonNode){
		Iterator<Entry<String, JsonNode>> it = jsonNode.fields();
		while (it.hasNext()) {
			Entry<String, JsonNode> field = it.next();
			flash(field.getKey(),field.getValue().asText());	
		}
    }
    
    public static Result signup() {
		return ok(signup.render(userForm));
	}
  
    
    public static Result createNewUser(){
    	Form<User> nu = userForm.bindFromRequest();
    	
    	ObjectNode jsonData = Json.newObject();
    	String userName = null;
    	try{
    		userName = nu.field("firstName").value()+" "+(nu.field("middleInitial")).value()
    				+" "+(nu.field("lastName")).value();
    		jsonData.put("userName", userName);
			jsonData.put("firstName", nu.field("firstName").value());
			jsonData.put("middleInitial", nu.field("middleInitial").value());
			jsonData.put("lastName", nu.field("lastName").value());
			jsonData.put("password", nu.field("password").value());
			jsonData.put("affiliation", nu.field("affiliation").value());
			jsonData.put("title", nu.field("title").value());
			jsonData.put("email", nu.field("email").value());
			jsonData.put("mailingAddress", nu.field("mailingAddress").value());
			jsonData.put("phoneNumber", nu.field("phoneNumber").value());
			jsonData.put("faxNumber", nu.field("faxNumber").value());
			jsonData.put("researchFields", nu.field("researchFields").value());
			jsonData.put("highestDegree", nu.field("highestDegree").value());
			
			JsonNode response = RESTfulCalls.postAPI(Constants.URL_HOST + Constants.CMU_BACKEND_PORT 
					+ Constants.ADD_USER, jsonData);

			// flash the response message
			Application.flashMsg(response);
    		
    	}catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.CONVERSIONERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls
					.createResponse(ResponseType.UNKNOWN));
		}
		return redirect(routes.Application.home());
    }
}
