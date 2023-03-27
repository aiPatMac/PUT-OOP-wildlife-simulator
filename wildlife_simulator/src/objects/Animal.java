package objects;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */

/** Class Animal is the superclass of all animals.
 * It contains the methods and variables that are common to all animals.
 * It also contains the setters and getters for the variables.
 */
public abstract class Animal extends Object { //Animal is an abstract class, meaning it can't be instantiated. It's a template for other classes.
    protected boolean alive = true; //All animals start alive.
    protected double speed; //The speed of the animal.
    protected Animal() { //The constructor for the Animal class.
        super();
        this.speed = (Math.random() * 5 + 5); //Sets the speed of the animal to a random number between 5 and 10.
    }
    public double getSpeed() {
        return speed;
    } //Returns the speed of the animal.
    /**
     * The move method. This method is used when the animal moves.
     * It is used to wait for a certain amount of time, and then move the animal.
     */
    protected void walk() { //The walk method. Time between steps.
        try {
        Thread.sleep((long) (300 / getSpeed()));
    } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public boolean isAlive() {
        return alive;
    } //Returns the alive boolean.
}