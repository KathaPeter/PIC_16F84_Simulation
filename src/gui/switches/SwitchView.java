package gui.switches;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pic.PIC_16F84;

public class SwitchView extends TableView<SwitchRow> {

	public SwitchView(PIC_16F84 pic) {

		// Create column RowHeader (Data type of String).
		TableColumn<SwitchRow, String> columnName = new TableColumn<SwitchRow, String>("Switche's");
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnName.setResizable(false); // cannot resize column anymore
		columnName.setPrefWidth(55);
		this.getColumns().add(columnName);

		// Column for bits 0 till 7
		for (int i = 0; i < 8; i++) {
			// Create column for BitValues (Data type of String).
			TableColumn<SwitchRow, Boolean> column = new TableColumn<SwitchRow, Boolean>("" + (7 - i));
			// every cell calls a getter class to get value
			// i = index of row
			//column.setCellValueFactory(new PropertySwitchArrayValueFactory(7 - i));
			column.setCellFactory(new CheckBoxHelper(7 - i, pic));
			this.getColumns().add(column);
			column.setResizable(false);
			column.setPrefWidth(20);

			column.setEditable(true);

		}

		// init switches
		List<SwitchRow> list = new ArrayList<>();
		list.add(new SwitchRow("PINS A", new Boolean[8]));
		list.add(new SwitchRow("PINS B", new Boolean[8]));
		this.setItems(FXCollections.observableArrayList(list));

		/***************************************
		 * Make editable
		 ***************************************/
		this.setEditable(true);

	}

}
