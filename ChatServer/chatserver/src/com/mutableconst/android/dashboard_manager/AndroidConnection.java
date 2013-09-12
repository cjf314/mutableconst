package com.mutableconst.android.dashboard_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.mutableconst.chatserver.gui.MainActivity;
import com.mutableconst.protocol.Protocol;

public class AndroidConnection {

	private final static char NEW_LINE = '\n';
	private final static String PING_STRING = "";
	private final static int MAX_PING_COUNTER = 30;
	private final static int PORT = 7767;

	private static int pingCounter = MAX_PING_COUNTER;

	private static boolean started = false;

	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;

	private static String serverAddress = "192.168.1.132";

	private final static StringBuilder responseBuilder = new StringBuilder();
	private final static ConcurrentLinkedQueue<String> requests = new ConcurrentLinkedQueue<String>();

	public static void startAndroidConnection() {
		if (!started) {
			started = true;
			new AndroidConnection();
		}
	}

	private AndroidConnection() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
						setupSocket();
						processSocketOutput();
						processSocketInput();
						pingSocket();
					} catch (SocketException e) {
						Preferences.sendToast("Socket is null: " + e.toString());
						resetSocket();
					} catch (UnknownHostException e) {
						Preferences.sendToast(e.toString());
						e.printStackTrace();
					} catch (IOException e) {
						Preferences.sendToast(e.toString());
						e.printStackTrace();
					} catch (InterruptedException e) {
						Preferences.sendToast(e.toString());
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/*
	 * If the socket is currently null, block until a succesful connection is
	 * made. After a connection, sets up the input and output streams for the
	 * socket.
	 */
	private static void setupSocket() throws UnknownHostException, IOException {
		if (socket == null) {
			Preferences.sendToast("Creating a New Socket");
			serverAddress = Preferences.getPreference(Preferences.SERVER_IP_ADDRESS);
			socket = new Socket(serverAddress, PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}
	}

	private static void processSocketOutput() {
		if (!requests.isEmpty() && out != null) {
			Preferences.sendToast("Outputing Data: " + requests.peek());
			out.println(requests.poll());
		}
	}

	private static void processSocketInput() throws IOException {
		while (in != null && in.ready()) {
			char nextChar = (char) in.read();
			responseBuilder.append(nextChar);
			if (nextChar == NEW_LINE) {
				String rawResponse = responseBuilder.toString().trim();
				System.out.println("Incoming request: " + rawResponse);
				responseBuilder.setLength(0);
				if (rawResponse.length() > 0) {
					AndroidEventManager.handleResponse(new Protocol(rawResponse));
				}
			}
		}
	}

	private static void pingSocket() throws SocketException {
		if (pingCounter-- == 0) {
			pingCounter = MAX_PING_COUNTER;
			out.println(PING_STRING);
			if (out.checkError()) {
				throw new SocketException("Ping Failed");
			}
		}
	}

	private static void resetSocket() {
		socket = null;
		in = null;
		out = null;
	}

	public static boolean addRequest(String request) {
		if (requests != null) {
			requests.add(request);
			return true;
		} else {
			return false;
		}
	}
}
