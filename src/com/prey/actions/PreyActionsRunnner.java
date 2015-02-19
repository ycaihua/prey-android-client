/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions;

import android.content.Context;

 

public class PreyActionsRunnner {
	protected boolean running = false;

	Thread myActionsRunnerThread = null;
	private String cmd;

	public PreyActionsRunnner(String cmd) {
		this.cmd=cmd;
	}

	public void run(Context ctx) {
		this.myActionsRunnerThread = new Thread(new PreyActionsRunner(ctx,cmd));
		this.myActionsRunnerThread.start();

	}
}
