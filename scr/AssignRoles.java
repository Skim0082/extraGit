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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.polarion.alm.projects.IProjectLifecycleManager;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.workflow.IArguments;
import com.polarion.alm.tracker.workflow.ICallContext;
import com.polarion.alm.tracker.workflow.IFunction;
import com.polarion.workflow.function.automation.functions.AssignRoles;
import com.polarion.platform.persistence.spi.EnumOption;
import com.polarion.platform.security.ISecurityService;
import com.polarion.subterra.base.data.identification.IContextId;
import com.polarion.platform.core.PlatformContext;


/**
 * @author Stefan Schuck
 * @version ALPHA $Revision$ $Date$
 */
public class AssignRoles implements IFunction {

	Logger 	log = Logger.getLogger(AssignRoles.class);
	
	IWorkItem 					wi;
	ITrackerService 			trackerService;
	IProjectService 			projectService; 
	IProjectLifecycleManager 	projectLifecycleManager;
	ISecurityService			securityService;
	
	
	public void execute(ICallContext context, IArguments arguments) {
		
		//Argument fetch (with default values)
		String usersField 		= arguments.getAsString("usersField","users");
		String rolesField		= arguments.getAsString("rolesField","roles");
		String projectIDField	= arguments.getAsString("projectIDField","projectID");
		
		init(context);
		
		assignRoles(projectIDField, usersField, rolesField);
	}


	private void assignRoles(String projectIDField, String usersField, String rolesField) {

		String projectID 	= (String) wi.getValue(projectIDField);
		IContextId contID 	= projectService.getProject(projectID).getContextId();
		
		List <EnumOption> usersList = (List<EnumOption>) wi.getValue(usersField);
		Iterator<EnumOption> users = usersList.iterator();
		
		while(users.hasNext()){
			String userID = users.next().getId(); 
			
			List <EnumOption> rolesList = (List<EnumOption>) wi.getValue(rolesField);
			Iterator<EnumOption> roles = rolesList.iterator(); 
			
			while(roles.hasNext()){
				String role = roles.next().getId();
				securityService.addContextRoleToUser(userID, role , contID);
			}
		}
		
		
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
		trackerService 			= context.getTrackerService();
		projectService 			= trackerService.getProjectsService();
		projectLifecycleManager = projectService.getLifecycleManager();
		securityService 		= (ISecurityService)PlatformContext.getPlatform().
								  lookupService(ISecurityService.class);
	}
	
	
	/**
	 * The method fetches all participants from a given multi line textfield and 
	 * fills a participants list for further actions 
	 * 
	 * @param participants	List which is filled during execution
	 * @param participantsField  Fieldname on wi to fetch participants
	 */

}
