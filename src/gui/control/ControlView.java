package gui.control;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pic.PIC_16F84;

public class ControlView extends VBox {

	private VBox boxPicControl;
	private TextField tfTSim;
	private Button btnRUN;
	private Button btnSTEP;
	private Button btnMCLR;
	private Button btnWDTEnable;
	private PIC_16F84 pic;
	private Button btnPower;
	private Stage stage;

	public ControlView(PIC_16F84 pic, Stage stage) {

		this.pic = pic;
		this.stage = stage;
		btnPower = new Button("POWER On/Off");
		boxPicControl = new VBox();
		TitledPane panePicControl = new TitledPane("PIC-Controll", boxPicControl);
		btnWDTEnable = new Button("WDT Enable");
		btnMCLR = new Button("MCLR");
		btnSTEP = new Button("Step");
		btnRUN = new Button("Run");
		tfTSim = new TextField("" + pic.getTSimMilliSec());
		tfTSim.setAlignment(Pos.BASELINE_RIGHT);

		panePicControl.setCollapsible(false);
		panePicControl.setVisible(true);

		// Size button
		// btnPower.setPadding(new Insets(30));
		btnWDTEnable.setPadding(new Insets(5));
		btnMCLR.setPadding(new Insets(5));
		btnSTEP.setPadding(new Insets(5));
		btnRUN.setPadding(new Insets(5));

		boxPicControl.setSpacing(8);
		boxPicControl.setMinHeight(123);

		Label lbTSim = new Label("TSim [ms]:");
		lbTSim.setStyle("-fx-alignment:center-left");
		lbTSim.setMinHeight(25);

		HBox hBoxTSim = new HBox();
		hBoxTSim.getChildren().addAll(lbTSim, tfTSim);
		hBoxTSim.setSpacing(8);

		this.setSpacing(8);// Space between elements
		this.getChildren().addAll(btnPower, btnWDTEnable, hBoxTSim, panePicControl);

		btnPower.setStyle("-fx-font-size: 2em;");
		btnPower.setText("ON");
		btnPower.setMinWidth(150);
		btnPower.setMinHeight(50);
		tfTSim.setMaxWidth(85);
		btnWDTEnable.setMinWidth(150);

		btnMCLR.setMinWidth(130 - 3);
		btnRUN.setMinWidth(130 - 3);
		btnSTEP.setMinWidth(130 - 3);

		tfTSim.setOnAction(event -> {
			int newTSimMilliSec = pic.getTSimMilliSec();

			try {
				newTSimMilliSec = Integer.valueOf(tfTSim.getText());
			} catch (Exception e) {

			}

			tfTSim.setText(newTSimMilliSec + "");
			pic.setTSimMilliSec(newTSimMilliSec);
			pic.updateGUI(false);
			updateButtons();
		});

		btnPower.setOnAction(event -> {

			if (!pic.isLSTLoaded()) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Lade LST-File!");
				alert.showAndWait();
			} else {
				pic.doTogglePower();
			}

			updateButtons();
		});

		btnSTEP.setOnAction(event -> {
			pic.doOneStep();
			updateButtons();
		});

		btnRUN.setOnAction(event -> {
			pic.doToggleRun();
			updateButtons();
		});

		btnMCLR.setOnAction(event -> {
			pic.doMCLR();
			updateButtons();
		});

		btnWDTEnable.setOnAction(event -> {
			pic.doToggleWDTE();
			updateButtons();
		});
	}

	public void updateButtons() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				boolean bPower = pic.isPower();
				boolean bRun = pic.isRun();
				boolean bWDTE = pic.isWDTE();
				boolean bSleep = pic.isInSleep();

				boxPicControl.getChildren().clear();
				if (bPower) {
					boxPicControl.getChildren().addAll(btnRUN, btnSTEP, btnMCLR);
				}

				tfTSim.setText(pic.getTSimMilliSec()+"");
				tfTSim.setDisable(bPower);
				btnPower.setText(bPower ? ("Power OFF") : ("Power ON"));
				
				btnRUN.setText(bRun ? ("Idle") : ("Run"));
				btnWDTEnable.setText(bWDTE ? ("Disable WDT") : ("Enable WDT"));
				btnWDTEnable.setDisable(bPower);

				String state = "OFF";
				if (bPower) {
					state = "Idle";
					if (bRun) {
						state = "Running";
					}
				}

				stage.setTitle(
						"PIC_16F84 - PIC: " + (state) + (" - WDT: " + (bWDTE ? ("Enabled") : ("Disabled")) + " - TSim: " + (pic.getTSimMilliSec()) + "ms - SLEEP: "+((bSleep)?("Yes"):("No"))));
			}
		});

	}
}
