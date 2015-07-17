package controllers;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
import play.data.*;
import utils.Constants;
import utils.RESTfulCalls;
import views.html.*;

public class Application extends Controller {
	
	public static Form<Login> loginForm = Form
			.form(Login.class);
	
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
}
