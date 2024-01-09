package gui.sfr;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SFRView extends TableView<SFRColumn> {

	public SFRView() {
		// Create column RowHeader (Data type of String).
		TableColumn<SFRColumn, String> columnName = new TableColumn<SFRColumn, String>("SFR");
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnName.setResizable(false); // cannot resize column anymore
		columnName.setPrefWidth(50);
		this.getColumns().add(columnName); // this??

		// Column for bits 0 till 7
		for (int i = 0; i < 8; i++) {
			// Create column for BitValues (Data type of String).
			TableColumn<SFRColumn, String> column = new TableColumn<SFRColumn, String>("" + (7 - i));
			// every cell calls a getter class to get value
			// i = index of row

			column.setCellValueFactory(new PropertySFRArrayValueFactory(7 - i));

			this.getColumns().add(column);
			column.setResizable(false);
			column.setPrefWidth(40);
		}

		// Dividing cell empty
		for (int i = 0; i < 2; i++) {
			TableColumn<SFRColumn, String> emptyColumn = new TableColumn<SFRColumn, String>("");
			emptyColumn.setResizable(false);
			emptyColumn.setPrefWidth(5);
			this.getColumns().add(emptyColumn);
		}

		// Create Column for hex
		TableColumn<SFRColumn, String> hexColumn = new TableColumn<SFRColumn, String>("HEX");
		hexColumn.setCellValueFactory(new PropertyValueFactory<>("hex")); // through "hex" it calls SFRRow.getHex()
		hexColumn.setResizable(false);
		hexColumn.setPrefWidth(30);
		this.getColumns().add(hexColumn);

		// Dividing Cell empty
		TableColumn<SFRColumn, String> emptyColumn = new TableColumn<SFRColumn, String>("");
		emptyColumn.setResizable(false);
		emptyColumn.setPrefWidth(5);
		this.getColumns().add(emptyColumn);

		// Create Column for dec
		TableColumn<SFRColumn, String> decColumn = new TableColumn<SFRColumn, String>("DEC");
		decColumn.setCellValueFactory(new PropertyValueFactory<>("dec")); // through "dec" it calls SFRRow.getDec()
		decColumn.setResizable(false);
		decColumn.setPrefWidth(30);
		this.getColumns().add(decColumn);

	}

	// Get data from SFR in Data_Memory
	public void updateView(List<SFRColumn> sfrData) {
		
		// List wrapper class
		// just Platform/Application thread is allowed to set Data in GUI
		// Application needs Runnable Object so no other thread can manipulate GUI
		Platform.runLater(new SFRHandlerUpdate(this, sfrData));

	}

	private class SFRHandlerUpdate implements Runnable {
		private SFRView sfrView;
		private List<SFRColumn> list;

		public SFRHandlerUpdate(SFRView sfrView, List<SFRColumn> list) {
			this.sfrView = sfrView;
			this.list = list;
		}

		// Adapter for method Platform.runLater(..);
		public void run() {
			sfrView.setItems(FXCollections.observableArrayList(list));
		}
	}

}
