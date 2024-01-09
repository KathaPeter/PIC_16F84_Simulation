package gui;

import java.util.List;

import gui.sfr.SFRColumn;
import memory.guiutil.Value_Pair;

public interface IGui {

	void updateGPR(int[] gprData);

	void updateSFR(List<SFRColumn> sfrData);

	void updateStack(int[] stackData, int pos);

	void updatPorts(List<Value_Pair<Boolean[]>> portsData);

	void updateLSTMarker(int pc13Bit);

	void updateRuntime(long lRuntimeMicroSec);

	void eventStop();

	void sleepChangedEvent();

}
