package de.raysha.clipsync.clipboard;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import de.raysha.clipsync.bean.ClipboardContent;

public class ClipSyncFactory {
	private static ClipSyncFactory instance;
	
	public static ClipSyncFactory getInstance(){
		if(instance == null){
			instance = new ClipSyncFactory();
		}
		
		return instance;
	}
	
	public Transferable buildTransferable(ClipboardContent content){
		if(content.getStringContent() != null){
			StringSelection strSel = new StringSelection(content.getStringContent());
			return strSel;
		}
		
		return null;
	}
	
	public ClipboardContent buildContent(String stringContent){
		ClipboardContent c = new ClipboardContent();
		c.setStringContent(stringContent);
		
		return c;
	}
}
