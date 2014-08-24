package de.raysha.clipsync;

import com.esotericsoftware.minlog.Log;

public class GlobalConfig {

	public static final int DEFAULT_TCP_PORT	=	1310;
	
	
	public static int CLIPBOARD_POLL_INTERVALL	=	2000;
	
	public static int CLIPBOARD_MAX_SIZE		=	1024 * 512;	//512 kb
	
	public static String DESTIATION_ADDRESS		=	null;
	
	public static int PORT_TO_USE				=	DEFAULT_TCP_PORT;
	
	public static int TIME_OUT					=	5000;

	/**
	 * null -> autom.; true -> server; false -> client
	 */
	public static Boolean CONNECTION_MODE		=	null;
	
	public static Boolean LOOP					= 	true;
	
	public static int LOG_LEVEL					=	Log.LEVEL_NONE;
}
