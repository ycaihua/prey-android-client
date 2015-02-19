/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions;

public class ActionResult {

	private String result;
	private HttpDataService dataToSend;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public HttpDataService getDataToSend() {
		return dataToSend;
	}

	public void setDataToSend(HttpDataService dataToSend) {
		this.dataToSend = dataToSend;
	}
}
