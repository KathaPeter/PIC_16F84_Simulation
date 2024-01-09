package memory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class EEPROM {

	// p. 33 address < 0x40
	private static List<Integer> fileAsList = new ArrayList<>(0x40);

	static {
		for (int i = 0; i < 0x40; i++) {
			fileAsList.add(12);
		}
	}

	public static boolean write() {
		if (EECON2.EECON2_WR_ENABLE) {
			if (Data_Memory.isEECON1_WR()) {

				importFile();

				int address8Bit = Data_Memory.getEEADR();
				int data8Bit = Data_Memory.getEEDATA();

				// p. 33 address < 0x40
				fileAsList.set(address8Bit & 0x3F, data8Bit);

				exportFile();

				
				//Simulated Delay of writing to EEPROM
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						EECON2.EECON2_WR_ENABLE = false;
						//Clear and set bits to signal finish of write into EEPROM
						Data_Memory.clearEECON1_WR();
						Data_Memory.setEEIF();
					}
				}).start();
				
				return true;
			}
		}

		return false;
	}

	public static boolean read() {
		if (Data_Memory.isEECON1_RD()) {

			importFile();

			int address8Bit = Data_Memory.getEEADR();
			int data8Bit = 0;

			// p. 33 address < 0x40
			data8Bit = fileAsList.get(address8Bit & 0x3F);

			Data_Memory.setEEDATA(data8Bit);
			Data_Memory.clearEECON1_RD();

			return true;
		}

		return false;
	}

	private static void importFile() {

		try (BufferedReader newBufferedReader = Files.newBufferedReader(getPath())) {

			for (String text = newBufferedReader.readLine(); text != null; text = newBufferedReader.readLine()) {
				String[] split = text.split(":");

				int index = Integer.valueOf(split[0].trim());
				int wert = Integer.valueOf(split[1].trim());

				fileAsList.set(index, wert);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Path getPath() {
		return Paths.get("eeprom.txt");
	}

	private static void exportFile() {

		clearFile();

		try (BufferedWriter newBufferedWriter = Files.newBufferedWriter(getPath(), StandardOpenOption.CREATE)) {

			for (int i = 0; i < fileAsList.size(); i++) {

				int wert = fileAsList.get(i);
				String sIndex = (i < 10 ? (" ") : ("")) + i;

				String text = " " + sIndex + " : " + wert + " " + System.lineSeparator();
				newBufferedWriter.write(text);

			}

			newBufferedWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void clearFile() {

		//CREATE FILE IF NOT EXIST
		try (BufferedWriter newBufferedWriter = Files.newBufferedWriter(getPath(), StandardOpenOption.CREATE)) {
			newBufferedWriter.write("");
			newBufferedWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//CLEAR FILE (TRUNCATE)
		try (BufferedWriter newBufferedWriter = Files.newBufferedWriter(getPath(), StandardOpenOption.TRUNCATE_EXISTING)) {
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void reset() {

		//Clear List in File
		for (int i = 0; i < fileAsList.size(); i++) {
			fileAsList.set(i, 0);
		}
		
		//Write List to File
		exportFile();
	}

}
