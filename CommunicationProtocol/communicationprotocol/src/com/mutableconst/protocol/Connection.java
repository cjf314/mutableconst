package com.mutableconst.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {

	private final static int PORT = 7767;
	private final static char NEW_LINE = '\n';
	private final static String PING_STRING = "";
	private final static int MAX_PING_COUNTER = 30;
	private final static String ANDROID_EVENT_MANAGER_CLASS = "com.mutableconst.android.dashboard_manager.AndroidEventManager";
	private final static String DESKTOP_EVENT_MANAGER_CLASS = "com.mutableconst.dashboard_manager.EventManager";

	private static int pingCounter = MAX_PING_COUNTER;
	private static boolean running = false;
	private static boolean isServer;
	private static Socket socket;
	private static ServerSocket serverSocket;
	private static BufferedReader in;
	private static PrintWriter out;

	private static String serverAddress = "192.168.1.131";

	private final static StringBuilder responseBuilder = new StringBuilder();
	private final static ConcurrentLinkedQueue<String> requests = new ConcurrentLinkedQueue<String>();

	private static Thread connectionThread;

	private static int extraSleep;

	private static Class<?> eventManager;
	private static Method handleResponseMethod;
	private static Class<?>[] handleResponseParameter = new Class[] { Protocol.class };

	/*
	 * If a connection currently isn't running, setup all needed values for the
	 * Connection and start it up.
	 */
	public static void startConnection(boolean server) {
		if (!running) {
			running = true;
			isServer = server;
			/*
			 * Setup the reflection EventManager and the reflection method
			 * handleResponse to use on this Connection.
			 */
			try {
				if (isServer) {
					eventManager = Class.forName(ANDROID_EVENT_MANAGER_CLASS);
				} else {
					eventManager = Class.forName(DESKTOP_EVENT_MANAGER_CLASS);
				}
				handleResponseMethod = eventManager.getDeclaredMethod("handleResponse", handleResponseParameter);
			} catch (Exception e) {
				System.out.println("Connection could not find an EventManager or Method to use. Aborting Connection");
				e.printStackTrace();
			}
			manageConnection();
		}
	}

	/*
	 * Sends a trigger to the current Connection Thread that it needs to stop.
	 */
	public static void stopConnection() {
		running = false;
	}

	/*
	 * Stops the current Connection and starts it up again
	 */
	public static void restartConnection() {
		if (running) {
			stopConnection();
			startConnection(isServer);
		}
	}

	/*
	 * Add an outgoing request to the socket. Requests is an interally
	 * synchronized queue.
	 */
	public static boolean addRequest(String request) {
		if (running) {
			requests.add(request);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Spawn's a new thread to handle the connection state and IO in. Any
	 * attempts at closing the socket will be repeatedly recovered from.
	 */
	private static void manageConnection() {
		connectionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						Thread.sleep(500 + extraSleep);
						extraSleep = 0;
						setupSocket();
						processSocketOutput();
						processSocketInput();
						pingSocket();
					} catch (SocketException e) {
						resetSocket();
					} catch (Exception e) {
						System.out.println("Exception on Socket: " + e.toString());
						e.printStackTrace();
						extraSleep = 5000; // To slow down repeated attempts on the socket.
					}
				}
			}
		});
		connectionThread.start();
	}

	/*
	 * If the socket is currently null, block until a succesful connection is
	 * made. After a connection, sets up the input and output streams for the
	 * socket.
	 */
	private static void setupSocket() throws UnknownHostException, IOException {
		if (socket == null) {
			if (isServer) {
				serverSocket = new ServerSocket(PORT);
				socket = serverSocket.accept();
			} else {
				socket = new Socket(serverAddress, PORT);
			}
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("Connection Made!");
		}
	}

	/*
	 * Checks the requests ConcurrentLinkedQueued for any requests that have
	 * been added. While the requests queue has requests pending they will be
	 * written to the socket. If the socket's error state ever switches to true,
	 * this method will throw a SocketException
	 */
	private static void processSocketOutput() throws SocketException {
		while (!requests.isEmpty() && out != null) {
			out.println(requests.peek());
			if (out.checkError()) {
				throw new SocketException("Output failed!");
			} else {
				requests.poll();
			}
		}
	}

	/*
	 * Reads the character stream from the socket while their are incoming
	 * characters and the socket doesn't block the current thread's execution.
	 * Passes completed requests to the appropriate EventManager.
	 */
	private static void processSocketInput() throws IOException {
		while(in.ready() && in != null) {
			char nextChar = (char) in.read();
			responseBuilder.append(nextChar);
			if (nextChar == NEW_LINE) {
				String rawResponse = responseBuilder.toString().trim();
				System.out.println("Incoming request: " + rawResponse);
				responseBuilder.setLength(0);
				if (rawResponse.length() > 0) {
					reflectEvent(new Protocol(rawResponse));
				}
			}
		}
	}

	/*
	 * Dynamically call either the Desktop or Android EventManager
	 * handleResponse method with requests incoming on the socket. Reflection
	 * was used to prevent having to compile both projects into each other.
	 */
	private static void reflectEvent(Protocol protocol) {
		try {
			handleResponseMethod.invoke(null, (Object) protocol);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Send an empty string on the socket. Throws a SocketException if the
	 * socket error state is set after the ping.
	 */
	private static void pingSocket() throws SocketException {
		if (pingCounter-- == 0) {
			pingCounter = MAX_PING_COUNTER;
			out.println(PING_STRING);
			if (out.checkError()) {
				throw new SocketException("Ping Failed");
			}
		}
	}

	/*
	 * Force closes the socket, the socket's input, and the socket's output
	 * streams.
	 */
	private static void resetSocket() {
		socket = null;
		in = null;
		out = null;
	}

}
