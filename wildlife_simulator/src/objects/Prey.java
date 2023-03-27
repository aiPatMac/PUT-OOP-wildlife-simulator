package objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Prey class.
 * Prey are the objects that are being hunted by the predators.
 * Prey can eat and drink from sources and hide in hideouts.
 * Prey can also reproduce.
 * Prey can also die.
 */
public class Prey extends Animal implements Runnable { //The Prey class.
    private final LinkedList<Spot> spots; //The list of spots.
    private final LinkedList<Prey> preys;  //The list of preys.
    private final MediaPlayer die1 = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/music/die1.mp3")).toExternalForm())); //The first death sound.
    private final MediaPlayer die2 = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/music/die2.mp3")).toExternalForm())); //The second death sound.
    private Spot currentSpot;
    private float water_level;
    private float health = 100;
    private float food_level;
    private boolean hiding; //true if hiding, false if not
    private boolean scared; //true if scared, false if not
    private boolean reroute; //true if rerouting, false if not

    public Prey(LinkedList<Spot> spots, LinkedList<Prey> preys) { //The constructor for the Prey class.
        super();
        String[] names = {"Baba/Prey", "Captain Redhead/Prey", "The Daughter/Prey"};
        this.name = names[(int) (Math.random() * 3)]; //Sets the name of the animal to a random name from the names array.
        this.spots = spots;
        this.preys = preys;
        this.currentSpot = closestSpot();
        this.water_level = (int) (Math.random() * 100); //Sets the water level to a random number between 0 and 100.
        this.food_level = (int) (Math.random() * 100); //Sets the food level to a random number between 0 and 100.
    }
    /**
     * The run method.
     * The method that is called when the thread is started.
     * It makes the prey move around the map
     * and eat and drink from sources.
     * It also makes the prey hide in hideouts and reproduce.
     */
    @Override
    public void run() { //The run method.
        while (alive) {
            didIdie(); //Checks if the animal died.
            followThePath(); //Follows the path.
            stopAndSit(); //Stops and sits.
        }
    }

    private void didIdie() { //Checks if the animal died.
        if (water_level <= 0 || food_level <= 0) {
            die();
            preys.remove(this);
            Thread.currentThread().interrupt();
            System.out.println(name + " died");
        }
    }
    /**
     * followThePath method.
     * The method that makes the prey move around the map using paths
     * I'm not happy with this method, but it works. The rerouting is not perfect.
     */
    private void followThePath() { //It works, don't touch it.
        LinkedList<Path> paths = currentSpot.getPaths(); //The list of paths.
        if (paths.isEmpty()) {
            System.out.println("No paths available");
            return; //Returns.
        }
        Random random = new Random(); //Creates a new random object.
        int index = random.nextInt(paths.size());
        Path path = paths.get(index);
        for (Point point : path.points) {
            getToThePoint(point);
            if (!this.alive || this.reroute) {
                break;
            }
        }
        if (this.reroute) {
            Point closestPoint = findClosestPoint(this.x, this.y, path); //Finds the closest point.
            Path reversePath = path.reverse(closestPoint);
            for (Point point : reversePath.points) {
                getToThePoint(point);
                if (!this.alive) { //If the animal is not alive or if the animal is rerouting.
                    break;
                }
            }
        }
        if (this.reroute) { //If the animal is rerouting.
            this.reroute = false;
            this.currentSpot = closestSpot();
        } else {
            this.currentSpot = path.endSpot;
        }
    }
    /**
     * get to the point method.
     * The method that makes the prey move to a point.
     * @param point
     */
    private void getToThePoint(Point point) {
        point.acquire(); //Acquires the point.
        walk();
        this.water_level -= 0.01;
        this.food_level -= 0.01;
        this.x = point.x; //Sets the x cord
        this.y = point.y; //Sets the y cord
        point.release(); //Releases the point.
    }

    private Point findClosestPoint(int x, int y, Path path) { //Finds the closest point.
        Point closestPoint = null; //The closest point.
        int minDistance = Integer.MAX_VALUE; //The minimum distance.
        for (Point p : path.points) { //For each point in the path.
            int dx = p.x - x; //The x distance.
            int dy = p.y - y; //The y distance.
            int distance = dx * dx + dy * dy; //The distance.
            if (distance < minDistance) { //If the distance is less than the minimum distance.
                closestPoint = p; //Sets the closest point to the point.
                minDistance = distance; //Sets the minimum distance to the distance.
            }
        }
        return closestPoint; //Returns the closest point.
    }
    /**
     * stop and sit method.
     * The method that makes the prey eat and drink from sources. It also makes the prey hide in hideouts and reproduce.
     */
    private void stopAndSit() {
        currentSpot.join(); //Joins the current spot.
        if (this.currentSpot instanceof Source) { //If the current spot is a source.
            if (this.currentSpot.getName().equals("Rum Barrel/Water Source")) { //If the current spot is the rum barrel.
                while (this.water_level < 100 && this.alive) {  //While the water level is less than 100 and the animal is alive.
                    this.water_level++;
                    this.food_level -= 0.05;
                    try {
                        Thread.sleep(100); //Sleeps for 100 milliseconds.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.scared) {
                        this.scared = false; //Sets scared to false.
                        break;
                    }
                }
            } else {
                while (this.food_level < 100 && this.alive) { //While the food level is less than 100 and the animal is alive.
                    this.food_level++;
                    this.water_level -= 0.05;
                    try {
                        Thread.sleep(100); //Sleeps for 100 milliseconds.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.scared) {
                        this.scared = false; //Sets scared to false.
                        break;
                    }
                }
            }
        } else if (this.currentSpot instanceof Hideout) { //If the current spot is a hideout.
            this.hiding = true;
            while (this.food_level > 50 && this.water_level > 50 && this.alive) { //While the food level is greater than 50 and the water level is greater than 50 and the animal is alive.
                this.food_level -= 0.05;
                this.water_level -= 0.05;
                if (this.health < 100) {
                    this.health += 0.1; //Increases the health by 0.05.
                }

                try {
                    Thread.sleep(100); //Sleeps for 100 milliseconds.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (currentSpot.currentCapacity >= 2) { //If the current capacity of the hideout is greater than or equal to 2.
                    ((Hideout) currentSpot).reproduce(this, preys, spots);
                    break;
                }
            }
            this.hiding = false;
        }
        currentSpot.leave(); //Leaves the current spot.
    }

    private Spot closestSpot() { //finds the closest spot.
        Spot closestSpot = null;
        int minDistance = Integer.MAX_VALUE;
        for (Spot s : spots) {
            int dx = s.getX() - x;
            int dy = s.getY() - y;
            int distance = dx * dx + dy * dy;
            if (distance < minDistance) {
                closestSpot = s;
                minDistance = distance;
            }
        }
        return closestSpot; //Returns the closest spot.
    }

    public void teleport(Spot spot) { //The teleport method.
        this.currentSpot = spot; //Sets the current spot to the spot.
        this.x = spot.getX(); //Sets the x to the x of the spot.
        this.y = spot.getY(); //Sets the y to the y of the spot.
    }

    public float getHealth() {
        return this.health;
    } //Returns the health of the animal.

    public void teleport_randomly(LinkedList<Spot> spots) {
        this.teleport(spots.get((int) (Math.random() * spots.size()))); //Teleports the animal to the random spot.
    }
    public void die() { //The die method.
        Random random = new Random();
        int index = random.nextInt(2);
        if (index == 0) {
            die1.play(); //Plays the first death sound.
        } else {
            die2.play(); //Plays the second death sound.
        }
        this.alive = false; //Sets the alive boolean to false.
    }

    public void getAttacked(float strength) { //The getAttacked method.
        this.health -= strength;
        this.scared = true;
    }

    public void reroute() {
        this.reroute = true;
    } //Sets the rerouting boolean to true.

    public boolean isHiding() {
        return this.hiding;
    } //Returns the hiding boolean.

    public float getWaterLevel() {
        return this.water_level;
    } //Returns the water level.

    public float getFoodLevel() {
        return this.food_level;
    } //Returns the food level.
}