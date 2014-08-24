package de.raysha.clipsync.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import de.raysha.clipsync.GlobalConfig;
import de.raysha.clipsync.bean.ClipboardContent;

public class ClipboardManager{
	private static ClipboardManager instance;
	
	private final Clipboard systemClipboard;
	private final ClipboardNotifier notifier;
	private List<NewContentEventHandler> contentListener = new ArrayList<NewContentEventHandler>();
	private ClipboardContent lastAppliedContent = null;
	
	private ClipboardManager(){
		this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.notifier = buildNotifier();
	}
	
	protected ClipboardNotifier buildNotifier(){
		ClipboardNotifier cn = new ClipboardNotifier();
		
		cn.addClipboardListener(stringContentListener);
		
		cn.setFrequency(GlobalConfig.CLIPBOARD_POLL_INTERVALL);
		cn.setEnabled(true);
		
		return cn;
	}
	
	public static void initialise(){
		getInstance();	//da die initialisierung im konstruktor stattfindet
	}
	
	public static ClipboardManager getInstance(){
		if(instance == null){
			instance = new ClipboardManager();
		}
		
		return instance;
	}
	
	public void addContentListener(NewContentEventHandler handler){
		if(contentListener.contains(handler)) return;
		
		contentListener.add(handler);
	}
	
	public void removeContentListener(NewContentEventHandler handler){
		contentListener.remove(handler);
	}
	
	private ClipboardListener<String> stringContentListener = new ClipboardListener<String>(String.class) {
		@Override
		public void valueChanged(String oldValue, String newValue) {
			ClipboardContent c = ClipSyncFactory.getInstance().buildContent(newValue);
			
			if(!c.equals(lastAppliedContent)){
				//nur wenn es nicht mein zuvor eingefügter inhalt ist
				notifyHandler(c);
			}
		}
		
		void notifyHandler(ClipboardContent content){
			for(NewContentEventHandler handler : contentListener){
				handler.handleNewClipboardContent(content);
			}
		}
	};
	
	public void applyClipboardContent(ClipboardContent content){
		if(lastAppliedContent != null && lastAppliedContent.equals(content)){
			return;	//habe diesen inhalt bereits übernommen
		}
		
		Transferable t = ClipSyncFactory.getInstance().buildTransferable(content);
		systemClipboard.setContents(t, null);
		
		lastAppliedContent = content;
	}
	
	public static void main(String[] args) throws InterruptedException {
		ClipboardManager.getInstance();
		Thread.sleep(1000000);
	}
}
