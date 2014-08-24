package de.raysha.clipsync.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import de.raysha.clipsync.GlobalConfig;
import de.raysha.clipsync.bean.ClipSyncDTO;
import de.raysha.clipsync.bean.ClipboardContent;
import de.raysha.clipsync.clipboard.ClipboardManager;
import de.raysha.clipsync.clipboard.NewContentEventHandler;

public abstract class AbstractContentUpdater implements Synchronizer, NewContentEventHandler {
	private final MessageSpliter<ClipSyncDTO> splitter;

	protected AbstractContentUpdater(){
		//TODO: 90% des writebuffers (da abzüglich des json-overheads)
		this.splitter = new MessageSpliter<ClipSyncDTO>(ClipSyncDTO.class, 2000);
	}

	protected final Listener listener = new Listener(){
		private Collection<ClipSyncDTO> partBuffer = new ArrayList<ClipSyncDTO>();

		@Override
		public void received(Connection connection, Object object) {
			if(!(object instanceof ClipboardContent)) return;

			ClipboardContent receivedContent = (ClipboardContent)object;

			partBuffer.add(receivedContent);

			if(allPartsReceived()){
				ClipboardContent completeMessage = (ClipboardContent)splitter.reuinion(partBuffer);
				partBuffer.clear();

				//neue inhalt von gegenseite bekommen
				//bei uns muss er übernommen werden
				applyClipboardContent(completeMessage);
			}

		}

		boolean allPartsReceived(){
			if(!partBuffer.isEmpty()){
				//alle teile sind in dem buffer gespeichert
				return partBuffer.iterator().next().getSize() == partBuffer.size();
			}else{
				return true;
			}
		}
	};

	protected void sendMessage(Connection connection, ClipSyncDTO message){
		List<ClipSyncDTO> parts = splitter.splitMessage(message);
		for(ClipSyncDTO p : parts){
			connection.sendTCP(p);
		}
	}

	@Override
	public void applyClipboardContent(ClipboardContent content) {
		ClipboardManager.getInstance().applyClipboardContent(content);
	}

	@Override
	public void handleNewClipboardContent(ClipboardContent content) {
		if(content.getStringContent().length() > GlobalConfig.CLIPBOARD_MAX_SIZE){
			Log.warn("Inhalt der Zwichenablage ist zu gro\u00df (" + content.getStringContent().length() + ")!" +
					" Der \u00fcbertragene Inhalt wird auf " + GlobalConfig.CLIPBOARD_MAX_SIZE + " Zeichen gek\u00fcrtzt!");
			content.setStringContent(content.getStringContent().substring(0, GlobalConfig.CLIPBOARD_MAX_SIZE));
		}

		distributeClipboardContent(content);
	}
}
