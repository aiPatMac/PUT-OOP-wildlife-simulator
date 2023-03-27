package objects;
import java.util.LinkedList;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */

/**
 * The Hideout class.
 * Hideout is a class where preys can hide.
 */
public class Hideout extends Spot {  //Hideout is a subclass of Spot.
    public Hideout(int maxCapacity) { //The constructor for the Hideout class.
        super(maxCapacity);
        this.name = "Ship/Hideout"; //Sets the name of the hideout to "Ship/Hideout".
    }
    /**
     * The reproduce method is used to create new prey, when two prey meet.
     * @param prey
     * @param preys
     * @param spots
     * it Adds the new prey to the list of preys.
     * The prey spawns in the hideout.
     */
    protected void reproduce(Prey prey, LinkedList<Prey> preys, LinkedList<Spot> spots) { //The reproduce method.
        double reproduction_chance = 0.23;
        if (Math.random() < reproduction_chance) {
            System.out.println(prey.getName() + " Jr. was born"); //Prints that the animal reproduced.
            Prey child = new Prey(spots, preys); //Creates a new animal.
            child.teleport(this); //Teleports the animal to the hideout. Because each object has random coordinates.
            preys.add(child);
            new Thread(child).start(); //Starts the new animal.
        }
    }
}