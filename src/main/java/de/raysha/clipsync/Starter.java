package de.raysha.clipsync;

import java.io.IOException;
import java.util.Arrays;

import com.esotericsoftware.minlog.Log;

import de.raysha.clipsync.clipboard.ClipboardManager;
import de.raysha.clipsync.communication.CSClient;
import de.raysha.clipsync.communication.CSServer;
import de.raysha.clipsync.communication.Synchronizer;

//TODO: Timeout bei zu langer ruhe..
//TODO: Clipboard-Exception bekommen (auch so eine art timeout)
public class Starter {
	public enum Option {
		PORT("-p", "--port", GlobalConfig.PORT_TO_USE, "\tDer zu verwendende Port."),
		TIMEOUT("-t", "--timeout", GlobalConfig.TIME_OUT, "\tTimout (in Milisekunden) f\u00fcr den Verbindungsversuch zum Server."),
		MODE("-m", "--mode", GlobalConfig.CONNECTION_MODE != null ? (GlobalConfig.CONNECTION_MODE ? "server" : "client") : "auto", 
							"\tAls was soll ClipSync gestartet werden: \n" +
							"\t\t\t\tServer: Startet einen Server - Verbindung(en) m\u00f6glich \n" + 
							"\t\t\t\tDie Zieladresse ist nicht n\u00f6tig bzw. wird ignoriert.\n" +
							"\t\t\t\tClient: Verbindet sich zu einem bestehenden Server\n" +
							"\t\t\t\tAuto: Versucht sich zuerst mit einem Server zu verbinden.\n" +
							"\t\t\t\tSchlägt dies fehl, wird ein Server gestartet."),
		LOOP("-lo", "--loop", GlobalConfig.LOOP, "\tSoll bei Verbindungsabbruch eine neue Verbindung aufgebaut werden?"),
		INTERVAL("-i", "--interval", GlobalConfig.CLIPBOARD_POLL_INTERVALL, "\tGibt den Intevall (in Milisekunden) an, in dem der Inhalt der\n" +
									"\t\t\t\tZwischenablage gepr\u00fcft wird."),
		MAX_CLIPBOARD_SIZE("-c", "--clipboard-size", GlobalConfig.CLIPBOARD_MAX_SIZE, "Die maximale Gr\u00f6\u00dfe (in Bytes) die der Inhalt der Zwichenablage sein \n" + 
									"\t\t\t\tdarf. Wird diese Gr\u00f6\u00dfe \u00fcberschritten, wird nur ein Teil \u00fcbertragen!"),
		LOG("-l", "--log", GlobalConfig.LOG_LEVEL, "\t\tSchaltet den Loglevel um:\n" +
							"\t\t\t\t\tNONE(6): Keine Logausgaben\n" +
							"\t\t\t\t\tTRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5)");
		
		private final String shortParam;
		private final String longParam;
		private final String description;
		private final Object defaultValue;
		
		private Option(String shortParam, String longParam, Object defaultValue, String desc){
			if(shortParam == null || longParam == null || desc == null)
				throw new RuntimeException("The parameters are mandatory!");
			
			this.shortParam = shortParam;
			this.longParam = longParam;
			this.description = desc;
			this.defaultValue = defaultValue;
		}
		
		public String getShortParam() {
			return shortParam;
		}
		public String getLongParam() {
			return longParam;
		}
		public Object getDefaultValue() {
			return defaultValue;
		}
		public String getDescription() {
			return description;
		}
		
		public static Option getOptionByShortParam(String param){
			for(Option o : values()){
				if(o.getShortParam().equals(param)) return o;
			}
			
			return null;
		}
		
		public static Option getOptionByLongParam(String param){
			for(Option o : values()){
				if(o.getLongParam().equals(param)) return o;
			}
			
			return null;
		}

		@Override
		public String toString() {
			return shortParam + ", " + longParam + " [" + defaultValue + "]\t" + description;
		}
	}
	
	
	static void printHelp(){
		System.out.println("Aufruf: ClipSync [ZielAdresse] [OPTION]");
		System.out.println("");
		for(Option o : Option.values()){
			System.out.println(o);
		}
	}
	
	static boolean applyOptions(String[] args){
		int startIndex = 1;
		
		if(	args == null || args.length == 0 || args[0].startsWith("-")) 
			startIndex = 0;
		else
			GlobalConfig.DESTIATION_ADDRESS = args[0];
		
		for(int i=startIndex; i < args.length; i += 2){
			String curArg = args[i];
			String nextArg = i + 1 < args.length ? args[i+1] : null;
			
			Option o = null;
			if(curArg.startsWith("--")){
				o = Option.getOptionByLongParam(curArg);
			}else if(curArg.startsWith("-")){
				o = Option.getOptionByShortParam(curArg);
			}else return false;
			
			if(nextArg == null || nextArg.startsWith("-"))
				return false;
			
			switch(o){
			case INTERVAL:
				GlobalConfig.CLIPBOARD_POLL_INTERVALL = Integer.parseInt(nextArg);
				break;
			case PORT:
				GlobalConfig.PORT_TO_USE = Integer.parseInt(nextArg);
				if(GlobalConfig.PORT_TO_USE <= 0 || GlobalConfig.PORT_TO_USE > 0xFFFF) return false;
				break;
			case TIMEOUT:
				GlobalConfig.TIME_OUT = Integer.parseInt(nextArg);
				break;
			case MODE:
				if("s".equalsIgnoreCase(nextArg) || "server".equalsIgnoreCase(nextArg))
					GlobalConfig.CONNECTION_MODE = true;
				else if("c".equalsIgnoreCase(nextArg) || "client".equalsIgnoreCase(nextArg))
					GlobalConfig.CONNECTION_MODE = false;
				else return false;
				
				break;
			case MAX_CLIPBOARD_SIZE:
				GlobalConfig.CLIPBOARD_MAX_SIZE = Integer.parseInt(nextArg);
			case LOOP:
				GlobalConfig.LOOP = Boolean.valueOf(nextArg);
				
				break;
			case LOG:
				if(nextArg.equalsIgnoreCase("NONE")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_NONE;
				}else if(nextArg.equalsIgnoreCase("DEBUG")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_DEBUG;
				}else if(nextArg.equalsIgnoreCase("TRACE")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_TRACE;
				}else if(nextArg.equalsIgnoreCase("WARN")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_WARN;
				}else if(nextArg.equalsIgnoreCase("ERROR")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_ERROR;
				}else if(nextArg.equalsIgnoreCase("INFO")){
					GlobalConfig.LOG_LEVEL = Log.LEVEL_INFO;
				}else return false;
				
				break;
			default: return false;
			}
		}
		
		if(	GlobalConfig.DESTIATION_ADDRESS == null && 
			(GlobalConfig.CONNECTION_MODE == null || !GlobalConfig.CONNECTION_MODE)){
			
			//kein Server modus aber keine Addresse angegeben
			return false;
		}
		
		return true;
	}
	
	static CSClient establishClient(){
		try {
			Log.info("Starte Client (" + GlobalConfig.DESTIATION_ADDRESS + ": " + GlobalConfig.PORT_TO_USE + ").");
			return new CSClient(
					GlobalConfig.DESTIATION_ADDRESS,
					GlobalConfig.PORT_TO_USE,
					GlobalConfig.TIME_OUT);
		} catch (IOException e) {
			Log.error("Konnte keine Verbindung zu Server aufbauen.", e);
		}
		
		return null;
	}
	
	static CSServer establishServer(){
		try {
			Log.info("Starte Server (Port: " + GlobalConfig.PORT_TO_USE + ").");
			return new CSServer(GlobalConfig.PORT_TO_USE);
		} catch (IOException e) {
			Log.error("Server konnte nicht gestartet werden!", e);
		}
		
		return null;
	}
	
	static void startCommunication(){
		Synchronizer syncer = null;
		
		if(GlobalConfig.CONNECTION_MODE == null){
			//ersteinmal versuchen sich zu verbinden
			syncer = establishClient();
			
			if(syncer == null){
				syncer = establishServer();
			}
		}else if(GlobalConfig.CONNECTION_MODE){
			syncer = establishServer();
		}else{
			syncer = establishClient();
		}
		
		if(syncer == null){
			return;
		}
		
		ClipboardManager.initialise();
		
		if(syncer instanceof CSClient){
			try {
				((CSClient)syncer).getClient().getUpdateThread().join();
			} catch (InterruptedException e) {
				Log.info("Verbindung wurde unterbrochen.", e);
			}
		}else if(syncer instanceof CSServer){
			try {
				((CSServer)syncer).getServer().getUpdateThread().join();
			} catch (InterruptedException e) {
				Log.info("Verbindung wurde unterbrochen.", e);
			}
		}
	}
	
	public static void main(String[] args) {
		if(!applyOptions(args)){
			printHelp();
			System.exit(1);
		}
		
		Log.set(GlobalConfig.LOG_LEVEL);
		Log.debug("Anwendung gestartet: " + Arrays.asList(args));
		
		//fals verbindung unterbrochen versuchen standhaft zu bleiben!
		do{
			startCommunication();
			if(GlobalConfig.LOOP){
				Log.info("Neuer Verbindungsversuch wird in 10s gestartet...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) { /* nichts tun */ }
			}
		}while(GlobalConfig.LOOP);
	}
}
