package de.raysha.clipsync.bean;

public class ClipSyncDTO {
	private int number = 1;
	private int size = 1;
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "(" + number + "/" + size + ") ";
	}
}
