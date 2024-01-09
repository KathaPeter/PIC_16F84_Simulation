package gui.osci;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import pic.PIC_16F84;

public class OSCIView extends HBox {

	private Label lbRuntime;

	public OSCIView(PIC_16F84 pic) {

		lbRuntime = new Label("Runtime: <00:00:00,000.000s>");
		this.getChildren().add(lbRuntime);
		lbRuntime.setStyle(
				"-fx-alignment:center-left; -fx-font-family:monospaced;-fx-font-size:20px; -fx-font-weight: bold;");
		lbRuntime.setPrefHeight(25);
		lbRuntime.setPrefWidth(400);

		updateRuntime(0);
	}

	public void updateRuntime(long lRuntimeMicroSec) {

		//final Duration d = Duration.of(lRuntimeMicroSec, ChronoUnit.MICROS);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				long millis = lRuntimeMicroSec / 1000L;
				long seconds = millis / 1000L;
				long minutes = seconds / 60L;
				long hours = minutes / 60L;

				lbRuntime.setText(String.format("     Runtime: <%02d:%02d:%02d,%03d.%03ds>", hours % 24, minutes % 60,
						seconds % 60, millis % 1000, lRuntimeMicroSec % 1000));

			}
		});

	}

}
