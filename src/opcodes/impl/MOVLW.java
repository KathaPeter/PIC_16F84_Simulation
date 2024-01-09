package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class MOVLW extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Special_Memory.setWReg(instObject.constant);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.constant, 2) + "h";
	}

}
