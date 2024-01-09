package gui.lst;

public class LSTRow {

	private String index;
	private boolean breakpoint;
	private String lstline;
	private String lstlineorig;
	
	public LSTRow(int index, String lstline, String lstlineorig) {
		super();
		this.index = ""+index;
		this.lstline = lstline;
		this.lstlineorig = lstlineorig;
		this.breakpoint = false;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public boolean isBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(boolean breakpoint) {
		this.breakpoint = breakpoint;
	}

	public String getLstline() {
		return lstline;
	}

	public void setLstline(String lstline) {
		this.lstline = lstline;
	}

	public String getLstlineorig() {
		return lstlineorig;
	}

	public void setLstlineorig(String lstlineorig) {
		this.lstlineorig = lstlineorig;
	}

}
