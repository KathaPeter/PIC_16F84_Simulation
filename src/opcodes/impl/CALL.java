package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class CALL extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int retAdress = Data_Memory.getPC13Bit();
		Ringbuffer.push(retAdress);
		
		Data_Memory.opCode_goto(instObject.constant);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "   " + toHex(instObject.constant, 3) + "h";
	}

}
