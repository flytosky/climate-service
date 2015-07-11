/*
 * Copyright (c) 2013 Carnegie Mellon University Silicon Valley. 
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available
 * under the terms of dual licensing(GPL V2 for Research/Education
 * purposes). GNU Public License v2.0 which accompanies this distribution
 * is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Please contact http://www.cmu.edu/silicon-valley/ if you have any 
 * questions.
 * 
 * */
package controllers;

import models.BugReport;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import utils.RESTfulCalls;
import utils.RESTfulCalls.ResponseType;
import views.html.*;

import java.util.*;


public class BugReportController extends Controller {
	final static Form<BugReport> bugReportForm = Form.form(BugReport.class);

	@play.db.jpa.Transactional
	public static Result reports() {
		return ok(bugReporting.render(bugReportForm));
	}

	@play.db.jpa.Transactional
	public static Result newReport() {
		Form<BugReport> filledForm = bugReportForm.bindFromRequest();

		BugReport report = new BugReport();
		try {
			// Validations
			report.setTitle(filledForm.get().getTitle());
			report.setName(filledForm.get().getName());
			report.setEmail(filledForm.get().getEmail());
			report.setOrganization(filledForm.get().getOrganization());
			report.setDescription(filledForm.get().getDescription());
			report.setSolved(0);

			report.save();
			return redirect(routes.BugReportController.list());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}	
		return ok(bugReporting.render(filledForm));
	}

	@play.db.jpa.Transactional
	public static Result list() {
		BugReport bugReport = new BugReport();
		List<Object[]> list = BugReport.getAll();
		LinkedList<BugReport> bugList = new LinkedList<BugReport>();
		// title VARCHAR(255), organization_name VARCHAR(255), email
		// VARCHAR(255), description
		for (Object[] e : list) {
			BugReport bug = new BugReport();
			bug.setId((Integer)e[0]);
			bug.setTitle(e[1].toString());
			bug.setName(e[2].toString());
			bug.setEmail(e[3].toString());
			bug.setOrganization(e[4].toString());
			bug.setDescription(e[5].toString());
			bug.setSolved((Byte)e[6]);
			bugList.add(bug);
		}
		return ok(bugs.render(bugList));
	}

	/*
	 * public static Result authenticate() { Form<Login> loginForm =
	 * form(Login.class).bindFromRequest(); if(loginForm.hasErrors()) return
	 * badRequest(login.render(loginForm)); else { session("email",
	 * loginForm.get().email); return redirect(
	 * routes.DeviceTypeController.deviceTypes() ); } }
	 */
	
	@play.db.jpa.Transactional
	public static Result deleteReport() {
		DynamicForm df = DynamicForm.form().bindFromRequest();
		
		try {
			int id = Integer.valueOf(df.field("idHolder").value());
		
			// Call the delete() method
			if(BugReport.delete(id)){
				Application.flashMsg(RESTfulCalls.createResponse(ResponseType.SUCCESS));
			}else {
				Application.flashMsg(RESTfulCalls.createResponse(ResponseType.DELETEERROR));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}
		return redirect(routes.BugReportController.list());
	}
	
	@play.db.jpa.Transactional
	public static Result solveReport() {
		DynamicForm df = DynamicForm.form().bindFromRequest();
		
		try {
			int id = Integer.valueOf(df.field("idHolder").value());
		
			// Call the delete() method
			if(BugReport.solve(id)){
				Application.flashMsg(RESTfulCalls.createResponse(ResponseType.SUCCESS));
			}else {
				Application.flashMsg(RESTfulCalls.createResponse(ResponseType.RESOLVEERROR));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Application.flashMsg(RESTfulCalls.createResponse(ResponseType.UNKNOWN));
		}
		return redirect(routes.BugReportController.list());
	}


}