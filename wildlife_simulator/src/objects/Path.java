package objects;
import javafx.scene.shape.Line;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Path class.
 * the path class is used to create paths between spots
 * paths are used to move animals between spots
 */
public class Path { //The path class.
    LinkedList<Point> points; //The points that make up the path.
    Spot startSpot; //The spot that the path starts at.
    Spot endSpot; //The spot that the path ends at.
    public Path() {
        points = new LinkedList<>(); //Initializes the list of points.
    }
    public void addPoint(Point point) {
        points.add(point);
    } //Adds a point to the list of points.
    /**
     * The getLine method is used to get the line that represents the path
     * and transforms it into a set of Points.
     * @param line
     */
    public void addLine(Line line) {
        int x1 = (int) line.getStartX(); //Gets the x coordinate of the start of the line.
        int y1 = (int) line.getStartY(); //Gets the y coordinate of the start of the line.
        int x2 = (int) line.getEndX(); //Gets the x coordinate of the end of the line.
        int y2 = (int) line.getEndY(); //Gets the y coordinate of the end of the line.
        int dx = x2 - x1; //Gets the change in x.
        int dy = y2 - y1; //Gets the change in y.
        int length = (int) Math.sqrt(dx * dx + dy * dy); // calculate the length of the line
        for (int i = 0; i < length; i++) { //Loops through the length of the line.
            int x = x1 + (i * dx / length); //Gets the x coordinate of the point.
            int y = y1 + (i * dy / length); //Gets the y coordinate of the point.
            Point point = new Point(x, y); //Creates a new point.
            addPoint(point); //Adds the point to the list of points.
        }
    }
    public void addStartSpot(Spot spot) {
        this.startSpot = spot;
    }
    public void addEndSpot(Spot obj) {
        this.endSpot = obj;
    }
    /**
     * the reverse method is used to reverse the path, when reroute is called.
     */
    public Path reverse(Point closestPoint) { //Reverses the path.
        Path reversed = new Path(); //Creates a new path.
        reversed.addEndSpot(startSpot); //Adds the start spot to the new path.
        for (int i = points.indexOf(closestPoint); i >= 0; i--) { //Loops through the points in the path.
            reversed.addPoint(points.get(i)); //Adds the point to the new path.
        }
        return reversed; //Returns the new path.
    }
}
/**
 * The Point class.
 * The point class is used to create points.
 * Points are used to create paths.
 */
class Point { //The point class. Could be better if intersection class that extends point was made. But I don't have time and in my approach it's not needed.
    int x;
    int y;
    /**
     * the semaphore is used to prevent multiple animals from using the same point at the same time.
     */
    private final Semaphore semaphore;
    public Point(int x, int y) { //The constructor for the Point class.
        this.x = x; //Sets the x coordinate of the point.
        this.y = y; //Sets the y coordinate of the point.
        semaphore = new Semaphore(1); //Initializes the semaphore.
    }
    protected void acquire() { //Acquires the semaphore.
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void release() {semaphore.release();} //Releases the semaphore.
}