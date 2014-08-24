package de.raysha.clipsync.bean;

import java.util.ArrayList;
import java.util.Collection;

import com.esotericsoftware.kryo.Kryo;

public class Network {
	private static Collection<Class<?>> objectClasses;
	
	public static Collection<Class<?>> getTransferObjectClasses(){
		if(objectClasses == null){
			objectClasses = new ArrayList<Class<?>>();
			
			objectClasses.add(ClipboardContent.class);
		}
		
		return objectClasses;
	}
	
	public static void registerClasses(Kryo kryo){
		for(Class<?> curClass : getTransferObjectClasses()){
			kryo.register(curClass);
		}
	}
}
