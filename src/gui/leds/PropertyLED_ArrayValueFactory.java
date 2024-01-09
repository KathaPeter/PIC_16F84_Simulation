package gui.leds;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

//Declaration of Callback<>
public class PropertyLED_ArrayValueFactory implements Callback<TableColumn.CellDataFeatures<LEDColumn, Boolean>, ObservableValue<Boolean>> {

	private int index;

	public PropertyLED_ArrayValueFactory(int i) {
		this.index = i;
	}

	//Definition of Callback<>
	@Override
	public ObservableValue<Boolean> call(CellDataFeatures<LEDColumn, Boolean> param) {
		
		//Call get hex value for column i
		LEDColumn value = param.getValue();
		return new SimpleBooleanProperty(value.getDisplay(index));
	} 
	
	

}
