package assignment5;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.collections.*;
import org.controlsfx.control.CheckListView;
import java.lang.reflect.Method;

public class Main extends Application implements EventHandler<ActionEvent> {

	private int stepInput = 1;
	private int quantityInput = 1;
	private String lastSeed = "~~~~~~~~~~~~~";
	private int stepsPerFrame;

	/**
	 * the main method of the program
	 * @param args no
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Go through directory and find all classes that instances of critter
	 * @param pkg the relevant package containing the Critters
	 * @return ArrayList of Critter classes
	 */
	private ArrayList<Class<Critter>> getCritterClasses(Package pkg) {
		ArrayList<Class<Critter>> classes = new ArrayList<Class<Critter>>();
		String packagename = pkg.getName();
		URL resource = ClassLoader.getSystemClassLoader().getResource(packagename);
		String path = resource.getFile(); //path to package
		File directory;
		try {
			directory = new File(resource.toURI());
		} catch (Exception e) {
			return null;
		}
		if (directory.exists()) {
			String[] files = directory.list();
			for (String file : files) {
				if (file.endsWith(".class")) {
					// removes the .class extension
					String className = packagename + '.' + file.substring(0, file.length() - 6);
					try {
						Class classObj = Class.forName(className);
						try {
							Object obj = classObj.newInstance();
							if (obj instanceof Critter) {
								classes.add(classObj);
							}
						} catch (Exception e) {
							continue; //Skip if class cannot be made into object
						}
					} catch (ClassNotFoundException e) {
						return null;
					}
				}
			}
		}
		return classes;
	}

	/**
	 * Go through classes and return array of names
	 * @return Array of class names that extend Critter in the package
	 */
	private ArrayList<String> getCritterOptionsMenu() {
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Class<Critter>> critters = getCritterClasses(this.getClass().getPackage());
		for (Class<Critter> c:critters){
			items.add(c.getSimpleName());
		}
		return items;
	}

	/**
	 * Add critters in world
	 * @param critter_name the name of the critter class to be added
	 * @param num the number of critters to be added
	 */
	private void addCritters(String critter_name, int num) {
		for(int i = 0; i<num; i++){
			try{
				Critter.makeCritter(critter_name);
			}catch(InvalidCritterException e){
				return;
			}
		}
	}

	/**
	 * Uses reflection and goes through checked items to call run stats and output to text area
	 * @param checkview the List of checked classes on the checkview to be run for stats
	 * @param stats the TextArea to be outputted to
	 */
	private void displayStats(CheckListView<String> checkview, TextArea stats) {
		StringBuilder start = new StringBuilder();
		for(String critter_name:checkview.getCheckModel().getCheckedItems()){
			try {
				Class classTemp = Class.forName(this.getClass().getPackage().getName() + "." + critter_name);
				Method m = classTemp.getMethod("runStats", List.class); //Get method
				List<Critter> instances = Critter.getInstances(critter_name);
				String output = (String)m.invoke(null, instances); //Invoke with instances
				start.append(output).append("\n");
			} catch (Exception e) {
				return;
			}
		}
		stats.clear();
		stats.appendText(start.toString());
	}

	/**
	 * Primary method handling all layout, pane, and modules for the Critter GUI. Each section is separated and named
	 * by their function. All calculations and additional logic is appended to the bottom of this class.
	 * @param primaryStage Stage to be drawn onto
	 */
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Critter World");

		//Central Pane
		Pane center = new Pane();
		center.setPadding(new Insets(5, 5, 5, 5));
		center.setBorder(new Border((new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
		Canvas canvas = new Canvas((int)center.getWidth(),(int)center.getHeight());
		canvas.widthProperty().bind(center.widthProperty());
		canvas.heightProperty().bind(center.heightProperty());
		canvas.widthProperty().addListener(event -> displayWorld(canvas));
		canvas.heightProperty().addListener(event -> displayWorld(canvas));
		center.getChildren().add(canvas); //Add canvas to center pane

		//Initialize runstats components
		ObservableList<String> strings = FXCollections.observableArrayList();
		strings.addAll(getCritterOptionsMenu());
		CheckListView<String> checkview = new CheckListView<String>(strings); //Run stats checkbox
		checkview.setMaxHeight(100);
		TextArea stats = new TextArea(); //Run stats Text Area
		stats.setMaxWidth(265);

		checkview.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				displayStats(checkview, stats); //Call run stats every time checkbox is selected
			}
		});

		//Display world to start
		displayWorld(canvas);
		displayStats(checkview, stats);

		//CritterQuantity Pane Title
		Label quantityStepTitle = new Label("Critter Population Settings");

		//CritterQuantity ChoiceBox
		ChoiceBox<String> critterTypes = new ChoiceBox<String>();
		critterTypes.getItems().addAll(getCritterOptionsMenu());
		if(critterTypes.getItems().size()>0){
			critterTypes.setValue(critterTypes.getItems().get(0));
		}

		//CritterQuantity 'Add' Button
		Button sizeStep = new Button();
		sizeStep.setText("Add");
		sizeStep.setOnAction(event -> {
			addCritters(critterTypes.getValue(),quantityInput);
        });
		Label currentQuantityInput = new Label("Current Quantity: " + quantityInput + " Critters");

		//CritterQuantity Slider
		Slider quantityMultiplier = new Slider();
		quantityMultiplier.setMin(0);
		quantityMultiplier.setMax(100);
		quantityMultiplier.setValue(1);
		quantityMultiplier.setShowTickLabels(true);
		quantityMultiplier.setShowTickLabels(true);
		quantityMultiplier.valueProperty().addListener((observable, oldValue, newValue) -> {
            quantityInput = (int)quantityMultiplier.getValue();
            currentQuantityInput.setText("Current Interval: " + quantityInput + " Critters");
        });

		//CritterQuantity Slider
		Button submit = new Button("Custom");
		submit.setOnAction(event -> {
			long newInput = inputAlert("Input", "Set your desired custom interval range 1-1000");
			if(newInput > 1000) {
				displayAlert("Error", "Error: Invalid Input. Critter Interval Unchanged.");
			} else {
				quantityInput = (int)newInput;
				currentQuantityInput.setText("Current Interval: " + quantityInput + " Steps");
			}
		});

		//CritterQuantity Pane
		HBox quant = new HBox(quantityMultiplier, submit);
		quant.setAlignment(Pos.CENTER_RIGHT);
		quant.setSpacing(15);

		HBox quantz = new HBox(currentQuantityInput, sizeStep);
		quantz.setAlignment(Pos.CENTER_RIGHT);
		quantz.setSpacing(25);

		VBox quantities = new VBox(quantityStepTitle, critterTypes, quant, quantz);

		quantities.setSpacing(15);
		quantities.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		quantities.setAlignment(Pos.CENTER);
		quantities.setPadding(new Insets(15, 5, 15, 5));

		//TimeStep Pane Title
		Label timeStepTitle = new Label("Time-Step Settings");

		//TimeStep 'Step' Button

		Button inputStep = new Button();
		inputStep.setText("Step");
		inputStep.setOnAction(event -> {
			for(int i = 0; i < stepInput; i++) {
				Critter.worldTimeStep();
				displayWorld(canvas);
				displayStats(checkview, stats);
			}
		});
		Label currentStepInput = new Label("Current Interval: " + stepInput + " Steps");

		//TimeStep Multiplier Slider
		Slider stepMultiplier = new Slider(0, 100, 1);
		stepMultiplier.setShowTickLabels(true);
		stepMultiplier.setShowTickMarks(true);
		stepMultiplier.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				stepInput = (int)stepMultiplier.getValue();
				currentStepInput.setText("Current Interval: " + stepInput + " Steps");
			}
		});

		//TimeStep 'Custom' Button
		Button custom = new Button("Custom");
		custom.setOnAction(event -> {
			long newInput = inputAlert("Input", "Set your desired custom interval range 1-1000");
			if(newInput > 1000) {
				displayAlert("Error", "Error: Invalid Input. World-Step Interval Unchanged.");
			} else {
				stepInput = (int)newInput;
				currentStepInput.setText("Current Interval: " + stepInput + " Steps");
			}
		});

		//TimeStep Panes
		HBox steps = new HBox(stepMultiplier, custom);
		steps.setAlignment(Pos.CENTER_RIGHT);
		steps.setSpacing(15);

		HBox stepz = new HBox(currentStepInput, inputStep);
		stepz.setAlignment(Pos.CENTER_RIGHT);
		stepz.setSpacing(25);

		VBox stepping = new VBox(timeStepTitle, steps, stepz);
		stepping.setSpacing(15);
		stepping.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		stepping.setAlignment(Pos.CENTER);
		stepping.setPadding(new Insets(15, 5, 15, 5));

		//Seed Pane Title
		Label seedTitle = new Label("Seed Settings");

		//Seed Button && CurrentSeed Label
		TextField seed = new TextField();
		Button setSeed = new Button("Set");
		Label currentSeed = new Label("Current Seed:  ~~~~~~~~~~");
		setSeed.setOnAction(event -> {
			flag = false;
			int[] submission = seed.getCharacters().chars().toArray();
			long total = 0;
			total = condense(submission);
			if(!flag) {
				Critter.setSeed(total);
				lastSeed = Long.toString(total);
				currentSeed.setText("Current Seed: " + lastSeed);
			} else {
				displayAlert("Error", "Error: Invalid Input. Seed Unchanged.");
			}
		});

		//Seed Panes
		HBox changeSeed = new HBox(seed, setSeed);
		changeSeed.setAlignment(Pos.CENTER);
		changeSeed.setSpacing(15);

		VBox seedBox = new VBox(seedTitle, changeSeed, currentSeed);
		seedBox.setSpacing(15);
		seedBox.setAlignment(Pos.CENTER);
		seedBox.setBorder(new Border(new BorderStroke(Color.BLACK,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		seedBox.setPadding(new Insets(15, 5, 15, 5));

		//End Program Button
		Button end = new Button("Close");
		end.setOnAction(event -> System.exit(0));
		end.setPadding(new Insets(15, 115, 15, 115));

		//RunStats Pane
		HBox relevantCritter = new HBox(checkview);
		relevantCritter.setAlignment(Pos.CENTER);
		VBox runStats = new VBox(relevantCritter, stats);
		runStats.setAlignment(Pos.CENTER);
		runStats.setSpacing(15);
		runStats.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		runStats.setAlignment(Pos.CENTER);
		runStats.setPadding(new Insets(15, 5, 15, 5));

		//Animation 'Current'
		Label currentSPF = new Label(Integer.toString(stepsPerFrame));

		//Animation Steps/Frame Slider
		Slider animationScale = new Slider(0,25,10);
		animationScale.setShowTickMarks(true);
		animationScale.setShowTickLabels(true);
		stepsPerFrame = 10;
		currentSPF.setText(Integer.toString(stepsPerFrame) + " Steps Per Frame");
		animationScale.valueProperty().addListener((observable, oldValue, newValue) -> {
            stepsPerFrame = (int)animationScale.getValue();
            currentSPF.setText(Integer.toString(stepsPerFrame) + " Steps Per Frame");
        });

		//Animation Timeline
		Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            for(int i = 0; i < stepsPerFrame; i++) {
                Critter.worldTimeStep();
            }
            displayWorld(canvas);
            displayStats(checkview, stats);
        }));
		fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);

		//Animation 'Start' Button
		Button set = new Button("Start");
		set.setOnAction(event -> {
			fiveSecondsWonder.play();

			seedBox.setDisable(true); //Disable all elements on start animation
			seed.setDisable(true);
			setSeed.setDisable(true);
			stepMultiplier.setDisable(true);
			submit.setDisable(true);
			custom.setDisable(true);
			inputStep.setDisable(true);
			sizeStep.setDisable(true);
			quantityMultiplier.setDisable(true);
			checkview.setDisable(true);
			animationScale.setDisable(true);
			set.setDisable(true);
			critterTypes.setDisable(true);
		});

		//Animation 'Stop' Button
		Button stop = new Button("Stop");
		stop.setOnAction(event -> {
			fiveSecondsWonder.stop();
			seedBox.setDisable(false); //Enable all elements
			seed.setDisable(false);
			setSeed.setDisable(false);
			stepMultiplier.setDisable(false);
			submit.setDisable(false);
			custom.setDisable(false);
			inputStep.setDisable(false);
			sizeStep.setDisable(false);
			quantityMultiplier.setDisable(false);
			checkview.setDisable(false);
			animationScale.setDisable(false);
			set.setDisable(false);
			critterTypes.setDisable(false);
		});

		//Animation Pane Title
		Label bottomTitle = new Label("Animation");

		//Right ControlPanel Pane
		VBox rightPane = new VBox();
		rightPane.getChildren().addAll(seedBox,stepping, quantities, runStats);
		rightPane.setSpacing(5);
		rightPane.setAlignment(Pos.CENTER);
		rightPane.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		//Animation Control Pane
		HBox bottomControl = new HBox(currentSPF, animationScale, set, stop, end);
		bottomControl.setMinHeight(50);
		bottomControl.setSpacing(15);
		bottomControl.setAlignment(Pos.CENTER);

		//Bottom Animation Pane
		VBox bottomPane = new VBox(bottomTitle, bottomControl);
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		bottomPane.setPadding(new Insets(15, 5, 15, 5));

		//BottomBottomPane (for the sxe visuals)
		VBox BottomBottomPane = new VBox(bottomPane);
		BottomBottomPane.setPadding(new Insets(5, 5, 15, 5));
		BottomBottomPane.setBorder(new Border(new BorderStroke(Color.BLACK,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		//BorderPane
		BorderPane border = new BorderPane();
		border.setBottom(BottomBottomPane);
		border.setRight(rightPane);
		border.setCenter(center);
		border.autosize();

		//Setting the Scene
		Scene scene = new Scene(border, 1500, 750);

		//Loading the Stage and setting parameters
		primaryStage.setScene(scene);
		primaryStage.setMinHeight(780);
		primaryStage.setMinWidth(450);
		primaryStage.show();
	}

	/**
	 * Draws square shape
	 * @param gc GraphicsContext to be drawn onto
	 * @param x x size of the square
	 * @param y y size of the square
	 * @param length length of the square
	 */
	public void drawSquare(GraphicsContext gc, double x, double y, double length) {
		gc.fillPolygon(new double[]{x, x+length, x+length, x},
				new double[]{y, y, y+length, y+length}, 4);
		gc.strokePolygon(new double[]{x, x+length, x+length, x},
				new double[]{y, y, y+length, y+length}, 4);
	}

	/**
	 * Draws circle shape
	 * @param gc GraphicsContext to be drawn onto
	 * @param x x size of the circle
	 * @param y y size of the circle
	 * @param diameter diameter of the circle
	 */
	private void drawCircle(GraphicsContext gc, double x, double y, double diameter) {
		gc.fillOval(x, y, diameter, diameter);
		gc.strokeOval(x, y, diameter, diameter);
	}

	/**
	 * Draws triangle shape
	 * @param gc GraphicsContext to be drawn onto
	 * @param x x size of the triangle
	 * @param y y size of the triangle
	 * @param length length of the triangle
	 */
	private void drawTriangle(GraphicsContext gc, double x, double y, double length) {
		gc.fillPolygon(new double[]{x, x+length/2, x+length},
				new double[]{y+length, y, y+length}, 3);
		gc.strokePolygon(new double[]{x, x+length/2, x+length},
				new double[]{y+length, y, y+length}, 3);
	}

	/**
	 * Draws diamond shape
	 * @param gc GraphicsContext to be drawn onto
	 * @param x x size of the diamond
	 * @param y y size of the diamond
	 * @param length length of the diamond
	 */
	private void drawDiamond(GraphicsContext gc, double x, double y, double length) {
		gc.fillPolygon(new double[]{x, x+length/2, x+length, x+length/2},
				new double[]{y+length/2, y+length, y+length/2, y}, 4);
		gc.strokePolygon(new double[]{x, x+length/2, x+length, x+length/2},
				new double[]{y+length/2, y+length, y+length/2, y}, 4);
	}

	/**
	 * Draws star shape
	 * @param gc GraphicsContext to be drawn onto
	 * @param x x size of the star
	 * @param y y size of the star
	 * @param length length of the star
	 */
	private void drawStar(GraphicsContext gc, double x, double y, double length) {
		double xpoints[] = {x, x+0.375*length, x+0.5*length, x+0.625*length, x+length, x+0.75*length,
				x+0.8*length, x+0.5*length, x+0.2*length, x+0.25*length};
		double ypoints[] = {y+0.375*length, y+0.325*length, y, y+0.325*length, y+0.375*length, y+0.575*length,
				y+0.9*length, y+0.7*length, y+0.9*length, y+0.575*length};
		gc.fillPolygon(xpoints, ypoints, xpoints.length);
		gc.strokePolygon(xpoints, ypoints, xpoints.length);
	}

	/**
	 * Draws grid lines on the given canvas
	 * @param canvas the canvas to be drawn onto
	 * @param cell_width the desired cell width
	 * @param cell_height the desired cell height
	 */
	private void drawGrid(Canvas canvas, double cell_width, double cell_height) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(),canvas.getHeight());
		// vertical lines
		gc.setStroke(Color.BLACK);
		for(int i = 0 ; i < Params.world_width; i++){
			gc.strokeLine(cell_width*i, 0, cell_width*i, canvas.getHeight());
		}
		for(int i = 0 ; i < Params.world_height; i++){
			gc.strokeLine(0, cell_height*i, canvas.getWidth(), cell_height*i);
		}
	}

	/**
	 * Draws general critter onto a Canvas in the given coordinates.
	 * @param canvas the canvas for the Critter to be drawn onto
	 * @param critter the critter that is going to be used to get the drawing details
	 * @param cell_x the x coordinate for the symbol
	 * @param cell_y the y coordinate for the symbol
	 */
	private void drawCritter(Canvas canvas, Critter critter, int cell_x, int cell_y) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double cell_width = canvas.getWidth()/Params.world_width;
		double cell_height = canvas.getHeight()/Params.world_width;

		double length, padding = cell_width/10, x_offset = padding/2, y_offset = padding/2;
		if(cell_width<cell_height){
			length = cell_width;
			y_offset += (cell_height-cell_width)/2;
		}else{
			length = cell_height;
			x_offset += (cell_width-cell_height)/2;
		}

		gc.setStroke(critter.viewOutlineColor());
		gc.setFill(critter.viewFillColor());

		switch (critter.viewShape()){
			case CIRCLE: 	drawCircle(gc, x_offset + cell_x*cell_width, y_offset + cell_y*cell_height, length-padding);
						 	break;
			case SQUARE: 	drawSquare(gc, x_offset + cell_x*cell_width, y_offset + cell_y*cell_height, length-padding);
							break;
			case TRIANGLE: 	drawTriangle(gc, x_offset + cell_x*cell_width, y_offset + cell_y*cell_height, length-padding);
							break;
			case DIAMOND: 	drawDiamond(gc, x_offset + cell_x*cell_width, y_offset + cell_y*cell_height, length-padding);
							break;
			case STAR:		drawStar(gc, x_offset + cell_x*cell_width, y_offset + cell_y*cell_height, length-padding);
							break;
		}
	}

	/**
	 * Goes through all critters and displays them onto a grid pane on the canvas given
	 * @param canvas the Canvas to be drawn onto
	 */
	private void displayWorld(Canvas canvas) {
		ArrayList<Critter>[][] world = Critter.displayWorld();

		double cell_width = canvas.getWidth()/Params.world_width;
		double cell_height = canvas.getHeight()/Params.world_width;

		drawGrid(canvas, cell_width, cell_height);

		for(int i = 0; i < Params.world_width; i++) {
			for(int j = 0; j < Params.world_height; j++) {
				if(world[i][j]!=null && world[i][j].size()>0){
					Critter critter = world[i][j].get(0);
					drawCritter(canvas, critter, i, j);
				}
			}
		}
	}

	/**
	 * A class that is required to be overridden by start in order for the program to function. All events are handled
	 * separately for each module so no events should end up here. If they do the program will output an error message
	 * to the console stating that an event was not handled.
	 * @param event start event to be handled
	 */
	@Override
	public void handle(ActionEvent event) {
		System.out.println("Error- ActionEvent not Handled: " + event.getSource());
	}

	/**
	 * Display alert is a module for displaying an alert to the user. To be dismissed it required that the user
	 * acknowledges the message by clicking a button.
	 * @param title the window title to be displayed
	 * @param message the message to be displayed to the user in the window
	 */
	private void displayAlert(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setHeight(100);

		Label errorMessage = new Label(message);
		errorMessage.setPrefWidth(150 + message.length() * 5);
		errorMessage.setAlignment(Pos.CENTER);
		Button close = new Button("Okay");
		close.setOnAction(event -> window.close());

		window.setWidth(errorMessage.getPrefWidth());

		VBox layout = new VBox();
		layout.getChildren().addAll(errorMessage, close);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(15);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}

	private static boolean answer;
	/**
	 * Confirm alert is a an Alert module that displays a message that its given and asks the user to answer with a
	 * yes or no represented as buttons. Their answer is returned.
	 * @param title the desired window title
	 * @param message the desired message to be displayed in the window
	 * @return the user's answer to the alert
	 */
	private boolean confirmAlert(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setHeight(100);

		Label errorMessage = new Label(message);
		errorMessage.setPrefWidth(150 + message.length() * 5);
		errorMessage.setAlignment(Pos.CENTER);
		window.setWidth(errorMessage.getPrefWidth());

		Button yes = new Button("yes");
		Button no =  new Button("no");

		yes.setOnAction(event -> {
			window.close();
			answer = true;
		});
		no.setOnAction(event -> {
			window.close();
			answer = false;
		});


		HBox answers = new HBox(yes, no);
		answers.setSpacing(15);
		answers.setAlignment(Pos.CENTER);
		VBox layout = new VBox();
		layout.getChildren().addAll(errorMessage, answers);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(15);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

		return answer;
	}

	private static long alertInput;
	private boolean flag;
	/**
	 * Input alert is a module for getting an input from the user. It displays the input message given and asks the user
	 * to input a response into a text box and click submit.
	 * @param title the Window title
	 * @param input the message to be displayed in the window
	 * @return the User's input into the text box
	 */
	private long inputAlert(String title, String input) {
		flag = false;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setHeight(100);

		Label errorMessage = new Label(input);
		errorMessage.setPrefWidth(150 + input.length() * 5);
		errorMessage.setAlignment(Pos.CENTER);
		window.setWidth(errorMessage.getPrefWidth());

		TextField alert = new TextField();
		Button submit = new Button("Submit");

		submit.setOnAction(event -> {
			int[] inputs = alert.getCharacters().chars().toArray();
			int total = condense(inputs);
			if(!flag) {
				alertInput = total;
			} else {
				alertInput = 100000000; 	//sets to be flagged later on
			}
			window.close();
		});

		HBox submission = new HBox(alert, submit);
		submission.setSpacing(15);
		submission.setAlignment(Pos.CENTER);
		VBox layout = new VBox();
		layout.getChildren().addAll(errorMessage, submission);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(15);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return alertInput;
	}

	/**
	 * Condense condenses an array of integers into a single number 'total' which is returned
	 * @param inputs the array of inputted integers
	 * @return total
	 */
	private int condense(int[] inputs) {
		int total = 0;
		flag = false;
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] -= 48;
			if(inputs[i] < 0 || inputs[i] > 9) {
				flag = true;
				break;
			}
			total *= 10;
			total += inputs[i];
		}
		return total;
	}
}