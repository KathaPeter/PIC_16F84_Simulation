package memory;

import java.util.ArrayList;
import java.util.List;

import gui.IGui;
import gui.sfr.SFRColumn;
import memory.guiutil.Value_Pair;
import pipeline.Executor;

public class Data_Memory {

	public static IGui gui;

	public static final int ADDRESS_STATUS = 0x03;
	public static final int INTERRUPT_ADDRESS = 0x04;

	private static volatile boolean SLEEP = false;
	private static volatile EReset_Cause X_RESET = EReset_Cause.NO_RESET;
	private static volatile int WDT_counter = 0;

	private static volatile boolean bSkip = false;

	private static int bank[] = new int[128];

	private static volatile int TMR0;
	private static volatile int PCL;
	private static volatile int PCH;
	private static volatile int STATUS;
	private static volatile int FSR;

	private static volatile int OPTION;
	private static volatile int EECON1;

	private static volatile int EEDATA;
	private static volatile int EEADR;
	private static volatile int PCLATH;
	private static volatile int INTCON;

	public static void resetPOWER_ON() {

		// Reset values manual p. 43
		FSR = 1; // otherwise endless-loop
		PCL = 0;
		PCH = 0;
		PCLATH = 0;
		STATUS &= 0x1F;
		STATUS |= 0x18;
		INTCON &= 0x1;
		OPTION = 0xFF;
		EECON1 = 0;

		X_RESET = EReset_Cause.NO_RESET;
		SLEEP = false;
		Executor.bSkip = false;
		Data_Memory.bSkip = false;
		Ringbuffer.reset();

		// Clear LED's
		Port_A.iPortA.resetLED();
		Port_B.iPortB.resetLED();

		// init TRISA, TRISB and also get from gui-switches /set gui LEDs
		Port_A.writeFromInTRISA(0xFF);
		Port_B.writeFromInTRISB(0xFF);
	}

	public static void resetMCLR_WDT_RESET() {
		// Reset values manual p. 43, 47
		PCL = 0;
		PCH = 0;
		PCLATH = 0;

		STATUS &= 0x1F;

		INTCON &= 0x1;
		OPTION = 0xFF;
		EECON1 = 0;

		X_RESET = EReset_Cause.NO_RESET;
		SLEEP = false;
		Executor.bSkip = false;
		Data_Memory.bSkip = false;
		Ringbuffer.reset();

		// Clear LED's
		Port_A.iPortA.resetLED();
		Port_B.iPortB.resetLED();

		// init TRISA, TRISB
		Port_A.writeFromInTRISA(0xFF);
		Port_B.writeFromInTRISB(0xFF);
	}

	public static boolean isSLEEP() {
		return SLEEP;
	}

	public static void set_SLEEP(boolean b) {
		SLEEP = b;
		gui.sleepChangedEvent();
	}

	
	public static EReset_Cause is_X_RESET() {
		return X_RESET;
	}

	public static void set_XReset(EReset_Cause cause) {
		X_RESET = cause;
	}
	
	

	/**************************************
	 * PC
	 **********************************************/

	// p. 18
	public static int getPC13Bit() {
		return (PCL + (PCH << 8)) & 0x1FFF;
	}

	public static void updatePC(int pc13Bit) {
		PCH = (pc13Bit >> 8) & 0x1F; // 5bits PCH
		PCL = pc13Bit & 0xFF; // 8bits PCL
	}

	// CALL & GOTO to write PC
	public static void opCode_goto(int data11Bit) {
		int pc13Bit = (data11Bit & 0x7FF) + ((PCLATH & 0x18) << 8);
		updatePC(pc13Bit);
	}

	// p. 18	
	private static void writePCL(int data8Bit) {
		int pc13Bit = (data8Bit & 0xFF) + ((PCLATH & 0x1F) << 8);
		updatePC(pc13Bit);
	}

	public static void testForInterrupt() {
		boolean bInterrupt = false;

		// Every new fetch proofs if an interrupt exits (T0IF, INTF, RBIF ->p. 48)
		// TEST INTERRUPT -> manual p. 17 and 48

		// GIE
		if ((INTCON & 0x80) != 0) {
			// RBIE && RBIF
			if ((INTCON & 0x1) != 0 && (INTCON & 0x8) != 0) {
				bInterrupt = true;
			}

			// INTF && INTE
			if ((INTCON & 0x2) != 0 && (INTCON & 0x10) != 0) {
				bInterrupt = true;
			}

			// T0IF && T0IE
			if ((INTCON & 0x4) != 0 && (INTCON & 0x20) != 0) {
				bInterrupt = true;
			}
		}

		if (bInterrupt) {

			INTCON &= 0x7F; // DISABLE GIE 

			/**
			 * kontext wechsel
			 */
			Ringbuffer.push(getPC13Bit());

			// Ensure that next OpCode will be executed 
			Data_Memory.bSkip = Executor.bSkip; 
			Executor.bSkip = false;

			// Save INTERRUPT_ADDRESS as pc
			PCL = (INTERRUPT_ADDRESS) & 0xFF;
			PCH = ((INTERRUPT_ADDRESS) & 0x3FF) >> 8; 
		}

	}

	public static void opCode_RETFIE() {

		updatePC(Ringbuffer.pop());

		// GIE enable
		INTCON |= 0x80;

		// bSkip rücksetzen auf Wert vor dem Interrupt
		Executor.bSkip = Data_Memory.bSkip;
	}

	// Interrupt WakeUpTest see scribble
	private static void interrupt_WakeUpTest() {
		if (SLEEP) {
			boolean b = false;
			if ((INTCON & 0x1) != 0 && (INTCON & 0x8) != 0) {
				b = true;
			}

			// INTF && INTE
			else if ((INTCON & 0x2) != 0 && (INTCON & 0x10) != 0) {
				b = true;
			}

			// T0IF && T0IE
			else if ((INTCON & 0x4) != 0 && (INTCON & 0x20) != 0) {
				b = true;
			}

			if (b) {
				X_RESET = EReset_Cause.INTERRUPT_WAKEUP_FROM_SLEEP;
			}
		}
	}

	public static void tickTimer0() {
		int tmr = TMR0;
		tmr++;
		if (tmr > 255) {
			tmr = 0;
			setT0IF();
		}
		TMR0 = tmr;
	}

	public static void tickWDT1us() {
		WDT_counter++;
		if (WDT_counter >= 18000) {
			WDT_counter = 0;

			Prescaler.muxC(true);
			Prescaler.muxD(false);
		}
	}

	public static void clearWDT() {
		// clear prescaler if assigned -> p. 29
		WDT_counter = 0;
		if (isPSA()) {
			Prescaler.resetPrescaler();
		}
	}

	/*******************************************************************************************************
	 ****************************************** read()Data_Memory,write()into_Data_Memory********************
	 *******************************************************************************************************/

	/*
	 * This Method selects the right bank for executing OpCodes in PIC16F84.
	 * 
	 * @param address7Bit is the seven bits wide address, which represents the two
	 * 2K banks of Data_Memory.
	 * 
	 * @return read8Bit_Bank_x() calls function to read from right register in
	 * preselected bank.
	 */

	public static int read8bit(int address7Bit) {

		// Check if RP0 (4th bit in STATUS) to select bank is 0 or 1
		if ((STATUS & 0x20) == 0) {
			// bank_0
			return read8Bit_Bank_0(address7Bit);
		} else {
			// bank_1
			return read8Bit_Bank_1(address7Bit);
		}

	}

	private static int read8Bit_Bank_0(int address7Bit) {

		switch (address7Bit) {

		case 0:
			// If request is bigger than address 0x7F select bank_0, else select bank_1
			if ((FSR & 0x80) == 0) {
				// bank_0
				return read8Bit_Bank_0(FSR & 0x7F);
			} else {
				// bank_1
				return read8Bit_Bank_1(FSR & 0x7F);
			}
		case 1:
			return TMR0;
		case 2:
			return PCL;
		case ADDRESS_STATUS:
			return STATUS;
		case 4:
			return FSR;
		case 5:
			return Port_A.getPORTA();
		case 6:
			return Port_B.getPORTB();
		case 7:
			return 0;
		case 8:
			return EEDATA;
		case 9:
			return EEADR;
		case 10:
			return PCLATH;
		case 11:
			return INTCON;
		default:
		}

		if (address7Bit >= 0xC && address7Bit <= 0x4F) {
			return bank[address7Bit];
		}

		return 0;
	}

	private static int read8Bit_Bank_1(int address7Bit) {

		switch (address7Bit) {

		case 0:
			if ((FSR & 0x80) == 0) {
				// bank_0
				return read8Bit_Bank_0(FSR & 0x7F);
			} else {
				// bank_1
				return read8Bit_Bank_1(FSR & 0x7F);
			}
		case 1:
			return OPTION;
		case 2:
			return PCL;
		case ADDRESS_STATUS:
			return STATUS;
		case 4:
			return FSR;
		case 5:
			return Port_A.getTRISA();
		case 6:
			return Port_B.getTRISB();
		case 7:
			return 0;
		case 8:
			return EECON1;
		case 9:
			// EECON2 p.34 read as 0
			return 0;
		case 10:
			return PCLATH;
		case 11:
			return INTCON;
		default:
		}

		if (address7Bit >= 0xC && address7Bit <= 0x4F) {
			return bank[address7Bit];
		}
		return 0;
	}

	/*
	 * boolean affectZDCC: false if OPCcode is affected the STATUS_Register, if true
	 * than is not affected If is affected than it's not allowed to write into
	 * Flag_Bits in STATUS_Register -> p. 15
	 */
	public static void write(int address7Bit, int data8Bit, boolean affectZDCC) {
		// If RP0 isn't set select bank_0, else select bank_1
		if ((STATUS & 0x20) == 0) {
			// bank_0
			write8Bit_Bank_0(address7Bit, data8Bit, affectZDCC);
		} else {
			// bank_1
			write8Bit_Bank_1(address7Bit, data8Bit, affectZDCC);
		}
	}

	private static void write8Bit_Bank_0(int address7Bit, int data8Bit, boolean affectZDCC) {
		switch (address7Bit) {
		case 0:
			if ((FSR & 0x80) == 0) {
				// Bank_0
				write8Bit_Bank_0(FSR & 0x7F, data8Bit, affectZDCC);
			} else {
				// Bank_1
				write8Bit_Bank_1(FSR & 0x7F, data8Bit, affectZDCC);
			}
			break;

		case 1:
			TMR0 = data8Bit;

			if (!isPSA()) {
				// clear Prescaler -> p. 29
				Prescaler.resetPrescaler();
			}
			break;
		case 2:
			writePCL(data8Bit);
			break;
		case ADDRESS_STATUS:
			write_STATUS(data8Bit, affectZDCC);
			break;
		case 4:
			FSR = data8Bit;
			break;
		case 5:
			Port_A.writeFromInPORTA(data8Bit);
			break;
		case 6:
			Port_B.writeFromInPORTB(data8Bit);
			break;
		case 7:
			break;
		case 8:
			EEDATA = data8Bit;
			break;
		case 9:
			EEADR = data8Bit;
			break;
		case 10:
			PCLATH = data8Bit;
			break;
		case 11:
			INTCON = data8Bit;
			break;
		default:
		}

		if (address7Bit >= 0xC && address7Bit <= 0x4F) {
			bank[address7Bit] = data8Bit;
		}
	}

	private static void write8Bit_Bank_1(int address7Bit, int data8Bit, boolean affectZDCC) {
		switch (address7Bit) {
		case 0:

			if ((FSR & 0x80) == 0) {
				// Bank_0
				write8Bit_Bank_0(FSR & 0x7F, data8Bit, affectZDCC);
			} else {
				// Bank_1
				write8Bit_Bank_1(FSR & 0x7F, data8Bit, affectZDCC);
			}
			break;

		case 1:
			OPTION = data8Bit;
			break;
		case 2:
			writePCL(data8Bit);
			break;
		case ADDRESS_STATUS:
			write_STATUS(data8Bit, affectZDCC);
			break;
		case 4:
			FSR = data8Bit;
			break;
		case 5:
			Port_A.writeFromInTRISA(data8Bit);
			break;
		case 6:
			Port_B.writeFromInTRISB(data8Bit);
			break;
		case 7:
			break;
		case 8:

			// OR cause the WR and RD bit can only be set (not cleared) -> p.33
			EECON1 |= (data8Bit & 0x7);

			// Unless WREN isn't set, clear WR -> p.34
			if (!Util.isBit(EECON1, 2)) {
				EECON1 = Util.clearBit(EECON1, 1);
			}

			// Allow clear WREN = bit 2
			if (!Util.isBit(data8Bit, 2)) {
				EECON1 = Util.clearBit(EECON1, 2);
			}

			// Allow clear EEIF = bit 4
			if (!Util.isBit(data8Bit, 4)) {
				EECON1 = Util.clearBit(EECON1, 4);
			}

			if (Util.isBit(EECON1, 1)) {
				EEPROM.write();
			} else if (Util.isBit(EECON1, 0)) {
				EEPROM.read();
			}

			break;
		case 9:
			EECON2.setEECON2(data8Bit);
			break;
		case 10:
			PCLATH = data8Bit;
			break;
		case 11:
			INTCON = data8Bit;
			break;
		default:
		}

		if (address7Bit >= 0xC && address7Bit <= 0x4F) {
			bank[address7Bit] = data8Bit;
		}
	}

	// Information to manipulate STATUS p.15
	private static void write_STATUS(int newSTATUS, boolean affectZDCC) {
		// If OPCcode is affected the STATUS_Register than just only the 3 bit can be
		// written to -> p. 15
		if (affectZDCC) {
			// just change bits 5-7
			newSTATUS &= 0xE0;
			STATUS &= 0x1F;
		} else {
			// just change bits 0-2 and 5-7
			newSTATUS &= 0xE7;
			STATUS &= 0x18;
		}

		STATUS |= newSTATUS;
	}

	public static void updateZFlag(boolean flagZ) {
		if (flagZ) {
			STATUS |= 0x4;
		} else {
			STATUS &= ~0x4;
		}
	}

	public static void updateCFlag(boolean flagC) {
		if (flagC) {
			STATUS |= 0x1;
		} else {
			STATUS &= ~0x1;
		}
	}

	public static void updateAllFlags(boolean flagC, boolean flagDC, boolean flagZ) {
		if (flagZ) {
			STATUS |= 0x4;
		} else {
			STATUS &= ~0x4;
		}

		if (flagC) {
			STATUS |= 0x1;
		} else {
			STATUS &= ~0x1;
		}

		if (flagDC) {
			STATUS |= 0x2;
		} else {
			STATUS &= ~0x2;
		}
	}

	/*****************************************************************************************************
	 * system-internal read SFR bits
	 *****************************************************************************************************/
	public static boolean isEECON1_WR() {
		return Util.isBit(EECON1, 1);
	}

	public static void clearEECON1_WR() {
		EECON1 = Util.clearBit(EECON1, 1);
	}

	public static boolean isEECON1_RD() {
		return Util.isBit(EECON1, 0);
	}

	public static void setEEDATA(int data8Bit) {
		EEDATA = data8Bit;
	}

	public static int getEEADR() {
		return EEADR;
	}

	public static int getEEDATA() {
		return EEDATA;
	}

	public static void clearEECON1_RD() {
		EECON1 = Util.clearBit(EECON1, 0);
	}

	public static boolean isT0SE() {
		// 5th bit is the T0SE in OPTION_REGISTER
		return (OPTION & 0x10) != 0;
	}

	public static boolean isINTEDG() {
		// 6th bit is the INTEDG in OPTION_REGISTER
		return (OPTION & 0x40) != 0;
	}

	// No boolean -> set only by event and cleared by software
	public static void setINTF() {
		INTCON |= 0x2;
		interrupt_WakeUpTest();
	}

	// No boolean -> set only by event and cleared by software //p. 27
	public static void setT0IF() {
		INTCON |= 0x4;
		interrupt_WakeUpTest();
	}

	// boolean to proof if RB7:4 changed kann per software aber auch via read
	// gecleart werden, deshalb boolean
	public static void setRBIF(boolean b) {
		if (b) {
			INTCON |= 0x1;
		} else {
			INTCON &= ~0x1; // nur das erste bit wird gecleart
		}
		interrupt_WakeUpTest();
	}

	public static void setEEIF() {
		EECON1 = Util.setBit(EECON1, 4);
		gui.updateSFR(getSFRData());
	}

	public static void set_TO(boolean b) {
		if (b) {
			STATUS = Util.setBit(STATUS, 4);
		} else {
			STATUS = Util.clearBit(STATUS, 4);
		}

	}

	public static void set_PD(boolean b) {
		if (b) {
			STATUS = Util.setBit(STATUS, 3);
		} else {
			STATUS = Util.clearBit(STATUS, 3);
		}

	}

	public static boolean isT0CS() {
		return (OPTION & 0x20) != 0;
	}

	public static boolean isPSA() {
		return (OPTION & 0x8) != 0;
	}

	public static int get_PSbits() {
		int bits = OPTION & 0x07;

		if (!isPSA()) {
			bits++; // = (int) Math.pow(bits, 2);
		}

		return bits;
	}

	public static int[] getGPRData() {
		return bank;
	}

	public static List<SFRColumn> getSFRData() {
		List<SFRColumn> list = new ArrayList<>();
		list.add(new SFRColumn("W_REG", Special_Memory.getWReg(), true));
		list.add(new SFRColumn("STATUS", new String[] { "IRP", "RP1", "RP0", "TO", "PD", "Z", "DC", "C" }));
		list.add(new SFRColumn("", STATUS, true));
		list.add(new SFRColumn("INTCON",
				new String[] { "GIE", "EEIE", "T0IE", "INTE", "RBIE", "T0IF", "INTF", "RBIF" }));
		list.add(new SFRColumn("", INTCON, true));
		list.add(
				new SFRColumn("OPTION", new String[] { "RBPU", "INTEDG", "T0CS", "T0SE", "PSA", "PS2", "PS1", "PS0" }));
		list.add(new SFRColumn("", OPTION, true));
		list.add(new SFRColumn("EECON1", new String[] { "", "", "", "EEIF", "WRERR", "WREN", "WR", "RD" }));
		list.add(new SFRColumn("", EECON1, true));
		list.add(new SFRColumn("TMR0", TMR0, true));
		list.add(new SFRColumn("PCL", PCL, true));
		list.add(new SFRColumn("PCLATH", PCLATH, true));
		list.add(new SFRColumn("FSR", FSR, true));
		list.add(new SFRColumn("PORTA", Port_A.getInternalPORTA(), true));
		list.add(new SFRColumn("TRISA", Port_A.getTRISA(), true));
		list.add(new SFRColumn("PORTB", Port_B.getInternalPORTB(), true));
		list.add(new SFRColumn("TRISB", Port_B.getTRISB(), true));
		list.add(new SFRColumn("EEDATA", EEDATA, true));
		list.add(new SFRColumn("EEADR", EEADR, true));
		list.add(new SFRColumn("PC", getPC13Bit(), false));
		return list;
	}

	public static List<Value_Pair<Boolean[]>> getPortsData() {
		List<Value_Pair<Boolean[]>> list = new ArrayList<>();
		list.add(new Value_Pair<Boolean[]>("LED PortA", Port_A.getLEDs()));
		list.add(new Value_Pair<Boolean[]>("LED PortB", Port_B.getLEDs()));
		return list;
	}

}
