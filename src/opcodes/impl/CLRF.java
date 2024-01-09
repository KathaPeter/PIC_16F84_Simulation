package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class CLRF extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		Data_Memory.write(instObject.address7Bit, 0, true);
		ALU8Bit.flagZ = true;
		updateZFlag();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "   " + toHex(instObject.address7Bit, 2) + "h ";
	}

}
