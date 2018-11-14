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

import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.workflow.IArguments;
import com.polarion.alm.tracker.workflow.ICallContext;
import com.polarion.alm.tracker.workflow.IFunction;
import com.polarion.workflow.function.automation.functions.AssignAuthor;


/**
 * @author Stefan Schuck
 * @version ALPHA $Revision$ $Date$
 */
public class AssignAuthor implements IFunction {

	Logger 	log = Logger.getLogger(AssignAuthor.class);
	
	IWorkItem 					wi;

	/*
	*	@wi.implements elibrary/EL-209 add source code to work item EL-209
	*
	*
	*/
	public void execute(ICallContext context, IArguments arguments) {

	    init(context);
		wi.addAssignee(wi.getAuthor());
	}
	
	private void init(ICallContext context) {
		wi = context.getWorkItem();
		wi.save();
	}
}
