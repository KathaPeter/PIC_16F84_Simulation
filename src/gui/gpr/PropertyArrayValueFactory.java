package gui.gpr;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;


public class PropertyArrayValueFactory implements Callback<TableColumn.CellDataFeatures<GPRColumn, String>, ObservableValue<String>> {

	private int index;

	public PropertyArrayValueFactory(int i) {
		this.index = i;
	}

	@Override
	public ObservableValue<String> call(CellDataFeatures<GPRColumn, String> param) {
		
		//Call get hex value for column i
		GPRColumn value = param.getValue();
		return new SimpleObjectProperty<>(value.getHex(index));
	} 
	
	

}
