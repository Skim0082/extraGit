/*
 * $Revision$
 *
 * Copyright (C) 2004-20012 Polarion Software
 * All rights reserved.
 * Email: info@polarion.com
 *
 *
 * Copyright (C) 2004-20012 Polarion Software
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Polarion Software.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Polarion Software MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. POLARION SOFTWARE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.polarion.workflow.function.automation.functions;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.polarion.alm.projects.IProjectLifecycleManager;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.workflow.IArguments;
import com.polarion.alm.tracker.workflow.ICallContext;
import com.polarion.alm.tracker.workflow.IFunction;
import com.polarion.workflow.function.automation.functions.CreateProject;
import com.polarion.platform.persistence.spi.DelegatingOption;
import com.polarion.platform.security.ISecurityService;
import com.polarion.platform.service.repository.IRepositoryService;
import com.polarion.subterra.base.location.ILocation;
import com.polarion.subterra.base.location.Location;


/**
 * @wi drivepilot/DP-529 add java source to DP-529
 * @author Stefan Schuck
 * @version ALPHA $Revision$ $Date$
 */
public class CreateProject implements IFunction {

	Logger 	log = Logger.getLogger(CreateProject.class);
	
	IWorkItem 					wi;
	ITrackerService 			trackerService;
	IProjectService 			projectService; 
	IProjectLifecycleManager 	projectLifecycleManager;
	ISecurityService			securityService;

	
	public void execute(ICallContext context, IArguments arguments) {
		
		//Argument fetch (with default values)
		String projectTemplateField 	= arguments.getAsString("projectTemplateField","projectTemplate");
		String projectIDField			= arguments.getAsString("projectIDField","projectID");
		String projectLocationField 	= arguments.getAsString("projectLocationField","projectLocation");
		String defaultLocation			= arguments.getAsString("defaultLocation","/Automation");
		String baseURL 					= arguments.getAsString("baseURL","http://localhost/polarion/#/project/");
		
		init(context);
		
		String projectID = createProjects(projectIDField, projectTemplateField, projectLocationField, defaultLocation);
		
		addProjectLinks(baseURL, projectID);
	}
	
	private void addProjectLinks(String baseURL, String projectID){
		
		wi.addHyperlink(baseURL+""+projectID, null);
		wi.save();
	}
	
	/**
	 * initialization of:<p>
	 * 
	 * 		wi (from context)<br>
	 * 		trackerService (from context)<br>
	 * 		projectService (from trackerService)<br>
	 * 		projectLifecycleManager (from projectService)<br>
	 * 		securityService
	 * 
	 * @param context
	 */
	private void init(ICallContext context) {
		wi 						= context.getWorkItem();
		trackerService			= context.getTrackerService();
		projectService			= trackerService.getProjectsService();
		projectLifecycleManager = projectService.getLifecycleManager();
	}
	

	/**
	 * This Method will create a project based on the given values.
	 * 
	 * @param projectTemplateField
	 * @param projectIDField
	 * @param projectlocationField
	 * @return ID of the project
	 */
	private String createProjects(String projectIDField, String projectTemplateField, String projectLocationField, String defaultLocation) {
		
		String projectID;
		String templateID;
		
		//fetch project ID
		projectID 	= (String) wi.getValue(projectIDField);
		
		//fetch project template
		DelegatingOption tmpDO = (DelegatingOption) wi.getValue(projectTemplateField);
		templateID  = tmpDO.getId();
		
		//create empty map (required for project creation)
		Map<String, String> paramValues = Collections.<String,String>emptyMap();
		
		ILocation location = Location.getLocationWithRepository(IRepositoryService.DEFAULT, defaultLocation);
		location = location.append((String) wi.getValue(projectLocationField));
		location = location.append((String) wi.getValue(projectIDField));

		//Create Project
		projectLifecycleManager.createProject(location, projectID, templateID, paramValues);
		
		return projectID;
	}

}
