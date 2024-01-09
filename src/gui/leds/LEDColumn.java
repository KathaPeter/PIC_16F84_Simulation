package gui.leds;

public class LEDColumn {
	
	private String name;
	private Boolean[] display;
	
	public LEDColumn(String name, Boolean[] value) {
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
