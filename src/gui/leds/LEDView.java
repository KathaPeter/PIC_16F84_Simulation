package gui.leds;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import memory.guiutil.Value_Pair;

public class LEDView extends TableView<LEDColumn> {

	public LEDView() {
		// Create column RowHeader (Data type of String).
		TableColumn<LEDColumn, String> columnName = new TableColumn<LEDColumn, String>("LED'S");
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnName.setResizable(false); // cannot resize column anymore
		columnName.setPrefWidth(55);
		this.getColumns().add(columnName);

		// Column for bits 0 till 7
		for (int i = 0; i < 8; i++) {
			// Create column for BitValues (Data type of String).
			TableColumn<LEDColumn, Boolean> column = new TableColumn<LEDColumn, Boolean>("" + (7 - i));
			// every cell calls a getter class to get value
			// i = index of row
			column.setCellValueFactory(new PropertyLED_ArrayValueFactory(7 - i));
			column.setCellFactory(te -> new CheckBoxTableCell<LEDColumn, Boolean>());
			this.getColumns().add(column);
			column.setResizable(false);
			column.setPrefWidth(20);
		}

	}

	public void updateView(List<Value_Pair<Boolean[]>> portsData) {
		List<LEDColumn> list = new ArrayList<>();

		for (Value_Pair<Boolean[]> elem : portsData) {
			list.add(new LEDColumn(elem.getName(), elem.getValue()));
		}

		// List wrapper class

		Platform.runLater(new LEDHandlerUpdate(this, list));

	}

	class LEDHandlerUpdate implements Runnable {
		private LEDView portView;
		private List<LEDColumn> list;

		public LEDHandlerUpdate(LEDView portView, List<LEDColumn> list) {
			this.portView = portView;
			this.list = list;
		}

		// Adapter for method Platform.runLater(..);
		public void run() {
			portView.setItems(FXCollections.observableArrayList(list));
		}
	}
}
