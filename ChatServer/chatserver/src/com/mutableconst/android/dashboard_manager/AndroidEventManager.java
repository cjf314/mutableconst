package com.mutableconst.android.dashboard_manager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import com.mutableconst.chatserver.gui.MainActivity;
import com.mutableconst.protocol.Protocol;

public class AndroidEventManager {

	private static Activity context;
	
	private static PendingIntent pi;
	private static SmsManager sms;

	public static void setupEnvironment(Activity activity) {
		AndroidConnection.startAndroidConnection();
		Preferences.setupPreferences(activity);
		pi = PendingIntent.getActivity(activity, 0, new Intent(activity, MainActivity.class), 0);
		sms = SmsManager.getDefault();
		context = activity;
	}
	
	public static void handleResponse(Protocol response) {
		if (response.getHeader().equals(Protocol.TEXT_MESSAGE_HEADER)) {
			System.out.println("Handling Recieve Text Message");
			sms.sendTextMessage(response.getPhoneNumber(), null, response.getMessage(), pi, null);
		} else {
			System.out.println("Cant handle this yet O_o");
		}
	}
	
	public static boolean forwardTextToComputer(String phoneNumber, String message) {
		String jsonString = new Protocol(Protocol.TEXT_MESSAGE_HEADER, Protocol.PHONE, phoneNumber, Protocol.MESSAGE, message).getEncodedJSONString();
		return AndroidConnection.addRequest(jsonString);
	}

}
