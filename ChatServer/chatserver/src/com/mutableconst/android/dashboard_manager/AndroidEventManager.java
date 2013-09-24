package com.mutableconst.android.dashboard_manager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import com.mutableconst.chatserver.gui.MainActivity;
import com.mutableconst.protocol.Protocol;
import com.mutableconst.protocol.Connection;

public class AndroidEventManager {

	private static Activity context;
	
	private static PendingIntent pi;
	private static SmsManager sms;

	public static void setupEnvironment(Activity activity) {
		Connection.startConnection(true);
		Preferences.setupPreferences(activity);
		pi = PendingIntent.getActivity(activity, 0, new Intent(activity, MainActivity.class), 0);
		sms = SmsManager.getDefault();
		context = activity;
	}
	
	public static void handleResponse(Protocol response) {
		Preferences.sendToast("Getting called holy shit");
		if (response.getHeader().equals(Protocol.TEXT_MESSAGE_HEADER)) {
			System.out.println("Handling Recieve Text Message");
			sms.sendTextMessage(response.getPhoneNumber(), null, response.getMessage(), pi, null);
		} else {
			System.out.println("Cant handle this yet O_o");
		}
	}
	
	public static boolean forwardTextToComputer(String phoneNumber, String message) {
		Preferences.sendToast("Forwarding message to computer");
		String jsonString = new Protocol(Protocol.TEXT_MESSAGE_HEADER, Protocol.PHONE, phoneNumber, Protocol.MESSAGE, message).getEncodedJSONString();
		return Connection.addRequest(jsonString);
	}

}
