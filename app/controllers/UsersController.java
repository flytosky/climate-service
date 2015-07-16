

package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import utils.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UsersController extends Controller {
	
	final static Form<User> userForm = Form.form(User.class);

//	public static Result registrationForm() {
//		return ok(userRegistrationForm.render(userForm));
//	}
//	
//	public static Result addUserForm(){
//		return ok(addUserForm.render(userForm));
//	}
//	
//	public static Result updateForm() {
//		return ok(updateUserForm.render(userForm));
//	}
//	
//	public static Result deleteForm(){
//		return ok(deleteUser.render(userForm));
//	}
//	
//	public static Result adminPage(){
//		return ok(adminPage.render(User.all()));
//	}
	
	public static Result searchUser(){
		return ok(searchUser.render(userForm));
	}

	public static Result getAllUsers(){
		List<User> usersList = new ArrayList<User>();
		JsonNode usersNode = RESTfulCalls.getAPI(Constants.URL_HOST
				+ Constants.CMU_BACKEND_PORT
				+ Constants.GET_ALL_USERS);
		
		// if no value is returned or error or is not json array
		if (usersNode == null || usersNode.has("error")
				|| !usersNode.isArray()) {
			return ok(allUsers.render(usersList, userForm));
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

		return ok(allUsers.render(usersList, userForm));
	}
	
}
