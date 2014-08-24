package de.raysha.clipsync.bean;

public class ClipboardContent extends ClipSyncDTO{
	private String stringContent;
	
	public ClipboardContent(){
	}
	
	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stringContent == null) ? 0 : stringContent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClipboardContent other = (ClipboardContent) obj;
		if (stringContent == null) {
			if (other.stringContent != null)
				return false;
		} else if (!stringContent.equals(other.stringContent))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "ClipboardContent [stringContent=" + stringContent + "]";
	}
}
