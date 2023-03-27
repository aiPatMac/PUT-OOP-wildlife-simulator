package objects;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Object class. This class is the superclass of all objects in the simulation.
 * It contains the methods and variables that are common to all objects, such as
 * the name, the x and y coordinates, id. It also contains the setters and getters
 */
public abstract class Object {//The base class for all objects in the game
    private static final Rectangle2D screenSize = Screen.getPrimary().getVisualBounds(); //Screen size to generate random coordinates
    private static final int width = (int) screenSize.getWidth();
    private static final int height = (int) screenSize.getHeight();
    private static int nextID = 0; //next ID to be assigned, starts at 0, increments by 1, used to assign ID to each object
    private final int id; //ID of object
    protected int x;
    protected int y;
    protected String name;

    public Object() {
        this.id = nextID++; //Assigns ID to object
        this.x = (int) ((Math.random() * width * 0.1) + (Math.random() * width * 0.6)); //Generates random x coordinate
        this.y = (int) ((Math.random() * height * 0.1) + (Math.random() * height * 0.8)); //Generates random y coordinate
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getID() {
        return id;
    }

    public float getDistance(Object o) {
        return (float) Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.y - o.y, 2));
    } // get distance between two objects

    public String getName() {
        return this.name;
    }
}