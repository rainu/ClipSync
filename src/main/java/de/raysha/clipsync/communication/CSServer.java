package de.raysha.clipsync.communication;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import de.raysha.clipsync.GlobalConfig;
import de.raysha.clipsync.bean.ClipboardContent;
import de.raysha.clipsync.bean.Network;
import de.raysha.clipsync.clipboard.ClipboardManager;

public class CSServer extends AbstractContentUpdater {
	protected final Server server;
	
	public CSServer() throws IOException{
		this(GlobalConfig.DEFAULT_TCP_PORT);
	}
	
	public CSServer(int port) throws IOException{
		this.server = establisServer(port);
		
		ClipboardManager.getInstance().addContentListener(this);
	}

	private Server establisServer(int port) throws IOException{
		Server lServer = new Server();
		lServer.addListener(listener);
		
		Network.registerClasses(lServer.getKryo());
		
		lServer.start();
		lServer.bind(port);
		
		return lServer;
	}
	
	@Override
	protected void finalize() throws Throwable {
		server.stop();
	}
	
	public Server getServer(){
		return server;
	}
	
	@Override
	public void distributeClipboardContent(ClipboardContent content) {
		for(Connection curCon : server.getConnections()){
			sendMessage(curCon, content);
		}
	}
	
	public static void main(String[] args) throws IOException {
		new CSServer();
	}
}
