package gui.gpr;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GPRView extends TableView<GPRColumn> {

	public GPRView() {
		//Creates title for table and header for 1st column with Data type of String.
		TableColumn<GPRColumn, String> title = new TableColumn<GPRColumn, String>("xXX_GPR");
		//Gets name into column cells xXX_GPR
		title.setCellValueFactory(new PropertyValueFactory<>("name"));	
		this.getColumns().add(title);
		title.setResizable(false);
		title.setPrefWidth(55);

		for (int i = 0; i < 16; i++) {
			// Creates column for member hexValues[] (in class GPRColumn) with Data type of String.
			TableColumn<GPRColumn, String> column = new TableColumn<GPRColumn, String>("0" + Integer.toHexString(i));
			// Getter class value of array index i
			column.setCellValueFactory(new PropertyArrayValueFactory(i));

			this.getColumns().add(column);
			column.setResizable(false);
			column.setPrefWidth(20);
		}
	}

	public void updateView(int[] gprData) {
		List<GPRColumn> list = new ArrayList<>();

		for (int i = 0; i < gprData.length/16; i++) {
			list.add(new GPRColumn(Integer.toHexString(i) + "0", i, gprData));
		}

		// List wrapper class
		Platform.runLater(new GPRHandlerUpdate(this, list));
	}

	class GPRHandlerUpdate implements Runnable {
		private GPRView gprView;
		private List<GPRColumn> list;

		public GPRHandlerUpdate(GPRView gprView, List<GPRColumn> list) {
			this.gprView = gprView;
			this.list = list;
		}

		// Adapter for method Platform.runLater(..);
		public void run() {
			gprView.setItems(FXCollections.observableArrayList(list));
		}
	}
}
