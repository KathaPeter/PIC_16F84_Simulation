package gui.switches;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import pic.PIC_16F84;

public class CheckBoxHelper implements Callback<TableColumn<SwitchRow, Boolean>, TableCell<SwitchRow, Boolean>> {

	private int columnI;
	private List<ObservableValue<Boolean>> column = new ArrayList<>();
	private PIC_16F84 pic;

	public CheckBoxHelper(int i, PIC_16F84 pic) {
		this.columnI = i;
		this.pic = pic;
	}

	@Override
	public TableCell<SwitchRow, Boolean> call(TableColumn<SwitchRow, Boolean> param) {
		
		if (column.size() <= 0) {
			column.add(new SimpleBooleanProperty(false));
			column.add(new SimpleBooleanProperty(false));

			column.get(0).addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					pic.doUpdatePinA(newValue, columnI);
				}
			});

			column.get(1).addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					pic.doUpdatePinB(newValue, columnI);
				}
			});
		}

		return CheckBoxTableCell.<SwitchRow, Boolean> forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(Integer row) {
				return column.get(row);
			}
		}, true).call(null);
	}

}
