/*******************************************************************************
 * Copyright (c) 2020 Haonan Huang.
 *
 *     This file is part of QuantumVITAS (Quantum Visualization Interactive Toolkit for Ab-initio Simulations).
 *
 *     QuantumVITAS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     QuantumVITAS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with QuantumVITAS.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 *******************************************************************************/
package main.java.app;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.MainClass;
import main.java.app.centerwindow.OutputViewerController;
import main.java.app.centerwindow.WorkScene3D;
import main.java.app.input.InputGeoController;
import main.java.app.input.InputMdController;
import main.java.app.input.InputOptController;
import main.java.app.input.InputScfController;
import main.java.app.menus.SettingsWindowController;
import main.java.com.consts.Constants.EnumCalc;
import main.java.com.consts.Constants.EnumStep;
import main.java.com.error.ErrorMsg;
import main.java.com.programconst.Coloring;
import main.java.com.programconst.DefaultFileNames.SettingKeys;
import main.java.input.ContainerInputString;
import main.java.job.JobNode;

public class MainWindowController implements Initializable{
	
	@FXML private BorderPane basePane;
	
    @FXML private Menu menuFile;
    
    @FXML private MenuButton calcMain;
    
    @FXML private MenuItem calcScf,
    calcOpt,
    calcDos,
    calcBands,
    calcMd,
    calcTddft,
    calcCustom,
    menuAbout,
    menuSaveProjectAs,
    menuLoadProject;
    
    @FXML private MenuItem stopCurrentJob,
    stopAllJobs,
    settingsMenuItem;
    
    @FXML private Button createProject,
    showInputButton,
    runJob,
    buttonOpenWorkSpace,
    saveProjectButton;
    
    @FXML private Label textWorkSpace;
    
    @FXML private Pane paneWorkSpace;
    
    @FXML private ComboBox<String> comboProject,
    comboCalculation;
    
	@FXML private ScrollPane inputField;
	
	@FXML private HBox hboxRight,
	hboxLeft;
	
	@FXML private Label calcLabel,
	currentJobLabel;
	
	@FXML private TabPane workSpaceTabPane;
	
	@FXML private RadioButton radioGeometry, 
	radioCalculation;
	
	@FXML private BorderPane rootPane;
	
	private ScrollPane scrollGeo,
	scrollOpt,
	scrollScf,
	scrollDos,
	scrollBands,
	scrollMd,
	scrollTddft;
	
	private ScrollPane scrollLeft;
	
	private BorderPane borderSettings;
	
	private TabPane tabPaneRight;
	
	private Boolean tabPaneStatusRight,
	scrollStatusLeft;
	
	private MainClass mainClass;
	
	private InputScfController contScf;
	
	private InputGeoController contGeo;
	
	private InputOptController contOpt;
	
	private InputMdController contMd;
			
	private MainLeftPaneController contTree;
	
	private SettingsWindowController contSettings;
	
	private HashMap<String, Tab> projectTabDict;
	
	private final ToggleGroup tgGroup = new ToggleGroup();
	
	private Thread thread1;
	
	private Integer tabRowNum;
	
	private OutputViewerController contOutput;
	
	private HBox hboxOutput;
			
	public MainWindowController(MainClass mc) {
		mainClass = mc;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1){
		workSpaceTabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);

		
		projectTabDict = new HashMap<String, Tab>();
		
		//set the style of workspace and QEEngine fields
		textWorkSpace.setBackground(new Background(new BackgroundFill(Coloring.defaultFile, 
				CornerRadii.EMPTY, Insets.EMPTY)));
		textWorkSpace.prefWidthProperty().bind(paneWorkSpace.widthProperty());
		textWorkSpace.prefHeightProperty().bind(paneWorkSpace.heightProperty());

		
		tabPaneRight = null;
		tabPaneStatusRight = false;
		
		
		
		// load all relevant panes and sub-panes
		try {
			contScf = new InputScfController(mainClass);
			FXMLLoader fxmlLoader1 = MainClass.getFxmlLoader("InputScf.fxml");
			fxmlLoader1.setController(contScf);
			scrollScf = fxmlLoader1.load();
			//contScf.initialize();//must be later than the load
			
			contGeo = new InputGeoController(mainClass);
			FXMLLoader fxmlLoader2 = MainClass.getFxmlLoader("InputGeo.fxml");
			fxmlLoader2.setController(contGeo);
			scrollGeo = fxmlLoader2.load();
			//contGeo.initialize();//must be later than the load
			
			
			contOpt = new InputOptController(mainClass);
			FXMLLoader fxmlLoader3 = MainClass.getFxmlLoader("InputOpt.fxml");
			fxmlLoader3.setController(contOpt);
			scrollOpt = fxmlLoader3.load();
			
			contMd = new InputMdController(mainClass);
			FXMLLoader fxmlLoader4 = MainClass.getFxmlLoader("InputMd.fxml");
			fxmlLoader4.setController(contMd);
			scrollMd = fxmlLoader4.load();
			
			FXMLLoader fxmlLoader5 = MainClass.getFxmlLoader("InputDos.fxml");
			scrollDos = fxmlLoader5.load();

			FXMLLoader fxmlLoader6 = MainClass.getFxmlLoader("InputBands.fxml");
			scrollBands = fxmlLoader6.load(); 

			FXMLLoader fxmlLoader7 = MainClass.getFxmlLoader("InputTddft.fxml");
			scrollTddft = fxmlLoader7.load(); 
			
			contTree = new MainLeftPaneController(mainClass);
			FXMLLoader fxmlLoaderTree = MainClass.getFxmlLoader("MainLeftPane.fxml");
			fxmlLoaderTree.setController(contTree);
			scrollLeft = fxmlLoaderTree.load();
			
			contSettings = new SettingsWindowController(mainClass);
			FXMLLoader fxmlLoaderSettings = MainClass.getFxmlLoader("settingsWindow.fxml");
			fxmlLoaderSettings.setController(contSettings);
			borderSettings = fxmlLoaderSettings.load();
			
			contOutput = new OutputViewerController(mainClass);
			FXMLLoader fxmlLoaderOutput = MainClass.getFxmlLoader("outputViewer.fxml");
			fxmlLoaderOutput.setController(contOutput);
			hboxOutput = fxmlLoaderOutput.load();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hboxOutput.prefWidthProperty().bind(workSpaceTabPane.widthProperty());
		hboxOutput.prefHeightProperty().bind(workSpaceTabPane.heightProperty());
		
		contTree.buttonOpenSelected.setOnAction((event) -> {
			String projName = contTree.getSelectedProject();
			if(projName==null || projName.isEmpty()) return;
			
			File wsDir = mainClass.projectManager.getWorkSpaceDir();
			
			if(wsDir==null || !wsDir.canRead()) {return;}
			
			String msg = mainClass.projectManager.loadProject(wsDir, projName);
			
			if(msg!=null) {
				if(msg.contains(ErrorMsg.alreadyContainsProject)) {
					Alert alert1 = new Alert(AlertType.INFORMATION);
			    	alert1.setTitle("Error");
			    	alert1.setContentText(msg);
			    	alert1.showAndWait();
			    	return;
		    	}
						
				if(msg.contains(ErrorMsg.cannotFindProjectFolder)) {
					contTree.updateProjects();
					Alert alert1 = new Alert(AlertType.INFORMATION);
			    	alert1.setTitle("Error");
			    	alert1.setContentText(msg);
			    	alert1.showAndWait();
					return;}
			}
			createProjectGui(projName);//loading GUI
			
			contTree.setOpenCloseButtons(false);
			
			if(msg!=null) {
				Alert alert1 = new Alert(AlertType.INFORMATION);
		    	alert1.setTitle("Info");
		    	alert1.setContentText(msg);
		    	alert1.showAndWait();
			}
		});
		contTree.buttonCloseSelected.setOnAction((event) -> {
			String projName = contTree.getSelectedProject();
			closeProject(projName);//should be null safe and empty safe
		});
		
		radioGeometry.setText("Geometry");
		radioGeometry.setToggleGroup(tgGroup);
		radioGeometry.setSelected(true);
		radioCalculation.setText("Calculation");
		radioCalculation.setToggleGroup(tgGroup);
		tgGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
			toggleGeometry();
		});
		
		setProjectNull();
		
		initializeLeftRightPane();//initialize tabPaneRight
		
		loadEnvironmentPaths();
		
		createProject.setOnAction((event) -> {
//			String oldProjectTemp = currentProject;
			
			TextInputDialog promptProjName = new TextInputDialog(); 
			String projName = null;
			String msg = null;
			promptProjName.setHeaderText("Enter the project name");
			do {
				Optional<String> result = promptProjName.showAndWait();
				if (result.isPresent()) {
					projName = promptProjName.getEditor().getText();
					msg = mainClass.projectManager.checkProjectName(projName);
					promptProjName.setHeaderText(msg);
				}else {
					return;
				}
			} while (msg!=null);
			
			File wsDir = mainClass.projectManager.getWorkSpaceDir();
			if(wsDir==null) return;
			File projDir = new File(wsDir,projName);
			
			if (projDir.exists()) {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Error");
		    	alert.setContentText("Project with the same name already existed in the workspace. Please try another name.");
		    	alert.showAndWait();
				return;
			}
			
			msg = mainClass.projectManager.addProject(projName);
			
			if (msg!=null) return;
			
			//set project tree
			contTree.addProject(projName);
			createProjectGui(projName);
		});
		workSpaceTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			//******main code for changing project********
			if (newTab==null) {
				setProjectNull();
			}
			else {
				mainClass.projectManager.setActiveProject(newTab.getText());
				
				
				updateWorkScene();
				
				comboProject.getSelectionModel().select(newTab.getText());
				//update calculation list
				comboCalculation.getItems().clear();
				//current calculation exists, so at least one calculation exists
				if (mainClass.projectManager.existCurrentCalc()) {
					ArrayList<String> al = mainClass.projectManager.getCurrentCalcList();
					for (String ec : al) {
						comboCalculation.getItems().add(ec);//******later, organize/sort the items
					}
					openCalc(mainClass.projectManager.getCurrentCalcName());
				}
				else {
					//******not the most efficient way, may run twice
					radioGeometry.setSelected(true);
					toggleGeometry();
				}
			}
	    });
		comboProject.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			if (newVal!=null) {
				//-------------********not the most efficient way*******---------------
				//will bounce between comboProject and workSpaceTabPane
				//no need to put the code executed when changing project again here!
				workSpaceTabPane.getSelectionModel().select(projectTabDict.get(newVal));
//				mainClass.projectManager.setActiveProject(newVal);
				openCalc(mainClass.projectManager.getCurrentCalcName());//openCalc is null tolerant
				
			}
	    });
		comboCalculation.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			if (newVal!=null && !newVal.isEmpty()) {
				mainClass.projectManager.setActiveCalculation(newVal);
				//-------------********not the most efficient way*******---------------
				openCalc(mainClass.projectManager.getCurrentCalcName());//openCalc is null tolerant
			}
	    });
		calcScf.setOnAction((event) -> {
			openCalc(EnumCalc.SCF,true);
		});
		calcOpt.setOnAction((event) -> {
			openCalc(EnumCalc.OPT,true);
		});
		calcDos.setOnAction((event) -> {
			openCalc(EnumCalc.DOS,true);
		});
		calcBands.setOnAction((event) -> {
			openCalc(EnumCalc.BANDS,true);
		});
		calcMd.setOnAction((event) -> {
			openCalc(EnumCalc.BOMD,true);
		});
		calcTddft.setOnAction((event) -> {
			openCalc(EnumCalc.TDDFT,true);
		});
		showInputButton.setOnAction((event) -> {
			ArrayList<ContainerInputString> cis = mainClass.projectManager.genInputFromAgent();
			
			if (cis!=null && cis.size()>0) {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Input");
		    	alert.setContentText(cis.size()+" steps in total. Show here the input for the first step:\n"+cis.get(0).toString());
		    	alert.showAndWait();
			}
			else {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Input");
		    	alert.setContentText("Cannot generate input file. Empty input file.");
		    	alert.showAndWait();
			}
		});
		
		//new thread listening to job status
		thread1 = new Thread() {
	        public void run() {
        		try {
		            while (!interrupted()) {        
	                    //sleep
	                    Thread.sleep(500);
		                
		                // update currentJobLabel on FX thread
		                Platform.runLater(new Runnable() {
		
		                    public void run() {
		                    	String st = mainClass.jobManager.getCurrentJobName();
		                    	if(st==null) {currentJobLabel.setText("Idle...");}
		                    	else {currentJobLabel.setText("Running: "+st);}
		                    }
		                });
		            }
        		} catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
	        }
        };
        thread1.start();
        
		stopCurrentJob.setOnAction((event) -> {
			mainClass.jobManager.stopCurrent();
		});
		stopAllJobs.setOnAction((event) -> {
			mainClass.jobManager.stopAll();
		});
		runJob.setOnAction((event) -> {
//			mainClass.jobManager.addNode(new JobNode(null,"notepad.exe"));
//			mainClass.jobManager.addNode(new JobNode("C:\\Program Files\\PuTTY\\","putty.exe"));
//			mainClass.jobManager.addNode(new JobNode(null,"notepad.exe"));
			//save project first
			File wsDir = mainClass.projectManager.getWorkSpaceDir();
			if(wsDir==null || !wsDir.canWrite()) {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Error");
		    	alert.setContentText("Cannot find the workspace directory when trying to run job.");
		    	alert.showAndWait();
		    	return;
	    	}
			//only save current calc, do not show successfully save window
			mainClass.projectManager.saveActiveProjectInMultipleFiles(wsDir,true,false);
			//get calculation directory
			File fl = mainClass.projectManager.getCalculationDir();
			if(fl==null || !fl.canWrite() || !fl.canRead()) {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Error");
		    	alert.setContentText("Cannot find the calculation directory when trying to run job.");
		    	alert.showAndWait();
		    	return;
			}

			//generate input file
			ArrayList<ContainerInputString> cis = mainClass.projectManager.genInputFromAgent();
			if(cis==null || cis.isEmpty()) {
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Error");
		    	alert.setContentText("No input file generated. Should not be like this! Abort...");
		    	alert.showAndWait();
		    	return;
			}

			for(int j = 0 ; j < cis.size() ; j++) {
				if(cis.get(j)==null || (cis.get(j).log!=null && !cis.get(j).log.isEmpty()) || cis.get(j).input==null) {
					Alert alert = new Alert(AlertType.INFORMATION);
			    	alert.setTitle("Warning");
			    	String stt = "Warning! Input file not complete for "+j+"th step. Please fix the following errors:\n";
			    	stt+=(cis.get(j).input==null? "Null input string.\n":"");
			    	alert.setContentText(cis.get(j)==null ? stt:(stt + cis.get(j).log));
			    	alert.showAndWait();
			    	return;
				}
				if(cis.get(j).stepName==null) {
					Alert alert = new Alert(AlertType.INFORMATION);
			    	alert.setTitle("Warning");
			    	alert.setContentText("Warning! EnumStep not set for "+j+"th step. Please check the code.");
			    	alert.showAndWait();
			    	return;
				}
				File calcFile = new File(fl,cis.get(j).stepName.toString()+".in");
				try {
		            Files.write(calcFile.toPath(), cis.get(j).input.getBytes());
		        } catch (IOException e) {
		        	Alert alert = new Alert(AlertType.INFORMATION);
			    	alert.setTitle("Error");
			    	alert.setContentText("Warning! Cannot write input file for "+j+"th step. Abort.");
			    	alert.showAndWait();
			    	return;
		        }
			}
			//start running the jobs
			for(int j = 0 ; j < cis.size() ; j++) {
				mainClass.jobManager.addNode(new JobNode(fl.getPath(),
						mainClass.projectManager.qePath+File.separator+"pw.exe",cis.get(j).stepName.toString()));
			}
			
			//just for test use
//	    	mainClass.jobManager.addNode(new JobNode(null,"notepad.exe"));
			
//			mainClass.jobManager.addNode(new JobNode(null,"notepad.exe"));
//			mainClass.jobManager.addNode(new JobNode(null,"notepad.exe"));
			
		});
//		runJob.setOnAction((event) -> {
//			Alert alert = new Alert(AlertType.INFORMATION);
//	    	alert.setTitle("Info");
//	    	alert.setContentText("To be implemented!");
//	    	alert.showAndWait();
//	    	return;
//		});
		saveProjectButton.setOnAction((event) -> {
			File wsDir = mainClass.projectManager.getWorkSpaceDir();
			if(wsDir==null || !wsDir.canWrite()) {return;}
			mainClass.projectManager.saveActiveProjectInMultipleFiles(wsDir);
		});
		menuSaveProjectAs.setOnAction((event) -> {
			
			DirectoryChooser dirChooser = new DirectoryChooser ();
			dirChooser.setTitle("Choose an alternative workspace folder to save the project");
			
			//go to current directory
			String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
			File tmpFile = new File(currentPath);
			if(tmpFile.canRead()) {
				dirChooser.setInitialDirectory(tmpFile);
			}
			
			File selectedDir = dirChooser.showDialog((Stage)rootPane.getScene().getWindow());
			
			if(selectedDir!=null && selectedDir.canWrite()) {
				mainClass.projectManager.saveActiveProjectInMultipleFiles(selectedDir);
			}
		});
		menuLoadProject.setOnAction((event) -> {
//			File wsDir = getWorkSpaceDir();
//			if(wsDir==null || !wsDir.canWrite()) {return;}
//			
//			FileChooser fileChooser = new FileChooser();
//			fileChooser.setInitialDirectory(wsDir);
//			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("project files", "*.proj"));
//			
//			File selectedFile = fileChooser.showOpenDialog((Stage)rootPane.getScene().getWindow());
//			
//			loadProject(selectedFile);
			
		});
		menuAbout.setOnAction((event) -> {
			Alert alert1 = new Alert(AlertType.INFORMATION);
	    	alert1.setTitle("License");
	    	alert1.setHeaderText("About");
	    	alert1.setContentText("Copyright (c) 2020 Haonan Huang.\r\n" + 
	    			"\r\n" + 
	    			"QuantumVITAS (Quantum Visualization Interactive Toolkit for Ab-initio Simulations) is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.\r\n" + 
	    			"\r\n" + 
	    			"QuantumVITAS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.\r\n" + 
	    			"\r\n" + 
	    			"You should have received a copy of the GNU General Public License along with QuantumVITAS. If not, see https://www.gnu.org/licenses/gpl-3.0.txt.");
	    	alert1.showAndWait();
		});
		
		settingsMenuItem.setOnAction((event) -> {
			
			//Scene scene = new Scene(borderSettings,600,450);
			Scene scene = new Scene(borderSettings);
	        Stage stage = new Stage();
	        stage.setTitle("Settings");
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initStyle(StageStyle.DECORATED);
	        stage.setScene(scene);
	        stage.showAndWait();
		});
		
		buttonOpenWorkSpace.setOnAction((event) -> {
			String wsp1 = mainClass.projectManager.readGlobalSettings(SettingKeys.workspace.toString());
			if(wsp1!=null) {
				File wsDir = new File(wsp1);
				if(mainClass.projectManager.existCurrentProject() && wsDir.canRead()) {
					Alert alert1 = new Alert(AlertType.INFORMATION);
			    	alert1.setTitle("Warning");
			    	alert1.setContentText("Please close ALL projects before changing the workspace directory.");
			    	alert1.showAndWait();
			    	return;
				}
			}
			
			DirectoryChooser dirChooser = new DirectoryChooser ();
			
			//go to current directory
			String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
			File tmpFile = new File(currentPath);
			if(tmpFile.canRead()) {
				dirChooser.setInitialDirectory(tmpFile);
			}
			
			File selectedDir = dirChooser.showDialog((Stage)rootPane.getScene().getWindow());
			
			if(selectedDir!=null && selectedDir.canRead()) {
				mainClass.projectManager.workSpacePath = selectedDir.getPath();
				textWorkSpace.setText(mainClass.projectManager.workSpacePath);
				mainClass.projectManager.writeGlobalSettings(SettingKeys.workspace.toString(),selectedDir.getPath());
				setWorkSpace(true);
				contTree.updateProjects();
				textWorkSpace.setBackground(new Background(new BackgroundFill(Coloring.validFile, 
						CornerRadii.EMPTY, Insets.EMPTY)));
			}
			
		});
		
	}
	private void updateWorkScene() {
		String currentPj = mainClass.projectManager.getActiveProjectName();
		if(currentPj==null) return;
		Tab newTab = this.projectTabDict.get(currentPj);
		if(newTab==null) return;
		
		VBox hbTmp = (VBox) newTab.getContent();
		//***may be unnecessary for some situations
		//remove last one. The if condition takes care of the case of first creation of a project
		if(tabRowNum==null) {
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Error");
	    	alert.setContentText("Null of tabRowNum. Cannot load workscene.");
	    	alert.showAndWait();
		}
		else {
			ObservableList<Node>  obsTmp = hbTmp.getChildren();
			if(obsTmp.size()>=tabRowNum) {obsTmp.remove(obsTmp.size()-1);}
			if(mainClass.projectManager.getShow3DScene()) {
				//add 3D view
				mainClass.projectManager.updateViewerPlot();//*******not always necessary
				WorkScene3D workScene = mainClass.projectManager.getActiveProject().getViewer3D();
				workScene.centerSubScene(workSpaceTabPane);
				AnchorPane acp = workScene.getRootPane();
				hbTmp.getChildren().add(acp);
			}
			else {
				contOutput.updateProjectFolder();
				hbTmp.getChildren().add(hboxOutput);
			}
		}
		
	}
	public void killAllThreads() {
		thread1.interrupt();
	}
	private void loadEnvironmentPaths() {
		//load environment variable
		String wsp = mainClass.projectManager.readGlobalSettings(SettingKeys.workspace.toString());
		mainClass.projectManager.workSpacePath = wsp;
		if (wsp!=null) {
			File wsDir = new File(wsp);
			if(wsDir.canRead()) {
				textWorkSpace.setText(wsp);
				textWorkSpace.setBackground(new Background(new BackgroundFill(Coloring.validFile, 
						CornerRadii.EMPTY, Insets.EMPTY)));
				setWorkSpace(true);
			}
			else {
				setWorkSpace(false);
				textWorkSpace.setBackground(new Background(new BackgroundFill(Coloring.invalidFile, 
						CornerRadii.EMPTY, Insets.EMPTY)));
				Alert alert1 = new Alert(AlertType.INFORMATION);
		    	alert1.setTitle("Warning");
		    	alert1.setContentText("Cannot load the previous workspace directory. Please specify a new one.");
		    	alert1.showAndWait();
			}
		}
		else {
			setWorkSpace(false);
			textWorkSpace.setBackground(new Background(new BackgroundFill(Coloring.invalidFile, 
					CornerRadii.EMPTY, Insets.EMPTY)));
			Alert alert1 = new Alert(AlertType.INFORMATION);
	    	alert1.setTitle("Message");
	    	alert1.setContentText("Please specify a workspace directory to start with.");
	    	alert1.showAndWait();
		}
		
		String qePath = mainClass.projectManager.readGlobalSettings(SettingKeys.qePath.toString());
		mainClass.projectManager.qePath = qePath;
		
		contTree.updateProjects();
		
		String wsp2 = mainClass.projectManager.readGlobalSettings(SettingKeys.pseudolibroot.toString());
		mainClass.projectManager.pseudoLibPath = wsp2;
	}

	private void closeProject(String pj) {
		String tmp = mainClass.projectManager.removeProject(pj);
		if(tmp!=null) return;//cannot remove project: pj==null || pj is empty or pj not in the list
		Tab tab = projectTabDict.get(pj);
		if(tab!=null) {workSpaceTabPane.getTabs().remove(tab);}
		contTree.closeProject(pj);
		projectTabDict.remove(pj);
		comboProject.getItems().remove(pj);
		//openCalc(null);//not necessary. Covered by the change tab listener
		contTree.setOpenCloseButtons(true);
	}
	
	
	private void setWorkSpace(boolean bl) {
		if (bl) {
			for (Node node : rootPane.getChildrenUnmodifiable()) {
				node.setDisable(false);
		    }
			for (Node node : buttonOpenWorkSpace.getParent().getParent().getChildrenUnmodifiable()) {
				node.setDisable(false);
		    }
			for (Node node : buttonOpenWorkSpace.getParent().getChildrenUnmodifiable()) {
				node.setDisable(false);
		    }
		}
		else{
			for (Node node : rootPane.getChildrenUnmodifiable()) {
				node.setDisable(true);
		    }
			buttonOpenWorkSpace.getParent().getParent().setDisable(false);
			for (Node node : buttonOpenWorkSpace.getParent().getParent().getChildrenUnmodifiable()) {
				node.setDisable(true);
		    }
			buttonOpenWorkSpace.getParent().setDisable(false);
			for (Node node : buttonOpenWorkSpace.getParent().getChildrenUnmodifiable()) {
				node.setDisable(true);
		    }
			buttonOpenWorkSpace.setDisable(false);
			textWorkSpace.setDisable(false);
		}
	}
	private void toggleGeometry() {
		if (tabPaneRight==null) return;
		if (radioGeometry.isSelected()) 
		{
			if (!mainClass.projectManager.existCurrentProject()) return;//abnormal!
			//radioGeometry.setSelected(true);
			mainClass.projectManager.setGeoActive(true);
			
			Tab tab = new Tab();
			tab.setText(EnumStep.GEO.getName());
			tab.setContent(scrollGeo);
			tabPaneRight.getTabs().clear();
			tabPaneRight.getTabs().add(tab);
			contGeo.loadProjectParameters();
			contGeo.setEnabled();
			calcLabel.setText("Geometry");
			comboCalculation.setValue("");
			if (!tabPaneStatusRight) {
				hboxRight.getChildren().add(0,tabPaneRight);
				tabPaneStatusRight = true;
			}
		}
		else {
			openCalc(mainClass.projectManager.getCurrentCalcName());
		}
		
	}
	public void addRightPane(ScrollPane scroll,EnumStep es) {
		if (tabPaneRight==null) return;
		
		Tab tab = new Tab();
		tab.setText(es.getName());
		tab.setContent(scroll);
		tabPaneRight.getTabs().add(tab);
		
		if (!tabPaneStatusRight) {
			hboxRight.getChildren().add(0,tabPaneRight);
			tabPaneStatusRight = true;
		}
	}
	public void clearRightPane() {
		if (tabPaneRight!=null) {
			tabPaneRight.getTabs().clear();
		}
		if (tabPaneStatusRight) {
			hboxRight.getChildren().remove(tabPaneRight);
		}
		tabPaneStatusRight = false;
	}
	private void setProjectNull() {
		mainClass.projectManager.setActiveProject(null);
		calcMain.setDisable(true);
		runJob.setDisable(true);
		clearRightPane();
		comboCalculation.getItems().clear();
		radioGeometry.setSelected(true);
		calcLabel.setText("");
	}
	
	private void initializeLeftRightPane() {
		
		// right part, default off
		tabPaneStatusRight = false;
		VBox vboxRight = new VBox();
		Button btnRight = new Button("R");
		vboxRight.getChildren().add(btnRight);
		hboxRight.getChildren().add(vboxRight);
		//right part, tab pane
		tabPaneRight = new TabPane();
		//Tab tab = new Tab("3e");
		//tabPaneRight.getTabs().add(tab);
		tabPaneRight.setPrefSize(375, 300);
		tabPaneRight.setMinSize(150, 150);
		tabPaneRight.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		//hboxRight.getChildren().add(0,tabPaneRight);
		
		//left part, default on
		scrollStatusLeft = true;
//		scrollLeft = new ScrollPane();
//		scrollLeft.setContent(projectTree);
		
		scrollLeft.setFitToHeight(true);
		VBox vboxLeft = new VBox();
		Button btnLeft = new Button("L");
		vboxLeft.getChildren().add(btnLeft);
		hboxLeft.getChildren().addAll(vboxLeft,scrollLeft);
		
		//set button action
		btnRight.setOnAction((event) -> {
			if (tabPaneStatusRight) {
				hboxRight.getChildren().remove(tabPaneRight);
			}
			else {
				hboxRight.getChildren().add(0,tabPaneRight);
			}
			tabPaneStatusRight=!tabPaneStatusRight;
		});
		btnLeft.setOnAction((event) -> {
			if (scrollStatusLeft) hboxLeft.getChildren().remove(scrollLeft);
			else hboxLeft.getChildren().add(scrollLeft);
			scrollStatusLeft=!scrollStatusLeft;
		});
	}
	private void createProjectGui(String projName) {

		//add to ComboBox
		comboProject.getItems().add(projName);
		comboProject.setValue(projName);
		//add tab
		Tab tab = new Tab();
		VBox hbTmp = new VBox();
		ToggleButton tgButton = new ToggleButton("");
		tgButton.setPrefWidth(150);
		
		if (mainClass.projectManager.getShow3DScene()) 
		{ tgButton.setSelected(false);tgButton.setText("Show input/output files");}
		else 
		{ tgButton.setSelected(true);tgButton.setText("Show geometry"); }
		//reversed than in projectManager
		tgButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{ 
			if(newValue==null) return;
			if (newValue) 
			{ tgButton.setText("Show geometry");}
			else 
			{ tgButton.setText("Show input/output files"); }
			mainClass.projectManager.setShow3DScene(!newValue);
			updateWorkScene();
		});
		
		hbTmp.getChildren().add(new HBox(tgButton,new Label("Toggle geometry and in/out files")));
		tabRowNum=2;//2 in total, including the display defined in the tab change listener
		
		tab.setContent(hbTmp);
		
		final String pj = projName;
		tab.setText(pj);
		tab.setClosable(true);
		tab.setOnClosed((e) -> {
			closeProject(pj);
		});
				
		//add tab
		projectTabDict.put(pj,tab);
		workSpaceTabPane.getTabs().add(tab);
		workSpaceTabPane.getSelectionModel().select(tab);//must happen AFTER projectTabDict.put(pj,tab), because updateWorkScene() uses this

		contTree.updateFullCalcTree();
		//allow more interactions
		calcMain.setDisable(false);
		runJob.setDisable(false);
		toggleGeometry();
	}
	private void openCalc(String ecStr) {
		
		//load an existing calculation having name String ecStr
		if (ecStr==null || ecStr.isEmpty()) {
			clearRightPane();
			comboCalculation.getItems().clear();
			calcLabel.setText("");
			return;
		}

		mainClass.projectManager.setActiveCalculation(ecStr);
		 
		//check again whether successfully set the active calculation
		if(!ecStr.equals(mainClass.projectManager.getCurrentCalcName())) {
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Error");
	    	alert.setContentText("Cannot set active calculation string ecStr.");
	    	alert.showAndWait();
	    	return;
		}
		EnumCalc ec = mainClass.projectManager.getCurrentCalcType();//null safe
		openCalc(ec, false);
		
	}
	private void openCalc(EnumCalc ec, boolean boolCreate) {
		//if(boolCreate), open a new calculation of type EnumCalc ec
		//if(!boolCreate), just load one existing calculation. MUST be entered through openCalc(String ecStr) in this case!
		String calcName="";
		if (ec==null) {return;}
		
		//Boolean firstFlag=false;
//		if (mainClass.projectManager.existCurrentStep(EnumStep.SCF)) contScf.loadProjectParameters();
//		if (mainClass.projectManager.existCurrentStep(EnumStep.GEO)) contGeo.loadProjectParameters();
		
		if (!mainClass.projectManager.existCurrentProject()) return;//abnormal!
		radioCalculation.setSelected(true);
		mainClass.projectManager.setGeoActive(false);
		
		switch(ec) {
		case SCF:
			if(boolCreate) {
				//create new calculation even if SCF exists
				//need to update current calculation before loading parameters
				mainClass.projectManager.addCalcToActiveProj(EnumCalc.SCF); 
				calcName = mainClass.projectManager.getCurrentCalcName();
				//initialize controllers. This will be automatically done only once
				//***moved to the beginning of the program
				//add comboBox item
				comboCalculation.getItems().add(calcName);
				//update current status to trees
				contTree.updateCalcTree(calcName);
				
				
				
			}
			
			//load parameters for current project and calculation
			contGeo.loadProjectParameters();
			contGeo.setDisabled();
			contScf.loadProjectParameters();
			clearRightPane();
			addRightPane(scrollGeo,EnumStep.GEO);
			addRightPane(scrollScf,EnumStep.SCF);
			try {tabPaneRight.getSelectionModel().select(1);}catch (Exception e) {}//load second tab(not geo)
			
			calcLabel.setText(EnumCalc.SCF.getLong());
			
			calcName = mainClass.projectManager.getCurrentCalcName();
			if(calcName!=null) comboCalculation.getSelectionModel().select(calcName);
			
			break;
		case OPT:
			if(boolCreate) {
				//need to update current calculation before loading parameters
				mainClass.projectManager.addCalcToActiveProj(EnumCalc.OPT); 
				calcName = mainClass.projectManager.getCurrentCalcName();
				//initialize controllers. This will be automatically done only once
				//***moved to the beginning of the program
				//add comboBox item
				comboCalculation.getItems().add(calcName);
				//update current status to trees
				contTree.updateCalcTree(calcName);
				
			}
			
			//load parameters for current project and calculation
			contGeo.loadProjectParameters();
			contGeo.setDisabled();
			contScf.loadProjectParameters();
			contOpt.loadProjectParameters();
			//update GUI
			clearRightPane();
			addRightPane(scrollGeo,EnumStep.GEO);
			addRightPane(scrollScf,EnumStep.SCF);
			addRightPane(scrollOpt,EnumStep.OPT);
			try {tabPaneRight.getSelectionModel().select(1);}catch (Exception e) {}//load second tab(not geo)
			
			calcLabel.setText(EnumCalc.OPT.getLong());
			calcName = mainClass.projectManager.getCurrentCalcName();
			if(calcName!=null) comboCalculation.getSelectionModel().select(calcName);
			break;
		case DOS:
			break;
		case BANDS:
			break;
		case BOMD:
			if(boolCreate) {
				//need to update current calculation before loading parameters
				mainClass.projectManager.addCalcToActiveProj(EnumCalc.BOMD); 
				calcName = mainClass.projectManager.getCurrentCalcName();
				//initialize controllers. This will be automatically done only once
				//***moved to the beginning of the program
				//add comboBox item
				comboCalculation.getItems().add(calcName);
				//update current status to trees
				contTree.updateCalcTree(calcName);
				
			}
			
			//load parameters for current project and calculation
			contGeo.loadProjectParameters();
			contGeo.setDisabled();
			contScf.loadProjectParameters();
			contMd.loadProjectParameters();
			//update GUI
			clearRightPane();
			addRightPane(scrollGeo,EnumStep.GEO);
			addRightPane(scrollScf,EnumStep.SCF);
			addRightPane(scrollMd,EnumStep.BOMD);
			try {tabPaneRight.getSelectionModel().select(1);}catch (Exception e) {}//load second tab(not geo)
			
			calcLabel.setText(EnumCalc.BOMD.getLong());
			calcName = mainClass.projectManager.getCurrentCalcName();
			if(calcName!=null) comboCalculation.getSelectionModel().select(calcName);
			break;
		case TDDFT:
			break;
		default:
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Error");
	    	alert.setContentText("Wrong calculation type!");

	    	alert.showAndWait();
		}
	}
}