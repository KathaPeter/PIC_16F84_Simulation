package gui.stack;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StackView extends TableView<StackColumn> {

	public StackView() {
		// Create column RowHeader (Data type of String) for INDEX.
		TableColumn<StackColumn, String> columnName = new TableColumn<StackColumn, String>("STACK");
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnName.setResizable(false); // cannot resize column anymore
		columnName.setPrefWidth(40);
		this.getColumns().add(columnName);

		// Create Column for value
		TableColumn<StackColumn, String> valueColumn = new TableColumn<StackColumn, String>("VALUE");
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value")); // through "value" it calls
																				// StackColumn.getValue()
		valueColumn.setResizable(false);
		valueColumn.setPrefWidth(40);
		this.getColumns().add(valueColumn);
		
		this.setPrefWidth(112);
	}

	// Get data from SFR in Data_Memory
	public void updateView(int[] stackData, int pos) {
		List<StackColumn> list = new ArrayList<>();

		for (int i = 0; i < stackData.length; i++) {
			list.add(new StackColumn(i + "", "" + stackData[i]));
		}

		// List wrapper class
		// just Platform/Application thread is allowed to set Data in GUI
		// Application needs Runnable Object so no other thread can manipulate GUI
		Platform.runLater(new STACKHandlerUpdate(this, list, pos));

	}

	class STACKHandlerUpdate implements Runnable {
		private StackView stackView;
		private List<StackColumn> list;
		private int pos;

		public STACKHandlerUpdate(StackView stackView, List<StackColumn> list, int pos) {
			this.stackView = stackView;
			this.list = list;
			this.pos = pos;
		}

		// Adapter for method Platform.runLater(..);
		public void run() {
			stackView.setItems(FXCollections.observableArrayList(list));
			stackView.getSelectionModel().clearAndSelect(pos);
		}
	}

}
