package de.raysha.clipsync.clipboard;

import de.raysha.clipsync.bean.ClipboardContent;

public interface NewContentEventHandler {

	/** 
	 * Wird aufgerufen, wenn sich der Zwichenspeicherinhalt geändert hat.
	 * 
	 * @param content
	 */
	public void handleNewClipboardContent(ClipboardContent content);
}
