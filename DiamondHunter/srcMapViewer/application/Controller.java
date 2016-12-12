package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
	private static Stage stage;
	private static Model mapEditorModel;
	
	@FXML private MenuItem loadTilesetButton;
	@FXML private MenuItem loadMapButton;
	@FXML private MenuItem saveMapButton;
	@FXML private MenuItem closeButton;
	@FXML private ToggleButton axeButton;
	@FXML private ToggleButton boatButton;
	@FXML private GridPane mapGrid;
	@FXML private Label coordLabel;
	@FXML private Label blockedLabel;
	private FileChooser tileFileChooser = new FileChooser();
	private FileChooser mapFileChooser = new FileChooser();
	private FileChooser itemFileChooser = new FileChooser();
	
	ImageView axeIcon = new ImageView();
	ImageView boatIcon = new ImageView();

	public static void setModel(Model model){ mapEditorModel = model; }
	public static void setStage(Stage newStage){ stage = newStage; }
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mapEditorModel.setController(this);
		
		confirmSuccessfulLoad();

		initTileFileChooser();
		initSaveMapChooser();
		initMapFileChooser();

		loadDefaultMap();
		mapEditorModel.loadItems();
		
		initIcons();
	}
	
	//FXML Methods
	
	@FXML
	private void handleLoadingTileset(ActionEvent event){	
		File tileFile = tileFileChooser.showOpenDialog(stage);
		if(tileFile != null){
			mapEditorModel.loadTiles(tileFile);
			loadMapButton.setDisable(false);
		}
	}
	
	@FXML
	private void saveItemMap(ActionEvent event){
		File itemFile = itemFileChooser.showSaveDialog(stage);
		if(itemFile != null) mapEditorModel.saveItemMap(itemFile.getAbsolutePath());
	}
	
	@FXML
	private void handleLoadingMap(ActionEvent event){
		File mapFile = mapFileChooser.showOpenDialog(stage);
		if(mapFile != null){
			mapEditorModel.loadMap(mapFile);	
			mapEditorModel.createMapGrid(mapGrid);
		}
	}
	
	@FXML private void axeToggled(ActionEvent event){
		if(axeButton.isSelected()){
			mapEditorModel.setItem(Model.AXE);
		}else
			mapEditorModel.setItem(Model.EMPTY);
	}

	@FXML private void boatToggled(ActionEvent event){
		if(boatButton.isSelected())
			mapEditorModel.setItem(Model.BOAT);
		else
			mapEditorModel.setItem(Model.EMPTY);
	}
	
	@FXML private void exitApplication(ActionEvent event){ System.exit(0); }
	
	//Non-FXML Methods
	
	private void confirmSuccessfulLoad(){
		try {
			if(loadTilesetButton == null) 	throw new Exception("Load Tileset button was not injected!");
			if( loadMapButton == null) 		throw new Exception( "Load Map button was not injected!");
			if( saveMapButton == null) 		throw new Exception( "Save Map Button was not injected!");
			if( closeButton == null)		throw new Exception( "Close Button was not injected!");
			if( axeButton == null) 			throw new Exception( "Axe Button was not injected!");
			if( boatButton == null) 		throw new Exception( "Boat Button was not injected!");		
			if( mapGrid == null) 			throw new Exception( "Map Grid was not injected!");
			if( coordLabel  == null) 		throw new Exception( "Coordinate Label was not injected!");
			if( blockedLabel  == null) 		throw new Exception( "Blocked Label was not injected!");
			if( tileFileChooser  == null) 	throw new Exception( "Tile File Chooser was not created!");
			if( mapFileChooser  == null) 	throw new Exception( "Map File Chooser was not created!");
			if( itemFileChooser  == null) 	throw new Exception( "Item File Chooser was not created!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateCoordinates(){
		coordLabel.setText("X: " + mapEditorModel.getCurrentX() + " Y: " + mapEditorModel.getCurrentY());
	}
	
	public void loadDefaultMap(){
		mapEditorModel.loadDefaultMap();
		mapEditorModel.createMapGrid(mapGrid);
	}
	
	private void initTileFileChooser(){
		tileFileChooser.setTitle("Open Tileset");
		tileFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));
	}
	
	private void initSaveMapChooser(){
		itemFileChooser.setTitle("Save Item Map");
		itemFileChooser.setInitialFileName("My Item Map");
		itemFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Item Map", "*.itm"));
	}
	
	private void initMapFileChooser(){
		mapFileChooser.setTitle("Open Map");
		mapFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Map", "*.map"));
	}

	public void updateIsBlocked(boolean isBlocked) {
		if (isBlocked)  blockedLabel.setText("Blocked");
		else 			blockedLabel.setText("Unblocked");
	}
	
	private void initIcons(){
		axeIcon.setImage(SwingFXUtils.toFXImage(Model.ITEMS[1][Model.AXE], null));
		boatIcon.setImage(SwingFXUtils.toFXImage(Model.ITEMS[1][Model.BOAT], null));
		
		axeButton.setGraphic(axeIcon);
		boatButton.setGraphic(boatIcon);
	}
	
	public void updateHasItem(int item){
		switch(item){
			case Model.EMPTY:blockedLabel.setText(blockedLabel.getText() + " - has no items"); 		break;
			case Model.AXE:  blockedLabel.setText(blockedLabel.getText() + " - has Axe"); 			break;
			case Model.BOAT: blockedLabel.setText(blockedLabel.getText() + " - has Boat"); 			break;
			case Model.BOTH: blockedLabel.setText(blockedLabel.getText() + " - has Axe and Boat"); 	break;
		}
	}

	public void displayItem(int xLoc, int yLoc) {
		if(mapEditorModel.getCurrentItem() == 1){
			axeButton.setGraphic(null);
			if(mapEditorModel.itemPlaced(1))
				mapGrid.getChildren().removeAll(axeIcon);
			mapGrid.add(axeIcon, xLoc, yLoc);
		} else if(mapEditorModel.getCurrentItem() == 0){
			boatButton.setGraphic(null);
			if(mapEditorModel.itemPlaced(0))
				mapGrid.getChildren().removeAll(boatIcon);
			mapGrid.add(boatIcon, xLoc, yLoc);
		}
	}
}
