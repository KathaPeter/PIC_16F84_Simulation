package opcodes.impl;

import memory.Data_Memory;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class SLEEP extends AExecutor{

	@Override
	public void execute(Decoder_Instruction instObject) {
		// Clear WDTimer and its prescaler if assigned -> p. 68
		Data_Memory.clearWDT();
		
		//Clear Flag _PD
		Data_Memory.set_PD(false);
		 
		//Set Flag _TO
		Data_Memory.set_TO(true);
		
		//Stop Oscillator
		Data_Memory.set_SLEEP(true);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}

}
