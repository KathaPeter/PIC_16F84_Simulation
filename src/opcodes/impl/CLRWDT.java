package opcodes.impl;

import memory.Data_Memory;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class CLRWDT extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Data_Memory.clearWDT();
		Data_Memory.set_TO(true);
		Data_Memory.set_PD(true);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}

}
