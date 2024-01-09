package memory;

public enum EReset_Cause {

	NO_RESET,
	
	//Call reset, set program_counter to 0x00; 
	WDT_RESET, MCLR_RESET_NORMAL, MCLR_RESET_SLEEP,

	// Set Sleep to false, set program_counter to (program_counter +1)
	WDT_WAKEUP, INTERRUPT_WAKEUP_FROM_SLEEP
}
