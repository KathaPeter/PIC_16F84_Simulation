package opcodes.impl;

import memory.Data_Memory;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class RETFIE extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Data_Memory.opCode_RETFIE();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}
}
