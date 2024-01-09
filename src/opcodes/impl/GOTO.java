package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class GOTO extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Data_Memory.opCode_goto(instObject.constant);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "   " + toHex(instObject.constant, 3) + "h";
	}


}
