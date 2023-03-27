package main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import objects.Object;
import objects.*;
import spots.HideoutPopulation;
import spots.Population;
import spots.SourcePopulation;

import java.util.LinkedList;
import java.util.Objects;

/**
 * The Main class is used to run the program.
 */
public class Main extends Application {
    private final MediaPlayer mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/music/iFeelGood.mp3")).toExternalForm())); //The music player.
    public static void main(String[] args) {
        launch(args);
    } //The main method.
    /**
     * The start method is used to start the program.
     * @param primaryStage The primary stage.
     * The method initializes GUI, creates a population of sources and hideouts, and runs the simulation.
     */
    @Override
    public void start(Stage primaryStage) {  //The start method.
        Group background = new Group();
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds(); // Gets the screen size. I want to make the game fullscreen
        Scene scene = new Scene(background, screenSize.getWidth(), screenSize.getHeight());
        setUpStage(primaryStage, scene, screenSize);
        draw("images/background.png", background, (int) screenSize.getWidth()/2, (int) screenSize.getHeight()/2, "bg",(int) screenSize.getWidth(),(int) screenSize.getHeight());
        VBox infoPanel = startInfoPanel(background, screenSize); //The info panel. Shows the info about the game.

        LinkedList<Prey> preys = new LinkedList<>();
        LinkedList<Spot> spots = createSpots(background);
        LinkedList<Predator> predators = new LinkedList<>();

        addPreyButton(background, spots, preys, screenSize);
        addPredatorButton(background, predators, preys, screenSize);

        final Object[] nearestObject = {null}; //The nearest object to the mouse.
        scene.setOnMouseClicked(event -> {
            nearestObject[0] = findNearestAnimal(event.getX(), event.getY(), predators, preys, spots); //Finds the nearest object to the mouse.
            displayInfo(infoPanel, nearestObject[0], spots, predators, preys); //Displays the info about the nearest object.
            if (nearestObject[0] instanceof Animal){
                addRemoveAnimalButton(background, nearestObject[0], predators, preys, screenSize);
                if (nearestObject[0] instanceof Prey){
                    addRerouteButton(background, nearestObject[0], screenSize);
                }
                if (nearestObject[0] instanceof Predator){
                    addDrinkMagicPotionButton(background, nearestObject[0], screenSize);
                }
            }
            //add remove all button TODO; add remove all button
            //addRemoveAllButton(background, predators, preys, screenSize);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        //still working on the music :) DONEE
        mediaPlayer.volumeProperty().setValue(0.1);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        //updated the map as animals move
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMap(background, predators, preys, nearestObject[0]);
                displayInfo(infoPanel, nearestObject[0], spots, predators, preys);
            }

        };
        timer.start();
    }
    /**
     * The setUpStage method is used to set up the stage. It sets the title, the icon, and the size of the stage.
     * @param primaryStage The primary stage.
     * @param scene The scene.
     * @param screenSize The screen size.
     * The method sets up the stage.
     */
    private void setUpStage(Stage primaryStage, Scene scene, Rectangle2D screenSize) { //Sets up the stage.
        primaryStage.getIcons().add(new Image("images/23.png")); //23 is the icon of the game. Always use 23. Always.
        primaryStage.setTitle("Prey-Predator Simulation"); //The title of the game. Should be changed.
        primaryStage.setScene(scene);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setWidth(screenSize.getWidth());
        primaryStage.setHeight(screenSize.getHeight());
    }
    /**
     * the createSpots method is used to create the spots.
     * It uses SourcePopulation and HideoutPopulation classes to create the spots.
     * then it creates spots lists and adds the spots to the background.
     * it also draws the spots on the background and creates the paths between the spots.
     * it returns the list of spots.
     * @param background The background. Essentially the root of the scene.
     * @return the list of spots.
     */
    private LinkedList<Spot> createSpots(Group background) {
        //create sources population, type true for water, type false for food, create hideouts
        SourcePopulation waterPopulation = new SourcePopulation(10, true); //The water population.
        SourcePopulation foodPopulation = new SourcePopulation(10, false); //The food population.
        HideoutPopulation hideoutPopulation = new HideoutPopulation(10); //The hideout population.
        LinkedList<Spot> spots = new LinkedList<>(); //The spots.
        for (int i = 0; i < hideoutPopulation.getPopulationSize(); i++) {
            spots.add((Spot) waterPopulation.getObj(i));
            spots.add((Spot) foodPopulation.getObj(i));
            spots.add((Spot) hideoutPopulation.getObj(i));
        }
        //draw sources, hideouts, neighbors
        drawNeighbors(background, waterPopulation, foodPopulation);
        drawNeighbors(background, foodPopulation, hideoutPopulation);
        drawNeighbors(background, hideoutPopulation, waterPopulation);
        drawNeighbors(background, waterPopulation, hideoutPopulation);
        drawNeighbors(background, foodPopulation, waterPopulation);
        drawNeighbors(background, hideoutPopulation, foodPopulation);
        drawSpots(background, waterPopulation, "images/water.png");
        drawSpots(background, foodPopulation, "images/food.png");
        drawSpots(background, hideoutPopulation, "images/hideout.png");

        return spots;
    }
    /**
     * The drawNeighbors method is used to draw the neighbors between two populations.
     * @param root The background.
     * @param population1 The first population.
     * @param population2 The second population.
     * The method draws the neighbors of the spots.
     */
    private void drawNeighbors(Group root, Population population1, Population population2) {
        Path path; //path to draw
        Path path2; //path to draw
        for (int i = 0; i < population1.getPopulationSize(); i++) {
            //create a list of distances to food sources
            double[] distances = calculateDistances(population1.getObj(i), population2);
            //find the closest spot
            int minIndex = findMinIndex(distances);
            //let's make it more readable
            int x1 = population1.getObj(i).getX();
            int y1 = population1.getObj(i).getY();
            int x2 = population2.getObj(minIndex).getX();
            int y2 = population2.getObj(minIndex).getY();
            //create a path from the spot1 to the spot2
            Line line = new Line(x1, y1, x2, y2);
            path = new Path();
            path.addStartSpot((Spot) population1.getObj(i));
            path.addEndSpot((Spot) population2.getObj(minIndex));
            path.addLine(line);
            Spot spot = (Spot) population1.getObj(i);
            spot.addPath(path);
            //create a path from the spot2 to the spot1
            Line line2 = new Line(x2, y2, x1, y1);
            path2 = new Path();
            path2.addStartSpot((Spot) population2.getObj(minIndex));
            path2.addEndSpot((Spot) population1.getObj(i));
            path2.addLine(line2);
            Spot spot2 = (Spot) population2.getObj(minIndex);
            spot2.addPath(path2);
            //draw the path on the map
            line.setStroke(Color.BLUE);
            line.setStrokeWidth(1);
            root.getChildren().add(line);
        }
    }
    /**
     * the startInfoPanel method is used to start the info panel.
     * it creates the info panel and adds it to the background.
     * @param background The background.
     * @param screenSize The screen size.
     */
    private VBox startInfoPanel(Group background, Rectangle2D screenSize) {
        VBox infoPanel = new VBox();
        infoPanel.setPrefWidth(screenSize.getWidth()*0.2); //The width of the info panel is 20% of the screen width.
        infoPanel.setPrefHeight(screenSize.getHeight()/3); //The height of the info panel is 1/3 of the screen height.
        infoPanel.setLayoutX(screenSize.getWidth() - infoPanel.getPrefWidth()); //The info panel is on the right side of the screen.
        infoPanel.setLayoutY(screenSize.getHeight()/3); //The info panel is in the middle of the screen.
        infoPanel.setSpacing(8); //The spacing between the labels.
        infoPanel.setPadding(new Insets(10)); //The padding of the info panel.
        infoPanel.setStyle("-fx-background-color: white; -fx-border-color: black;"); //The style of the info panel.
        background.getChildren().add(infoPanel); //Adds the info panel to the background.
        return infoPanel; //Returns the info panel.
    }
    /**
     * the addPredators method is used to enable the user to add predators to the game.
     * @param background the background.
     * @param predators the list of predators.
     * @param preys the list of preys.
     * @param screenSize the screen size.
     */
    private void addPredatorButton(Group background, LinkedList<Predator> predators, LinkedList<Prey> preys, Rectangle2D screenSize) {
        Button addPredatorButton = new Button("Add Predator"); //The button to add a predator.
        addPredatorButton.setPrefSize(100, 20); //The size of the button.
        addPredatorButton.setLayoutX(screenSize.getWidth() - addPredatorButton.getPrefWidth()*1.5); //The x position of the button.
        addPredatorButton.setLayoutY(10); //The y position of the button.
        addPredatorButton.setOnAction(event -> { //When the button is clicked.
            Predator predator = new Predator(preys); //Creates a new predator.
            predators.add(predator); //Adds the predator to the list of predators.
            new Thread(predator).start(); //Starts the predator thread.
        });
        background.getChildren().add(addPredatorButton); //Adds the button to the background.
    }
    /**
     * the addPreyButton method is used to enable the user to add preys to the game.
     * @param background the background.
     * @param preys the list of preys.
     * @param screenSize the screen size.
     */
    private void addPreyButton(Group background, LinkedList<Spot> spots, LinkedList<Prey> preys, Rectangle2D screenSize) {
        Button addPreyButton = new Button("Add Prey"); //The button to add a prey.
        //The size of the button. The x position of the button. The y position of the button.
        addPreyButton.setPrefSize(100, 20);
        addPreyButton.setLayoutX(screenSize.getWidth() - addPreyButton.getPrefWidth()*3);
        addPreyButton.setLayoutY(10);
        addPreyButton.setOnAction(event -> { //When the button is clicked.
            Prey prey = new Prey(spots, preys);
            preys.add(prey);
            prey.teleport_randomly(spots); //Teleports the prey to a random spot, because initalizing the prey will not teleport it.
            new Thread(prey).start(); //Starts the prey thread.
        });
        background.getChildren().add(addPreyButton);
    }
    /**
     * the addRemoveAnimalButton method is used to enable the user to remove animals from the game.
     * @param background the background.
     * @param nearestObject the nearest object.
     * @param predators the list of predators.
     * @param preys the list of preys.
     * @param screenSize the screen size.
     */
    private void addRemoveAnimalButton(Group background, Object nearestObject, LinkedList<Predator> predators, LinkedList<Prey> preys, Rectangle2D screenSize) {
        Button removeAnimalButton = new Button("Remove");
        //The size of the button. The x position of the button. The y position of the button.
        removeAnimalButton.setPrefSize(100, 20);
        removeAnimalButton.setLayoutX(screenSize.getWidth() - removeAnimalButton.getPrefWidth()*3);
        removeAnimalButton.setLayoutY(50);
        removeAnimalButton.setOnAction(event -> {
            if (nearestObject instanceof Predator) {
                ((Predator) nearestObject).die();
                predators.remove(nearestObject);
            } else {
                ((Prey) nearestObject).die();
                preys.remove(nearestObject);
            }
            Thread.currentThread().interrupt();

        });
        background.getChildren().add(removeAnimalButton);
    }
    /**
     * the addRerouteButton method is used to enable the user to reroute the paths of the preys
     * @param background the background.
     * @param object the nearest object.
     * @param screenSize the screen size.
     */
    private void addRerouteButton(Group background, Object object, Rectangle2D screenSize) {
        if (object instanceof Prey) {  //If the object is a prey.
            Button rerouteButton = new Button("Reroute"); //The button to reroute the prey.
            //The size of the button. The x position of the button. The y position of the button.
            rerouteButton.setPrefSize(100, 20);
            rerouteButton.setLayoutX(screenSize.getWidth() - rerouteButton.getPrefWidth() * 1.5);
            rerouteButton.setLayoutY(50);
            rerouteButton.setOnAction(event -> {
                try {
                    Prey prey = (Prey) object;
                    prey.reroute();
                } catch (Exception e) {
                    System.out.println("You can't reroute this object!"); //If the object is not a prey.
                    e.printStackTrace();
                }
            });
            background.getChildren().add(rerouteButton);
        }else{
            //remove reroute button
            for (Node node : background.getChildren()) {
                if (node instanceof Button) {
                    if (((Button) node).getText().equals("Reroute")) {
                        background.getChildren().remove(node);
                    }
                }
            }
        }
    }
    /**
     * the addDrinkMagicPotionButton method is used to enable the user to make predator drink a magic potion.
     * @param background the background.
     * @param object the list of spots.
     * @param screenSize the screen size.
     */
    private void addDrinkMagicPotionButton(Group background, Object object, Rectangle2D screenSize) {
        Button drinkMagicPotionButton = new Button("Magic Potion"); //The button to drink a magic potion.
        drinkMagicPotionButton.setOnAction(event -> ((Predator) object).drinkMagicPotion(object)); //When the button is clicked.
        //basic button settings
        drinkMagicPotionButton.setPrefSize(100, 20);
        drinkMagicPotionButton.setLayoutX(screenSize.getWidth() - drinkMagicPotionButton.getPrefWidth() * 1.5);
        drinkMagicPotionButton.setLayoutY(50);
        background.getChildren().add(drinkMagicPotionButton);
    }
    /**
     * the updateMap method is used to update the map and draw each animal
     * @param root the root/background.
     * @param predators the list of predators.
     * @param preys the list of preys.
     * @param marked_animal the marked animal.
     */
    private void updateMap(Group root, LinkedList<Predator> predators, LinkedList<Prey> preys, Object marked_animal) {
        markNearest(root, marked_animal); //Marks the nearest animal.
        for (int i = 0; i < root.getChildren().size(); i++) {
            try {
                root.getChildren().removeIf(child -> child instanceof ImageView && child.getId().equals("animal")); //Removes all the animals from the map.
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
        for (Prey prey : preys) {
            if (Objects.equals(prey.getName(), "Baba/Prey")) { //If the prey is a baba.
                drawAnimal(root, prey, "images/prey1.png"); //Draws the baba.
            }else if (Objects.equals(prey.getName(), "Captain Redhead/Prey")) { //If the prey is a captain redhead.
                drawAnimal(root, prey, "images/prey2.png"); //Draws the captain redhead.
            }else{
                drawAnimal(root, prey, "images/prey3.png"); //Draws the daughter.
            }
        }
        for (Predator predator : predators) {
            if (Objects.equals(predator.getName(), "Asterix/Predator")) { //If the predator is asterix.
                drawAnimal(root, predator, "images/asterix.png"); //Draws asterix.
            }else{
                drawAnimal(root, predator, "images/obelix.png"); //Draws obelix.
            }
        }
    }
    /**
     * drawSpots method is used to draw the spots on the map.
     * @param root the root.
     * @param population the population of the spots.
     * @param imageFile the image file.
     */
    private void drawSpots(Group root, Population population, String imageFile) {
        for (int i = 0; i < population.getPopulationSize(); i++) {
            int x = population.getObj(i).getX(); //The x position of the spot.
            int y = population.getObj(i).getY(); //The y position of the spot.
            draw(imageFile, root, x, y, "spot", 32, 32); //Draws the spot.
        }
    }
    /**
     * drawAnimal method is used to draw the animals on the map.
     * @param root the root/background.
     * @param animal the animal that is going to be drawn.
     * @param imageFile the image file.
     */
    private void drawAnimal(Group root, Animal animal, String imageFile) { //dont like this method, maybe change it. Use draw() instead.
        int x = animal.getX();
        int y = animal.getY();
        draw(imageFile, root, x, y, "animal", 32, 32);
    }
    /**
     * draw method is used to draw the image on the map.
     * @param imageFile the image file.
     * @param root the root/background.
     * @param x the x position of the image.
     * @param y the y position of the image.
     * @param id the id of the image.
     * @param width the width of the image.
     * @param height the height of the image.
     */
    private void draw(String imageFile, Group root, int x, int y, String id, float width, float height) { //Draws an image.
        Image image = new Image(imageFile);
        ImageView imageView = new ImageView(image);
        //basic image settings
        imageView.setId(id);
        imageView.setX(x - width / 2);
        imageView.setY(y - height / 2);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        root.getChildren().add(imageView); //Adds the image to the map.
    }
    /**
     * findNearest method is used to find the nearest animal.
     * @param x the x position of mouse click.
     * @param y the y position of mouse click.
     * @param predators the list of predators.
     * @param preys the list of preys.
     * @param spots the list of spots.
     * @return the nearest Object. Can be a predator, prey or spot (the name isn't accurate, but it is important to me to keep it this way).
     */
    private Object findNearestAnimal(double x, double y, LinkedList<Predator> predators, LinkedList<Prey> preys, LinkedList<Spot> spots) {
        //finds the nearest animal to the given coordinates and returns it.
        double minDistance = Double.MAX_VALUE;
        Object nearestObject = null;
        LinkedList<Object> list = new LinkedList<>();
        list.addAll(predators);
        list.addAll(preys);
        list.addAll(spots);
        for (Object object : list) {
            double distance = calculateDistances(x, y, object.getX(), object.getY());
            if (distance < minDistance) {
                minDistance = distance;
                nearestObject = object;
            }
        }
        return nearestObject;
    }
    /**
     * markNearest method is used to mark the nearest animal.
     * @param root the root/background.
     * @param nearestObject the nearest animal.
     */
    private void markNearest(Group root, Object nearestObject){//marks the nearest object differently. Depends on the type of the object.
        root.getChildren().removeIf(child -> child instanceof Circle); //Removes the old mark.
        if (nearestObject != null) {
            if (nearestObject instanceof Spot) { //If the nearest object is a spot.
                Circle circle = new Circle(nearestObject.getX(), nearestObject.getY(), 5);
                circle.setFill(Color.RED);
                root.getChildren().add(circle); //Adds the new mark.
            }else if (nearestObject instanceof Predator && (((Predator) nearestObject).isAlive()) || (nearestObject instanceof Prey) && (((Prey) nearestObject).isAlive())) {
                Circle circle = new Circle(nearestObject.getX(), nearestObject.getY(), 17);
                circle.setFill(Color.TRANSPARENT);
                circle.setStrokeWidth(3);
                if (nearestObject instanceof Predator) {
                    if (((Predator) nearestObject).mode) {
                        circle.setStroke(Color.RED);
                        root.getChildren().add(circle);
                    } else {
                        circle.setStroke(Color.CHARTREUSE);
                        root.getChildren().add(circle);
                    }
                } else {
                    circle.setStroke(Color.YELLOW);
                    root.getChildren().add(circle);
                }
            }
        }
    }
    /**
     * displayInfo method is used to display the information about the nearest animal. It is displayed in the GUI.
     * @param infoPanel the info panel.
     * @param nearestObject the nearest Object. Used to display the information about it.
     * @param spots the list of spots.
     * @param predators the list of predators.
     * @param preys the list of preys.
     */
    private void displayInfo(VBox infoPanel, Object nearestObject, LinkedList<Spot> spots, LinkedList<Predator> predators, LinkedList<Prey> preys) {
        infoPanel.getChildren().clear(); //Clears the info panel.
        //display num of water sources, food sources, hideout, predators, preys
        if (nearestObject != null) { //If there is a nearest object.
            infoPanel.getChildren().clear(); //Clears the info panel.
            //display num of water sources, food sources, hideout, predators, preys
            Label numSpots = new Label("Number of spots: " + spots.size());
            Label numPredators = new Label("Number of predators: " + predators.size());
            Label numPreys = new Label("Number of preys: " + preys.size());
            infoPanel.getChildren().addAll(numSpots, numPredators, numPreys);
            //add line to separate info
            Line line = new Line();
            line.setStartX(0);
            line.setStartY(0);
            line.setEndX(infoPanel.getPrefWidth());
            line.setEndY(0);
            infoPanel.getChildren().add(line);
            //display info of the nearest object
            Label nameID = new Label("ID: " + nearestObject.getID() + " Name: " + nearestObject.getName());
            Label position = new Label("Position: (" + nearestObject.getX() + ", " + nearestObject.getY() + ")");
            infoPanel.getChildren().addAll(nameID, position);
            if (nearestObject instanceof Spot) { //If the nearest object is a spot.
                Label capacity = new Label("Capacity: " + ((Spot) nearestObject).getCapacity() + "/" + ((Spot) nearestObject).getMaxCapacity());
                infoPanel.getChildren().addAll(capacity);
            } else { //If the nearest object is an animal.
                Label speed = new Label("Speed: " + Math.round(((Animal) nearestObject).getSpeed()));
                infoPanel.getChildren().addAll(speed);
                if (nearestObject instanceof Predator predator) { //If the nearest object is a predator.
                    Label strength = new Label("Strength: " + Math.round(predator.getStrength()));
                    infoPanel.getChildren().addAll(strength);
                } else { //If the nearest object is a prey.
                    Prey prey = (Prey) nearestObject;
                    Label stats = new Label("HP: " + Math.round(prey.getHealth()) + "   wLevel: " + Math.round(prey.getWaterLevel()) + "   fLevel: " + Math.round(prey.getFoodLevel()));
                    infoPanel.getChildren().addAll(stats);
                }
            }
        }
    }
    /**
     * findMinIndex method is used to find the index of the minimum value in an array.
     * @param distances the array of distances.
     * @return the index of the minimum value in the array.
     */
    private int findMinIndex(double[] distances) { //Finds the index of the minimum value in the array.
        int minIndex = 0;
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < distances[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
    /**
     * calculateDistances method is used to calculate the distance between two points.
     * @param x the x coordinate of the first point.
     * @param y the y coordinate of the first point.
     * @param x1 the x coordinate of the second point.
     * @param y1 the y coordinate of the second point.
     * @return the distance between the two points.
     */
    private double calculateDistances(double x, double y, double x1, double y1) {
        //calculates the distance between two points.
        return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
    }
    /**
     * calculateDistances method is used to calculate the distance between Object and each spot from a given Population
     * @param obj the Object.
     * @param population2 the Population.
     * @return the distance between the Object and each spot from the Population.
     */
    private double[] calculateDistances(Object obj, Population population2) { //calculates the distances between an object and a population.
        double[] distances = new double[population2.getPopulationSize()];
        for (int i = 0; i < population2.getPopulationSize(); i++) {
            distances[i] = calculateDistances(obj.getX(), obj.getY(), population2.getObj(i).getX(), population2.getObj(i).getY());
        }
        return distances;
    }
    /**
     * todo: add AddRemoveAllButton method
     * @param background the background.
     * @param predators the list of predators.
     * @param preys the list of preys.
     * @param screenSize the size of the screen.
     */
    private void addRemoveAllButton(Group background, LinkedList<Predator> predators, LinkedList<Prey> preys, Rectangle2D screenSize) {
        Button removeAllButton = new Button("Remove All");
        removeAllButton.setOnAction(event -> {
            //TODO: remove all animals
            removeAll(background, predators, preys);
        });
        removeAllButton.setLayoutX(screenSize.getWidth() - 100);
        removeAllButton.setLayoutY(screenSize.getHeight() - 100);
        background.getChildren().add(removeAllButton);
    } //TODO: remove all animals
    /**
     * removeAll method is used to remove all the animals from the GUI.
     * @param background the background.
     * @param predators the list of predators.
     * @param preys the list of preys.
     *              TODO: remove all animals
     */
    private void removeAll(Group background, LinkedList<Predator> predators, LinkedList<Prey> preys) {
        //remove all animals //TODO: remove all animals
    } //TODO: remove all animals
}