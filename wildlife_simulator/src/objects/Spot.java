package objects;
import java.util.LinkedList;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */

/**
 * The Spot class.
 * Spot is a class that is used to create spots.
 * Spots are the places where prey can eat, drink, reproduce and hide. It is a parent class for the Source and Hideout
 */
public abstract class Spot extends Object {
    LinkedList<Path> paths = new LinkedList<>(); //The list of available paths.
    public int maxCapacity;
    public int currentCapacity;
    public Spot(int maxCapacity) { //The constructor for the Spot class.
        super(); //Calls the constructor of the Object class.
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
    }
    public void addPath(Path path) {paths.add(path);} //Adds a path to the list of paths.
    public LinkedList<Path> getPaths() {
        return paths;
    }
    public int getCapacity() {
        return currentCapacity;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    /**
     * join and leave methods get care of the limited capacity problem
     */
    public synchronized void join() { //Adds one to the spot.
        if (currentCapacity < maxCapacity) {
            currentCapacity++; //Adds one to the current capacity of the spot.
        }else{ //If the current capacity is equal to the max capacity, the thread waits.
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void leave() {  //Removes one from the spot.
        if (currentCapacity > 0) {
            currentCapacity--;
        }
    }
}