package gui.sfr;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class PropertySFRArrayValueFactory implements Callback<CellDataFeatures<SFRColumn, String>, ObservableValue<String>> {

	private int index;

	public PropertySFRArrayValueFactory(int i) {
		index = i;
	}

	@Override
	public ObservableValue<String> call(CellDataFeatures<SFRColumn, String> param) {
		// SimpleObjectProperty<> cause ObservableValue need this as return type
		return new SimpleObjectProperty<>( param.getValue().getBit(index)); // get row
	}
}
