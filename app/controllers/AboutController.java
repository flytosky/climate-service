package controllers;

import java.util.List;

import play.data.Form;
import play.mvc.*;
import views.html.*;


public class AboutController extends Controller {
		
    public static Result aboutUs() {
    	return ok(aboutUs.render());
    }
    
    public static Result aboutProject(){
    	return ok(aboutProject.render());
    }
    
    public static Result tutorial() {
		return ok(tutorial.render());
	}

}