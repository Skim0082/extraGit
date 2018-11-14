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

import org.apache.log4j.Logger;

import com.polarion.alm.projects.IProjectLifecycleManager;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.workflow.IArguments;
import com.polarion.alm.tracker.workflow.ICallContext;
import com.polarion.alm.tracker.workflow.IFunction;
import com.polarion.core.util.exceptions.UserFriendlyRuntimeException;
import com.polarion.workflow.function.automation.functions.RecalculateFields;
import com.polarion.platform.jobs.GenericJobException;
import com.polarion.platform.security.ISecurityService;


/**
 * @author Stefan Schuck
 * @version ALPHA $Revision$ $Date$
 * @wi elibrary/EL-206 add source code to work item EL-206
 */
public class RecalculateFields implements IFunction {

	Logger 	log = Logger.getLogger(RecalculateFields.class);
	
	IWorkItem 					wi;
	ITrackerService 			trackerService;
	IProjectService 			projectService; 
	IProjectLifecycleManager 	projectLifecycleManager;
	ISecurityService			securityService;

	
	public void execute(ICallContext context, IArguments arguments) {
		
		init(context);
		
		try {
            wi.getDataSvc().getCalculatedFieldsService().recalculate(wi.getProject().getContextId());
        } catch (GenericJobException e) {
            throw new UserFriendlyRuntimeException("Could not refresh Calculated fields");
        }
		wi.save();
	}
	
	private void init(ICallContext context) {
		wi = context.getWorkItem();
	}
	
	

}
