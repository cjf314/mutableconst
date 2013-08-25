package com.mutableconst.protocol;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Singleton class for encoding requests/responses to and from JSON
 * 
 * Basic commands go <COMMAND>:<DATA> For example:
 * { "TYPE":"TEXT_MESSAGE_TYPE" 
 *   "NAME":"Nicholas"
 *   "PHONE":"1-800-999-9999"
 *   "MESSAGE":"Slenderman is in the woods"
 * }
 * 
 * { "TYPE":"CONTACT_REQUEST_TYPE"
 *   "CONTACTS":"Casey Foster|1-262-555-8989,Nicholas|1-994-555-2121" 
 * }
 * 
 */

public class Protocol
{
	private static Protocol protocol = null;

	//JSON Keys
	public final static String TYPE = "TYPE";
	public final static String PHONE = "PHONE";
	public final static String NAME = "NAME";
	public final static String MESSAGE = "MESSAGE";
	public final static String CONTACTS = "CONTACTS";

	//Header Types
	public final static String TEXT_MESSAGE_TYPE = "TEXT_MESSAGE_TYPE";
	public final static String CONTACT_REQUEST_TYPE = "CONTACT_REQUEST_TYPE";
	public final static String PING_TYPE = "PING_TYPE";

	private Protocol()
	{}

	public static Protocol getProtocol()
	{
		if (protocol == null) {
			protocol = new Protocol();
		}
		return protocol;
	}
	
	public String encodePing() {
		JSONObject json = new JSONObject();
		json.put(TYPE, PING_TYPE);
		return json.toString();
	}

	public String encodeSendTextMessage(String phone, String message)
	{
		JSONObject json = new JSONObject();
		//Put  name in here
		json.put(PHONE, phone);
		json.put(MESSAGE, message);
		json.put(TYPE, TEXT_MESSAGE_TYPE);
		return json.toString();
	}

	public String encodeContactRequest()
	{
		return new JSONObject().put(TYPE, CONTACT_REQUEST_TYPE).toString();
	}

	public HashMap<String, String> decodeRawRequest(String jsonString)
	{
		if (jsonString != null) {
			JSONObject json = new JSONObject(jsonString);
			String requestHeader = json.getString(TYPE);
			//System.out.println("Decoding Raw Object: " + requestHeader);
			if (requestHeader.equals(TEXT_MESSAGE_TYPE)) {
				return decodeReceiveTextMessage(json);
			} else if (requestHeader.equals(CONTACT_REQUEST_TYPE)) {
				return decodeContactRequest(json);
			}
		}
		return null;
	}

	private HashMap<String, String> decodeReceiveTextMessage(JSONObject json)
	{
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(MESSAGE, json.getString(MESSAGE));
			//map.put(NAME, json.getString(NAME));
			map.put(TYPE, json.getString(TYPE));
			map.put(PHONE, json.getString(PHONE));
			return map;
		} catch (JSONException e) {
			System.out.println("JSON Error: Unable to decode text message.");
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<String, String> decodeContactRequest(JSONObject json)
	{
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(CONTACTS, json.getString(CONTACTS));
			return map;
		} catch (Exception e) {
			System.out.println("JSON Error: Unable to decode contacts.");
		}
		return null;
	}

}
