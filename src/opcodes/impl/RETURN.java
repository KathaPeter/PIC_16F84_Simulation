package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class RETURN extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Data_Memory.updatePC(Ringbuffer.pop());
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}


}
