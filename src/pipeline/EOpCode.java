package pipeline;

import java.lang.reflect.InvocationTargetException;

import opcodes.IExecutor;

public enum EOpCode {
	CLRW("00 0001 0xxx xxxx"), NOP("00 0000 0xx0 0000"), CLRWDT("00 0000 0110 0100"), RETFIE("00 0000 0000 1001"),
	RETURN("00 0000 0000 1000"), SLEEP("00 0000 0110 0011"), ADDWF("00 0111 dfff ffff"), ANDWF("00 0101 dfff ffff"),
	CLRF("00 0001 1fff ffff"), COMF("00 1001 dfff ffff"), DECF("00 0011 dfff ffff"), DECFSZ("00 1011 dfff ffff"),
	INCF("00 1010 dfff ffff"), INCFSZ("00 1111 dfff ffff"), IORWF("00 0100 dfff ffff"), MOVF("00 1000 dfff ffff"),
	MOVWF("00 0000 1fff ffff"), RLF("00 1101 dfff ffff"), RRF("00 1100 dfff ffff"), SUBWF("00 0010 dfff ffff"),
	SWAPF("00 1110 dfff ffff"), XORWF("00 0110 dfff ffff"), BCF("01 00bb bfff ffff"), BSF("01 01bb bfff ffff"),
	BTFSC("01 10bb bfff ffff"), BTFSS("01 11bb bfff ffff"), ADDLW("11 111x kkkk kkkk"), ANDLW("11 1001 kkkk kkkk"),
	IORLW("11 1000 kkkk kkkk"), MOVLW("11 00xx kkkk kkkk"), RETLW("11 01xx kkkk kkkk"), SUBLW("11 110x kkkk kkkk"),
	XORLW("11 1010 kkkk kkkk"), CALL("10 0kkk kkkk kkkk"), GOTO("10 1kkk kkkk kkkk");

	private String mask;
	private int value;
	IExecutor executor;

	EOpCode(String mask) {
		
		//Load dynamically Class per Name by reflection
		try {
			this.executor = (IExecutor) Class.forName("opcodes.impl."+this.name()).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			System.out.println(" WAU WAU NOT FOUND CLASS "+this.name());
		}
		
		
		this.mask = "";

		// delete spaces
		for (Character c : mask.toCharArray()) {
			if (c != ' ') {
				this.mask = this.mask + c;
			}
		}

		// calc value
		for (int i = 0; i < this.mask.length(); i++) {

			// parse ascii to int
			// mask.charAt(mask.length() - 1 - i) - '0' == 1
			if (this.mask.charAt(this.mask.length() - 1 - i) == '1') {
				value += Math.pow(2.0, i);
			}
		}
	}

	public String getMask() {
		return mask;
	}

	public int getValue() {
		return value;
	}
	
	public void execute(Decoder_Instruction decoder_Instruction) {
		this.executor.execute(decoder_Instruction);
	}

	
	/*public IExecutor getExecutor() {
		return executor;
	}*/

	// Match an instruction
	public boolean match(int instruction) {
		for (int i = 0; i < mask.length(); i++) {
			boolean bitI = (instruction & 1 << i) != 0;
			char maskI = mask.charAt(mask.length() - 1 - i);

			switch (maskI) {

			case '0':
				if (bitI) {
					return false;
				}
				break;

			case '1':
				if (!bitI) {
					return false;
				}
				break;

			}

		}

		return true;
	}

	public String toString(Decoder_Instruction decoder_Instruction) {
		return executor.toString(decoder_Instruction);
	}
};
