package de.raysha.clipsync.communication;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.raysha.clipsync.GlobalConfig;
import de.raysha.clipsync.bean.ClipboardContent;
import de.raysha.clipsync.bean.Network;
import de.raysha.clipsync.clipboard.ClipboardManager;

public class CSClient extends AbstractContentUpdater {
	protected final Client client;
	protected final int MAX_TRANSER_SIZE;
	
	public CSClient(String host) throws IOException{
		this(host, GlobalConfig.DEFAULT_TCP_PORT, 5000);
	}
	
	public CSClient(String host, int timeout) throws IOException{
		this(host, GlobalConfig.DEFAULT_TCP_PORT, timeout);
	}
	
	public CSClient(String host, int port, int timeout) throws IOException{
		this.client = establishClient(host, port, timeout);
		
		//TODO: 90% des writebuffers (da abzüglich des json-overheads)
		this.MAX_TRANSER_SIZE = this.client.getTcpWriteBufferSize() * 90 / 100;
		System.out.println(MAX_TRANSER_SIZE);
		ClipboardManager.getInstance().addContentListener(this);
	}
	
	private Client establishClient(String host, int port, int timeout) throws IOException{
		Client lClient = new Client();
		lClient.addListener(listener);
		
		Network.registerClasses(lClient.getKryo());
		
		lClient.addListener(clientListener);
		lClient.start();
		lClient.connect(timeout, host, port);
		
		return lClient;
	}
	
	@Override
	protected void finalize() throws Throwable {
		client.stop();
	}
	
	@Override
	public void distributeClipboardContent(ClipboardContent content) {
		sendMessage(client, content);
	}
	
	private Listener clientListener = new Listener(){
		@Override
		public void disconnected(Connection connection) {
			if(!client.isConnected()){
				client.stop();
			}
		}
	};
	
	public Client getClient(){
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		ClipboardManager.getInstance();
		
		CSClient c = new CSClient("localhost");
		c.distributeClipboardContent(new ClipboardContent());
		c.client.getUpdateThread().join();
	}
}
