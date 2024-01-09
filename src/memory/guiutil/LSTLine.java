package memory.guiutil;

public class LSTLine {

	private String lstlineOrig;
	private String lstline;
	
	

	public LSTLine(String lstline, String lstlineOrig) {
		super();
		this.lstlineOrig = lstlineOrig;
		this.lstline = lstline;
	}

	public String getLine() {
		return lstline;
	}

	public String getOrigLine() {
		return lstlineOrig;
	}

}
