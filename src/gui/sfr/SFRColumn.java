package gui.sfr;

public class SFRColumn {

	private String name;
	private String[] bits = new String[] { "", "", "", "", "", "", "", "" };
	private String dec = "";
	private String hex = "";

	public SFRColumn() {
		/*
		 * this.name = element.getName(); String[] bitnames = element.getBitnames(); boolean bBits = element.isBits();
		 * 
		 * this.dec = "" + element.getValue(); if (this.dec.length() < 2) { this.dec = "00" + this.dec; } else if (this.dec.length() < 3) { this.dec = "0" +
		 * this.dec; }
		 * 
		 * this.hex = Integer.toHexString(element.getValue()); if (this.hex.length() < 2) { this.hex = "0" + this.hex; }
		 * 
		 * // get bits for bitCells int[] ibits = new int[8]; for (int i = 0; i < 8; ++i) { int bitmask = 1 << i; ibits[i] = (element.getValue() & bitmask) != 0
		 * ? 1 : 0; }
		 */

	}

	public SFRColumn(String name, final int value, boolean bBits) {
		this.name = name;

		// get dec value
		this.dec = "" + value;
		if (this.dec.length() < 2) {
			this.dec = "00" + this.dec;
		} else if (this.dec.length() < 3) {
			this.dec = "0" + this.dec;
		}

		// get hex value
		this.hex = Integer.toHexString(value);
		if (this.hex.length() < 2) {
			this.hex = "0" + this.hex;
		}

		// get bits for bitCells
		for (int i = 0; i < 8 && bBits; ++i) {
			int bitmask = 1 << i;
			bits[i] = "" + ((value & bitmask) != 0 ? 1 : 0);
		}
	}

	public SFRColumn(String name, String[] bitnames) {
		this.name = name;

		// get bits for bitCells
		for (int i = 0; i < 8; ++i) {
			bits[i] = bitnames[7-i];
		}

	}

	public String getBit(int index) {
		return bits[index];
	}

	public String getDec() {
		return dec;
	}

	public String getHex() {
		return hex;
	}

	public String getName() {
		return name;
	}
}
