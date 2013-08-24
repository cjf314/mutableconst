package com.mutableconst.dashboard_manager;

import com.mutableconst.chatclient.gui.BuddyListWindow;
import com.mutableconst.chatclient.gui.SystemTrayInterface;
import com.mutableconst.chatclient.gui.TextWindow;
import com.mutableconst.models.Buddy;
import com.mutableconst.protocol.Protocol;

import java.util.HashMap;

import javax.swing.JFrame;

public class EventManager {

	private static EventManager reference;
	private HashMap<Buddy, TextWindow> windows;

	public static void main(String[] args) {
		BuddyListWindow.focusBuddyListWindow();
		Preferences.loadPreferences();
		new SystemTrayInterface();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Preferences.savePreferences();
			}
		});
		//Protocol.getProtocol().test();
	}

	private EventManager() {
		windows = new HashMap<Buddy, TextWindow>();
	}

	public static EventManager getEventManager() {
		if (reference == null) {
			reference = new EventManager();
		}
		return reference;
	}

	public void launchTextWindow(Buddy buddy) {
		TextWindow window = windows.get(buddy);
		if (window == null) {
			windows.put(buddy, new TextWindow(buddy));
		} else {
			window.toFront();
			window.setExtendedState(JFrame.NORMAL);
			window.repaint();
		}
	}

	public void textWindowClose(Buddy buddyObject) {
		if (windows.containsKey(buddyObject)) {
			windows.remove(buddyObject);
		}
	}

	public void exit() {
		System.exit(0);
	}

	public void focusBuddyListWindow() {
		BuddyListWindow.focusBuddyListWindow();
	}

	public void sendTextMessage(Buddy buddy, String message) {
		String jsonString = Protocol.getProtocol().encodeSendTextMessage(buddy.getPhoneNumber(), message);
		System.out.println(jsonString);
	}

}
