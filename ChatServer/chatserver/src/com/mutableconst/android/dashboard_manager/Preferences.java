package com.mutableconst.android.dashboard_manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Preferences {

	private static Activity context;
	
	private static SharedPreferences sharedPreferences;
	private static SharedPreferences.Editor editor;

	// Keys
	public static final String PREFERENCE_KEY = "PREFERENCES";
	
	public static final String SERVER_IP_ADDRESS = "IP_ADDRESS";

	private static boolean setup = false;

	public static void setupPreferences(Activity activity) {
		if (!setup && activity != null) {
			context = activity;
			sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, 0);
			setup = true;
		} else {
			System.out.println("Error setting up preferences");
		}
	}

	public static String getPreference(String key) {
		if (setup) {
			return sharedPreferences.getString(key, null);
		} else {
			return null;
		}
	}

	public static void setPreference(String key, String value) {
		if (setup) {
			if (key != null && value != null) {
				editor = sharedPreferences.edit();
				editor.putString(key, value);
				editor.commit();
			}
		}
	}
	
	public static void sendToast(final String message) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}

}
