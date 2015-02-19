/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class PreyEmail {

	public static String getEmail(Context context) {
		if (PreyConfig.getPreyConfig(context).isEclairOrAbove()) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(context, accountManager);
			if (account != null) {
				return account.name;
			}
		}
		return null;
	}

	private static Account getAccount(Context context, AccountManager accountManager) {
		if (PreyConfig.getPreyConfig(context).isEclairOrAbove()) {
			Account[] accounts = accountManager.getAccountsByType("com.google");
			if (accounts.length > 0) {
				return accounts[0];
			}
		}
		return null;
	}
}
