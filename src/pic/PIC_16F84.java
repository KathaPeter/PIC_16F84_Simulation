package pic;

import java.util.ArrayList;
import java.util.List;
import gui.IGui;
import gui.sfr.SFRColumn;
import memory.Code_Memory;
import memory.Data_Memory;
import memory.EEPROM;
import memory.EReset_Cause;
import memory.Port_A;
import memory.Port_B;
import memory.Prescaler;
import memory.Ringbuffer;
import memory.guiutil.LSTLine;
import memory.guiutil.Value_Pair;
import pipeline.Decoder_Find;
import pipeline.Decoder_Instruction;
import pipeline.Executor;
import pipeline.Fetcher;

public class PIC_16F84 {

	private volatile boolean power = false;
	private volatile boolean run = false;
	private volatile boolean oneStep = false;
	private volatile boolean WDT_enable = false;

	private volatile long lRuntimeMicroSec = 0L;

	private volatile int iTSimMilliSec = 500; // 500ms = 0,5 Seconds
	private IGui gui;
	private volatile boolean EXIT;
	private List<Integer> breakpoints = new ArrayList<Integer>();
	private Thread mainThread;

	public PIC_16F84(IGui gui) {

		this.gui = gui;
		Data_Memory.gui = gui;

		EXIT = false;

		mainThread = new Thread(new Runnable() {

			@Override
			public void run() {
				mainLoop();
			}
		});

		// Start threads
		mainThread.start();
	}

	/******************************************************************************************
	 * 
	 * CONTROL-METHODS
	 ******************************************************************************************/

	public boolean doTogglePower() {

		power = !power;

		// START SEQUENZ/STATE
		if (power) {
			Data_Memory.resetPOWER_ON(); // POWER_ON_RESET
			lRuntimeMicroSec = 0;
		} else {
			oneStep = false;
		}

		updateGUI(power);

		run = false; // RUN NOT AT START OR END

		return power;
	}

	public boolean doToggleRun() {
		if (power) {
			run = !run;
		}

		return run;
	}

	public boolean doToggleWDTE() {
		if (!power) {
			WDT_enable = !WDT_enable;
		}

		return WDT_enable;
	}

	public void doLoadFile(String lstFilePath) throws PICSimException {
		if (!power) {
			Code_Memory.fileread(lstFilePath);
		} else {
			throw new PICSimException("PIC muss aus sein!");
		}
	}

	public void doResetEEPROM() throws PICSimException {
		if (!power) {
			EEPROM.reset();
		} else {
			throw new PICSimException("PIC muss aus sein!");
		}
	}

	// TODO do one step?
	public void doOneStep() {
		if (power && !run) {
			oneStep = true;
		}
	}

	public void doMCLR() {
		if (power) {

			if (Data_Memory.isSLEEP()) {
				Data_Memory.set_XReset(EReset_Cause.MCLR_RESET_SLEEP);
			} else {
				Data_Memory.set_XReset(EReset_Cause.MCLR_RESET_NORMAL);
			}

			doOneStep();
		}
	}

	public void doExit() {
		EXIT = true;
		try {
			// Application till the end of mainLoop cycle 
			mainThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public void doUpdatePinA(boolean b, int bit) {

		// Just the first 5bits in PORTA
		if (bit < 5) {
			Port_A.iPortA.setSwitch(b, bit);

			if (power) {
				// inform pic: new value at port a also in idle state
				Port_A.update_writeFromOut();
				updateGUI(true);
			}
		}
	}

	public void doUpdatePinB(boolean b, int bit) {
		Port_B.iPortB.setSwitch(b, bit);
		if (power) {
			// inform pic: new value at port a also in idle state
			Port_B.update_writeFromOut();
			updateGUI(true);
		}
	}

	public void setTSimMilliSec(int newTsimMilliSec) {
		if (!power) {
			iTSimMilliSec = newTsimMilliSec;
			lRuntimeMicroSec = 0;
		}
	}

	public void doSetBreakpoints(List<Integer> breakpoints) {
		this.breakpoints = breakpoints;
	}

	/**********************************************************************************************
	 * 
	 * UPDATE GUI
	 ***********************************************************************************************/

	public void updateGUI(boolean data) {

		int[] gprData;
		List<SFRColumn> sfrData;
		int[] stackData;
		List<Value_Pair<Boolean[]>> portsData;

		if (data) {
			gprData = Data_Memory.getGPRData();
			sfrData = Data_Memory.getSFRData();
			stackData = Ringbuffer.getStackData();
			portsData = Data_Memory.getPortsData();

			gui.updateLSTMarker(Data_Memory.getPC13Bit());
		} else {
			gprData = new int[0];
			stackData = new int[0];
			sfrData = new ArrayList<>();
			portsData = new ArrayList<>();
		}

		gui.updateGPR(gprData);
		gui.updateSFR(sfrData);
		gui.updateStack(stackData, Ringbuffer.getStackPos());
		gui.updatPorts(portsData);
		gui.updateRuntime(lRuntimeMicroSec);

	}

	/**********************************************************************************************
	 * 
	 * LOOPS
	 ***********************************************************************************************/

	private void mainLoop() {
		while (!EXIT) {

			if (power) {
				if (run || oneStep) {

					oneTick(!oneStep);

					testForReset();

					if (oneStep) {
						oneStep = false; // just once true
					}

				} else {
					// IDLE - DO NOTHING - wait for one step or run
				}

			} else {
				// OFF - DO NOTHING - wait for power on
			}
		}
	}

	private void testForReset() {
		EReset_Cause cause = Data_Memory.is_X_RESET();
		if (cause != EReset_Cause.NO_RESET) {

			switch (cause) {

			// PD and TO are 3th and 4th bits in STATUS_Register
			case INTERRUPT_WAKEUP_FROM_SLEEP:
				Data_Memory.set_PD(false);
				Data_Memory.set_TO(true);
				// Set PC to PC + 1
				Data_Memory.set_XReset(EReset_Cause.NO_RESET);
				Data_Memory.set_SLEEP(false);
				break;

			case MCLR_RESET_NORMAL:
				// Data_Memory.set_PD(true);
				// Data_Memory.set_TO(true);
				Data_Memory.resetMCLR_WDT_RESET();
				lRuntimeMicroSec = 0L;
				break;

			case MCLR_RESET_SLEEP:
				Data_Memory.set_PD(false);
				Data_Memory.set_TO(true);
				Data_Memory.resetMCLR_WDT_RESET();
				lRuntimeMicroSec = 0L;
				break;

			case WDT_RESET:
				Data_Memory.set_PD(true);
				Data_Memory.set_TO(false);
				Data_Memory.resetMCLR_WDT_RESET();
				lRuntimeMicroSec = 0L;
				break;

			case WDT_WAKEUP:
				Data_Memory.set_PD(false);
				Data_Memory.set_TO(false);
				Data_Memory.set_XReset(EReset_Cause.NO_RESET);
				Data_Memory.set_SLEEP(false);
				break;

			default:
				Data_Memory.set_XReset(EReset_Cause.NO_RESET);
				break;
			}

			updateGUI(true);

			if (run) {
				waitMicroSec();
			}
		}
	}

	// oneTick() simulates 1us!!
	private void oneTick(boolean runmode) {

		int pc13Bit = Data_Memory.getPC13Bit();

		if (!Data_Memory.isSLEEP()) {

			if (runmode && isBreakPoint(pc13Bit)) {
				// SET run to false on Breakpoint to stop execution loop
				run = false;
				// inform gui
				gui.eventStop();
				return;
			}

			// Increment _pc for next instruction
			Data_Memory.updatePC(pc13Bit + 1);

			// Pipline FETCH-DECODE-EXECUTE
			int current_Instruction = Fetcher.current_Instruction(pc13Bit);
			Decoder_Instruction searchInstruction = Decoder_Find.searchInstruction(current_Instruction);
			Executor.execute(searchInstruction);

			// testInterrupt
			Data_Memory.testForInterrupt();
			

			// Tick from Clock
			Prescaler.muxA(false);
		}

		// Tick per Step
		lRuntimeMicroSec++;

		if (WDT_enable) {
			Data_Memory.tickWDT1us();
		}
		testForReset();

		updateGUI(true);

		// Simulate delay only in runmode
		if (runmode) {
			waitMicroSec();
		}

	}

	/**
	 * PRIVATE METHODS
	 */

	private boolean isBreakPoint(int pc13Bit) {
		List<Integer> list = breakpoints;
		return list.contains((Integer) pc13Bit);
	}

	private void waitMicroSec() {

		try {
			//
			Thread.sleep(iTSimMilliSec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * GETTER METHODS
	 */

	public long getlRuntimeMicroSec() {
		return lRuntimeMicroSec;
	}

	public int getTSimMilliSec() {
		return iTSimMilliSec;
	}

	public List<LSTLine> getLST() {
		return Code_Memory.getLST();
	}

	public boolean isLSTLoaded() {
		return Code_Memory.getLineCounter() > 0;
	}

	public boolean isPower() {
		return power;
	}

	public boolean isRun() {
		return run;
	}

	public boolean isWDTE() {
		return WDT_enable;
	}

	public boolean isInSleep() {
		return Data_Memory.isSLEEP();
	}

}
