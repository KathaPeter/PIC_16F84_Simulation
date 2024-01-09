package gui.menu;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gui.lst.LSTView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import pic.PICSimException;
import pic.PIC_16F84;

public class LSTMenu extends MenuBar {

	private static List<String> lstFilePaths = new ArrayList<>();

	static {
		// init list -> Interface DirectoryStream loads/filters with Paths.get(..lsts..) only .LST-files
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("./lsts/"))) {
			for (Path path : directoryStream) {
				// insert parsed .LST-file names to String into List lstFilePaths
				lstFilePaths.add(path.toString());
			}
		} catch (IOException ex) {
		}
	}

	public LSTMenu(PIC_16F84 pic, LSTView lstview) {

		Menu menu = new Menu("Load File");

		Menu mOptions = new Menu("Options");

		for (Iterator<String> iterator = lstFilePaths.iterator(); iterator.hasNext();) {

			String lstFilePath = iterator.next();

			MenuItem add = new MenuItem(lstFilePath.substring(7)); // 7 means without ./lsts/

			add.setOnAction(new LoadHandler(lstFilePath, pic, lstview));

			menu.getItems().addAll(add);
		}

		MenuItem resetEEPROM = new MenuItem("Reset EEPROM"); // 7 means without ./lsts/

		resetEEPROM.setOnAction(event -> {
			try {
				pic.doResetEEPROM();
			} catch (PICSimException e) {

				//ALERT for reset EEPROM if pic is power ON
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		});

		mOptions.getItems().addAll(resetEEPROM);

		this.getMenus().add(menu);
		this.getMenus().add(mOptions);
	}

	class LoadHandler implements EventHandler<ActionEvent> {

		private String lstFilePath;
		private PIC_16F84 pic;
		private LSTView lstview;

		public LoadHandler(String lstFilePath, PIC_16F84 pic, LSTView lstview) {
			this.lstFilePath = lstFilePath;
			this.pic = pic;
			this.lstview = lstview;
		}

		@Override
		public void handle(ActionEvent event) {
			try {
				// just load file on event
				pic.doLoadFile(lstFilePath);
				lstview.updateLST(pic.getLST(), lstFilePath);
			} catch (PICSimException e) {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}

	}

}
