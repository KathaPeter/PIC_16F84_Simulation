package gui.switches;

public class SwitchRow {

	private String name;
	private Boolean[] display;

	public SwitchRow(String name, Boolean[] value) {
		this.name = name;
		this.display = value;
	}

	public String getName() {
		return name;
	}

	public Boolean getDisplay(int i) {
		return display[i];
	}

}
