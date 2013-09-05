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

	//JSON Keys
	public final static String HEADER = "HEADER";
	public final static String PHONE = "PHONE";
	public final static String NAME = "NAME";
	public final static String MESSAGE = "MESSAGE";
	public final static String CONTACTS = "CONTACTS";

	//Header Types
	public final static String TEXT_MESSAGE_HEADER = "TEXT_MESSAGE_HEADER";
	public final static String CONTACT_REQUEST_HEADER = "CONTACTS_REQUEST_HEADER";

	private JSONObject jsonParser;
	private JSONObject jsonEncoder;

	/*
	 * Used for decoding a JSON String
	 */
	public Protocol(String jsonString)
	{
		jsonParser = new JSONObject(jsonString);
		//TODO check for bad strings, needs to throw some Exception
	}

	/*
	 * Used for encoding a JSON String
	 * keyValues needs to be an even length array of Strings
	 * where the values are Key|Value|Key|Value....
	 */
	public Protocol(String header, String... keyValues)
	{
		jsonEncoder = new JSONObject();
		jsonEncoder.put(HEADER, header);
		if (keyValues.length % 2 == 0) {
			for (int i = 0; i < keyValues.length; i += 2) {
				jsonEncoder.put(keyValues[i], keyValues[i + 1]);
			}
		} else {
			//TODO what the fuck do we do here?
		}
	}

	public String getEncodedJSONString() 
	{
		if (jsonEncoder != null) {
			return jsonEncoder.toString();
		}
		return null;
	}

	public String getHeader()
	{
		return jsonParser.getString(HEADER).trim();
	}

	/*
	 * 
	 */
	public String getName()
	{
		return jsonParser.getString(NAME).trim();
	}

	public String getPhoneNumber()
	{
		return jsonParser.getString(PHONE).trim();
	}

	public String getMessage()
	{
		return jsonParser.getString(MESSAGE).trim();
	}
	
}