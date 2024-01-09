package pipeline;

import memory.Code_Memory;

public class Fetcher {
	
	//stack und erste frei lassen also 1 + 8 frei dann bei 9 start :D
	
	public static int current_Instruction(int pc10Bit) {
		
		int instruction = Code_Memory.cMemory[pc10Bit];
		
		return instruction;
	}
}