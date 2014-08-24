package de.raysha.clipsync.communication;

import de.raysha.clipsync.bean.ClipboardContent;

public interface Synchronizer {

	/**
	 * Verteilt den Zwischenspeicherinhalt an die Gegenseite.
	 * 
	 * @param content Inhalt, der verteilt werden soll.
	 */
	public void distributeClipboardContent(ClipboardContent content);
	
	/**
	 * Setzt den gegebenen Zwichenspeicherinhalt auf diesem System.
	 * 
	 * @param content Inhalt, der gesetzt werden soll.
	 */
	public void applyClipboardContent(ClipboardContent content);
}
