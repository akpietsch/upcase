package de.uni_koeln.spinfo.drc.mongodb.data.core;



public class RangeDocument extends AbstractDocument {
	
	private int start;
	private int end;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	

}
