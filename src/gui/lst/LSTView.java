package gui.lst;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import memory.guiutil.LSTLine;
import pic.PIC_16F84;

public class LSTView extends TableView<LSTRow> {

	private LSTView thiz;

	public LSTView(PIC_16F84 pic) {
		
		thiz = this;
		TableColumn<LSTRow, String> columnIndex = new TableColumn<>("Index");
		columnIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
		this.getColumns().add(columnIndex);
		columnIndex.setResizable(false);
		columnIndex.setPrefWidth(35);

		TableColumn<LSTRow, Boolean> columnBreakpoint = new TableColumn<>("Breakpoint");
		this.getColumns().add(columnBreakpoint);
		columnBreakpoint.setResizable(false);
		columnBreakpoint.setPrefWidth(75);

		TableColumn<LSTRow, String> columnLSTLine = new TableColumn<>("Command");
		columnLSTLine.setCellValueFactory(new PropertyValueFactory<>("lstline"));
		columnLSTLine.getStyleClass().add("my-special-column-style");
		this.getColumns().add(columnLSTLine);
		columnLSTLine.setResizable(false);
		columnLSTLine.setPrefWidth(120);

		TableColumn<LSTRow, String> columnLSTLineOrig = new TableColumn<>("LST");
		columnLSTLineOrig.setCellValueFactory(new PropertyValueFactory<>("lstlineorig"));
		this.getColumns().add(columnLSTLineOrig);
		columnLSTLineOrig.getStyleClass().add("my-special-column-style");
		columnLSTLineOrig.setMinWidth(500);
		columnLSTLineOrig.setResizable(true);

		TableColumn<LSTRow, String> columnN = new TableColumn<>("LST");
		this.getColumns().add(columnN);
		columnN.setPrefWidth(10);
		columnN.setResizable(false);

		columnBreakpoint.setCellValueFactory(new Callback<CellDataFeatures<LSTRow, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<LSTRow, Boolean> param) {
				LSTRow person = param.getValue();

				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isBreakpoint());

				// Note: singleCol.setOnEditCommit(): Not work for
				// CheckBoxTableCell.

				// When "Single?" column change.
				booleanProp.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						person.setBreakpoint(newValue);
						
						//Collect Breakpoints list from table
						List<Integer> list = new ArrayList<Integer>();
						for (LSTRow cmd : thiz.getItems()) {
							if ( cmd.isBreakpoint() ) {
								list.add(Integer.valueOf(cmd.getIndex()));
							}
						}
						
						//set breakpoints
						pic.doSetBreakpoints(list);
					}
				});
				return booleanProp;
			}
		});

		columnBreakpoint.setCellFactory(new Callback<TableColumn<LSTRow, Boolean>, //
				TableCell<LSTRow, Boolean>>() {
			@Override
			public TableCell<LSTRow, Boolean> call(TableColumn<LSTRow, Boolean> p) {
				CheckBoxTableCell<LSTRow, Boolean> cell = new CheckBoxTableCell<LSTRow, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		
		this.setEditable(true);
	}

	public void updateLST(List<LSTLine> data, String lstfilepath) {
		List<LSTRow> list = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			LSTLine lstLine = data.get(i);
			list.add(new LSTRow(i, lstLine.getLine(), lstLine.getOrigLine()));
		}

		// List wrapper class
		Platform.runLater(new LSTHandlerUpdate(this, list, lstfilepath));

	}

	class LSTHandlerUpdate implements Runnable {
		private LSTView lstView;
		private List<LSTRow> list;
		private String lstfilepath;

		public LSTHandlerUpdate(LSTView lstView, List<LSTRow> list, String lstfilepath) {
			this.lstView = lstView;
			this.list = list;
			this.lstfilepath = lstfilepath;
		}

		// Adapter for method Platform.runLater(..);
		public void run() {
			lstView.setItems(FXCollections.observableArrayList(list));
			lstView.getColumns().get(3).setText(lstfilepath.substring(7));
		}
	}

	public void updateLSTMarker(final int pc13Bit) {
		final LSTView thiz = this;
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				//select tablerow
				thiz.getSelectionModel().clearAndSelect(pc13Bit);
				
				//make selected row visible in table (Scoll)
				thiz.scrollTo(Math.max(pc13Bit - 6, 0));
			}
		});

	}
}
