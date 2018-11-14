/*
 * $Revision$
 *
 * Copyright (C) 2004-2013 Polarion Software
 * All rights reserved.
 * Email: info@polarion.com
 *
 *
 * Copyright (C) 2004-2013 Polarion Software
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.polarion.alm.projects.IProjectLifecycleManager;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.projects.model.IUser;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.workflow.IArguments;
import com.polarion.alm.tracker.workflow.ICallContext;
import com.polarion.alm.tracker.workflow.IFunction;
import com.polarion.core.util.types.Text;
import com.polarion.workflow.function.automation.functions.CreateUsers;
import com.polarion.workflow.function.automation.helper.Participant;
import com.polarion.platform.security.ISecurityService;
import com.polarion.platform.core.PlatformContext;


/**
 * @author Stefan Schuck
 * @version ALPHA $Revision$ $Date$
 */
public class CreateUsers implements IFunction {

	Logger 	log = Logger.getLogger(CreateUsers.class);
	
	IWorkItem 					wi;
	ITrackerService 			trackerService;
	IProjectService 			projectService; 
	IProjectLifecycleManager 	projectLifecycleManager;
	ISecurityService			securityService;
	
	//Mail Server
	String smtpHostname			= System.getProperty("announcer.smtp.host");
	String smtpUser				= System.getProperty("announcer.smtp.user");
	String smtpPassword			= System.getProperty("announcer.smtp.password");
	String smtpAuth				= System.getProperty("announcer.smtp.auth");
	String smtpPort				= System.getProperty("announcer.smtp.port");
	String userNameSuffix       = "";
	String userIDSuffix         = "";
	
	public void execute(ICallContext context, IArguments arguments) {
		
		
		
		String userNameField 		= arguments.getAsString("newUserNameField","newUserName");
		String userMailField 		= arguments.getAsString("newUserMailField","newUserMail");
		String multiUserField       = arguments.getAsString("multiUserField","none");
		String userNameSuffix       = arguments.getAsString("userNameSuffix"," (Student)");
		String userIDSuffix         = arguments.getAsString("userIDSuffix","_s");
		
		this.userNameSuffix = userNameSuffix;
		this.userIDSuffix   = userIDSuffix;
		
		init(context);
		
		
		if (multiUserField.equals("none")){
		    createUsers(userNameField, userMailField);
		}
		else{
		    List <Participant> participants = new ArrayList<Participant>();
		    analyzeParticipants(participants, multiUserField);
		    createUsers(participants);
		}
	}

	/**
	 * The Method will create one user
	 * @wi.implements elibrary/EL-210 add source code to work item EL-210
	 *  add comment here
	 * @param userName
	 * @param email
	 */
	private void createUsers(String userNameField, String emailField) {
		
		Participant participant = new Participant();
		
		participant.setName((String) wi.getValue(userNameField));
		participant.setEmail((String) wi.getValue(emailField));
		
		String userName = participant.getName();
		
		userName = userName.replaceAll("ä", "ae");
		userName = userName.replaceAll("ö", "oe");
		userName = userName.replaceAll("ü", "ue");
		
		String[] fullName = userName.split("( )+");
		
		String id = "";
		
		if (fullName.length < 2){
		    id = fullName[0];
		    participant.setInitials(fullName[0].substring(0, 2));
		}else 
		{
    		fullName[1] = fullName[1].replaceAll("( )*", "");
    		fullName[0] = fullName[0].replaceFirst("( )*", "");
    		id = fullName[1]+fullName[0].substring(0, 1);
    		participant.setInitials(fullName[0].substring(0, 1)+fullName[1].substring(0, 1));
		}
		participant.setId(id.toLowerCase());
		
		
		IUser user = trackerService.getTrackerUser(participant.getId());

		if (user.isUnresolvable()){
			
			user = projectService.createUser(participant.getId());
			
			user.setName(participant.getName());
			user.setEmail(participant.getEmail());
			user.setInitials(participant.getInitials().toUpperCase());
		
			securityService.createUser(participant.getId().toLowerCase(), participant.getPassword());
			
		}else{
			user.setName(participant.getName());
			user.setEmail(participant.getEmail());
			user.setInitials(participant.getInitials().toUpperCase());
			
			securityService.changePassword(
					participant.getId().toLowerCase(), 
					participant.getPassword());
		}
		
		user.save();
		securityService.addGlobalRoleToUser (user.getId(), "user");
	}
	
	/**
	 * The Method will create one user per participant or update existing users with the correct information
	 * 
	 * @param participants
	 */
	private void createUsers(List<Participant> participants) {
		//create users START
		Iterator<Participant> iParticipants = participants.iterator();
		
		while (iParticipants.hasNext()){
			
			Participant currentParticipant = iParticipants.next();
			IUser user = trackerService.getTrackerUser(currentParticipant.getId());

			if (user.isUnresolvable()){
				
				user = projectService.createUser(currentParticipant.getId());
				
				user.setName(currentParticipant.getName());
				user.setEmail(currentParticipant.getEmail());
				user.setInitials(currentParticipant.getInitials().toUpperCase());
			
				securityService.createUser(currentParticipant.getId().toLowerCase(), currentParticipant.getPassword());
				
			}else{
				user.setName(currentParticipant.getName());
				user.setEmail(currentParticipant.getEmail());
				user.setInitials(currentParticipant.getInitials().toUpperCase());
				
				securityService.changePassword(
						currentParticipant.getId().toLowerCase(), 
						currentParticipant.getPassword());
			}
			
			user.save();
			securityService.addGlobalRoleToUser (user.getId(), "user");
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
	private void analyzeParticipants(List<Participant> participants, String participantsField) {
		
		String users;
		Text tmpText;
		tmpText = (Text) wi.getValue(participantsField);
		users = tmpText.getContent();
		
		Iterator<String> usersList = Arrays.asList(users.split("\n")).iterator(); 
		
		while (usersList.hasNext()){
			
			Participant participant = new Participant();
			
			// the line should have the format:
			// "email@domain.com;lastname, firstname"
			
			String currentUser = usersList.next();
			String[] userInfos = currentUser.split(";");
			
			participant.setEmail(userInfos[0]);
			participant.setName(participant.getEmail());
			
			
			if (userInfos.length > 1){
				
				userInfos[1] = userInfos[1].replaceAll("ä", "ae");
				userInfos[1] = userInfos[1].replaceAll("ö", "oe");
				userInfos[1] = userInfos[1].replaceAll("ü", "ue");
				
				String[] fullName = userInfos[1].split(",");
				//INFO:  fullName = [lastname, firstname]
				
				try{
					fullName[0] = fullName[0].replaceAll("( )*", "");
					fullName[1] = fullName[1].replaceFirst("( )*", "");
					participant.setName(fullName[1] + " " + fullName[0] + userNameSuffix);
					String id = fullName[0]+fullName[1].substring(0, 1) + userIDSuffix;
					participant.setId(id.toLowerCase());
					participant.setInitials(fullName[1].substring(0, 1)+fullName[0].substring(0, 1));
				} catch (NullPointerException npw){
					participant.setName(fullName[0]);
					participant.setId(fullName[0].toLowerCase());
					participant.setInitials("X");
				}
				participants.add(participant);
			}
		}
	}

}
