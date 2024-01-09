package gui.gpr;

public class GPRColumn {

	private String name;
	private String[] hexValues;

	public GPRColumn(String name) {
		
	}

	public GPRColumn(String name, int row, int[] gprData) {
		this.name = name;
		this.hexValues = new String[16];
		

		for (int i = 0; i < hexValues.length; i++) {
			String text = Integer.toHexString(gprData[i + row * 16]);		

			if (text.length() < 2) {
				text = "0" + text;
			}

			hexValues[i] = text;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHex(int i) {
		return hexValues[i];
	}

}
