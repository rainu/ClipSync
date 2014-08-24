package de.raysha.clipsync.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.raysha.clipsync.bean.ClipSyncDTO;
import de.raysha.clipsync.bean.ClipboardContent;

public class MessageSpliter<T extends ClipSyncDTO> {
	protected final Class<T> messageClass;
	protected final int maxLength;
	
	public MessageSpliter(Class<T> messageClass, int maxLength){
		this.messageClass = messageClass;
		this.maxLength = maxLength;
	}
	
	List<T> splitMessage(T orgMessage){
		if(!(orgMessage instanceof ClipboardContent))
			throw new RuntimeException("Can not split unknown ClipSyncDTO " + orgMessage.getClass().getName());
	
		ClipboardContent message = (ClipboardContent)orgMessage;
		if(	message.getStringContent() == null ||
			message.getStringContent().length() <= maxLength){
			
			message.setNumber(1);
			message.setSize(1);
			Collections.singletonList(message);
		}
		
		List<T> result = new ArrayList<T>();
		
		int number=0;
		for(int i=0; i < message.getStringContent().length(); i += maxLength){
			ClipboardContent part = new ClipboardContent();
			
			part.setStringContent(message.getStringContent().substring(
					i, Math.min(message.getStringContent().length(), i + maxLength)));
			
			part.setNumber(++number);
			
			result.add((T)part);
		}
		for(T part : result){
			part.setSize(number);
		}
		
		return result;
	}
	
	T reuinion(Collection<T> allParts){
		if(	allParts == null || allParts.isEmpty() || 
			allParts.iterator().next().getSize() != allParts.size()){
			
			return null;
		}
		
		if(!(allParts.iterator().next() instanceof ClipboardContent))
			throw new RuntimeException("Can not union unknown ClipSyncDTO " + allParts.iterator().next().getClass().getName());
			
		List<T> sorted = new ArrayList<T>(allParts);
		Collections.sort(sorted, new Comparator<ClipSyncDTO>() {
			@Override
			public int compare(ClipSyncDTO o1, ClipSyncDTO o2) {
				return new Integer(o1.getNumber()).compareTo(o2.getNumber());
			}
		});
		
		ClipboardContent completeContent = new ClipboardContent();
		completeContent.setStringContent("");
		
		for(ClipboardContent part : (List<ClipboardContent>)sorted){
			completeContent.setStringContent(completeContent.getStringContent() + part.getStringContent());
		}
		
		return (T)completeContent;
	}
	
	
	public static void main(String[] args) {
		MessageSpliter<ClipboardContent> ms = new MessageSpliter<ClipboardContent>(ClipboardContent.class, 5);
		
		ClipboardContent content = new ClipboardContent();
		content.setStringContent("Hallo Rainu, wie geht es dir denn so?");
		List<ClipboardContent> parts = ms.splitMessage(content);
		
		System.out.println(parts);
		System.out.println(ms.reuinion(parts));
	}
}
