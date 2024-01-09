
package client;

import java.util.List;

import gui.IGui;
import gui.control.ControlView;
import gui.gpr.GPRView;
import gui.leds.LEDView;
import gui.lst.LSTView;
import gui.menu.LSTMenu;
import gui.osci.OSCIView;
import gui.sfr.SFRColumn;
import gui.sfr.SFRView;
import gui.stack.StackView;
import gui.switches.SwitchView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import memory.guiutil.Value_Pair;
import pic.PIC_16F84;

public class Program2 extends Application implements IGui {

	private GPRView gprview;
	private ControlView controlView;
	private SFRView sfrview;
	private StackView stackview;
	private LEDView portview;
	private SwitchView switchview;
	private LSTView lstview;
	private PIC_16F84 pic = null;
	private LSTMenu menuBar = null;
	private OSCIView osciview;

	@Override
	public void start(Stage stage) {

		// Title of window
		stage.setTitle("PIC_16F84");

		// Instantiates main_loop and wdt_loop
		pic = new PIC_16F84(this);

		// Instantiates Table_Columns for GPR_Memory, SFR_Register, Stack_Ringbuffer
		gprview = new GPRView();
		sfrview = new SFRView();
		stackview = new StackView();
		portview = new LEDView();
		switchview = new SwitchView(pic);
		lstview = new LSTView(pic);
		osciview = new OSCIView(pic);

		controlView = new ControlView(pic, stage);
		menuBar = new LSTMenu(pic, lstview);

		sfrview.setPrefWidth(465);
		gprview.setPrefWidth(377);
		stackview.setPrefWidth(83);
		portview.setPrefWidth(218);
		switchview.setPrefWidth(217);
		lstview.setPrefWidth(770);
		
		

		setSortableFalse(sfrview, gprview, stackview, portview, switchview, lstview);

		lstview.getColumns().get(0).getStyleClass().add("first");
		sfrview.getColumns().get(0).getStyleClass().add("first");
		gprview.getColumns().get(0).getStyleClass().add("first");
		portview.getColumns().get(0).getStyleClass().add("first");
		switchview.getColumns().get(0).getStyleClass().add("first");
		stackview.getColumns().get(0).getStyleClass().add("first");

		// VBox to arrange all group_elements
		VBox root = new VBox();
		root.getChildren().addAll(menuBar, getTopLine(), getBottomLine());
		root.setPadding(new Insets(5));
		root.setSpacing(5);

		// Window size
		Scene scene = new Scene(root, 935, 740);
		scene.getStylesheets().add("stylesheet.css");

		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();

		stage.setOnCloseRequest(event -> {
			// Exit Programm totally (not just window)
			pic.doExit();
			Platform.exit();
		});

		controlView.updateButtons();
		pic.updateGUI(false);
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void updateGPR(int[] gprData) {
		gprview.updateView(gprData);
	}

	@Override
	public void updateSFR(List<SFRColumn> sfrData) {
		sfrview.updateView(sfrData);
	}

	@Override
	public void updateStack(int[] stackData, int pos) {
		stackview.updateView(stackData, pos);
	}

	@Override
	public void updatPorts(List<Value_Pair<Boolean[]>> portsData) {
		portview.updateView(portsData);
	}

	@Override
	public void updateLSTMarker(int pc13Bit) {
		lstview.updateLSTMarker(pc13Bit);
	}

	@Override
	public void updateRuntime(long lRuntimeMicroSec) {
		osciview.updateRuntime(lRuntimeMicroSec);
	}

	@Override
	public void eventStop() {
		controlView.updateButtons();
	}
	
	@Override
	public void sleepChangedEvent() {
		controlView.updateButtons();
	}

	public Node getTopLine() {
		// HBox for GPR_Memory, Stack_Memory and Control_Buttons
		HBox hBox = new HBox();
		hBox.setSpacing(5);
		hBox.getChildren().addAll(controlView, lstview);
		return hBox;
	}

	public Node getBottomLine() {
		// hBoxBottom
		HBox hBox = new HBox();
		hBox.getChildren().addAll(sfrview, getBottomRightLine());
		return hBox;
	}

	private Node getBottomRightLine() {
		VBox vBox = new VBox();
		vBox.getChildren().addAll(getBottomRightTop(), getBottomRightCenter(), osciview);
		return vBox;
	}

	private Node getBottomRightTop() {
		HBox hBox = new HBox();
		hBox.getChildren().addAll(portview, switchview);
		hBox.setPrefHeight(200);
		hBox.setSpacing(24);
		return hBox;
	}

	private Node getBottomRightCenter() {
		HBox hBox = new HBox();
		hBox.getChildren().addAll(gprview, stackview);
		return hBox;
	}

	private void setSortableFalse(TableView<?>... list) {
		for (TableView<?> tableView : list) {
			for (TableColumn<?, ?> tableColumn : tableView.getColumns()) {
				tableColumn.setSortable(false);
			
			}
		}

	}

	

}
