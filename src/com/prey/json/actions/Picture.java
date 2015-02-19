/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;

import com.prey.PreyLogger;
import com.prey.actions.ActionResult;
import com.prey.actions.HttpDataService;
import com.prey.json.JsonAction;
import com.prey.json.actions.picture.PictureUtil;

public class Picture extends JsonAction {

	public List<HttpDataService> report(Context ctx, List<ActionResult> list, JSONObject parameters) {
		List<HttpDataService> listResult = super.report(ctx, list, parameters);
		PreyLogger.d("Ejecuting Picture reports. DONE!");
		return listResult;
	}

	public List<HttpDataService> get(Context ctx, List<ActionResult> list, JSONObject parameters) {
		List<HttpDataService> listResult = super.get(ctx, list, parameters);
		return listResult;
	}

	public HttpDataService run(Context ctx, List<ActionResult> list, JSONObject parameters) {
		return PictureUtil.getPicture(ctx);
	}

	public HttpDataService start(Context ctx, List<ActionResult> list, JSONObject parameters) {
		return PictureUtil.getPicture(ctx);
	}

	public List<HttpDataService> sms(Context ctx, List<ActionResult> list, JSONObject parameters) {
		return null;
	}

}
