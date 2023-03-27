package objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.LinkedList;
import java.util.Objects;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Predator class.
 */
public class Predator extends Animal implements Runnable { //The Predator class.
    private final MediaPlayer asterix = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/music/asterix.mp3")).toExternalForm())); //The asterix sound for magic potion
    private final MediaPlayer obelix = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/music/obelix.mp3")).toExternalForm())); //The obelix sound for magic potion
    public boolean mode = true; //The mode of the predator. If the mode is true, predator attacks. If the mode is false, predator relaxes.
    private float strength; //The strength of the predator. Used to determine the amount of damage done to prey.
    private final LinkedList<Prey> preys; //The list of preys. Used to determine the closest prey.
    public Predator(LinkedList<Prey> preys) { //The constructor for the Predator class.
        super(); //Calls the constructor of the Animal class.
        String[] names = {"Asterix/Predator", "Obelix/Predator"};
        this.name = names[(int) (Math.random() * 2)]; //Sets the name of the predator to a random name from the names array.
        this.preys = preys; //Sets the list of preys to the list of preys passed in the constructor.
        this.strength = (float) (Math.random() * 50 + 10); //Sets the strength of the predator to a random number between 0 and 100.
        if (this.name.equals("Obelix/Predator")) { //If the name of the predator is "Obelix/Predator", the predator is stronger.
            this.strength *= 1.5; //Doubles the strength of the predator.
            this.speed *= 1.5; //Doubles the speed of the predator.
        }
    }
    /**
     * The run method.
     * The method that is called when the thread is started.
     * It makes the predator move around the map
     * First, it chooses a target prey.
     * Then, it chases the prey.
     * Tries to attack the prey and if it succeeds, it eats the prey.
     * If there is no prey, it relaxes.
     */
    @Override
    public void run() {
        while (alive) { //While the predator is alive.
            Prey target = findTarget(); //Finds the target.
            chase(target); //Chases the target.
            attack(target, preys); //Attacks the target.
            walk(); //Walks.
        }
    }
/**
     * The findTarget method.
     * Finds the closest prey.
     * @return the closest prey.
     */
    private Prey findTarget() { //Finds the closest prey.
        Prey target = null;
        int minDistance = Integer.MAX_VALUE;
        for (Prey p : preys) {
            int dx = p.getX() - x;
            int dy = p.getY() - y;
            int distance = dx * dx + dy * dy;
            if (distance < minDistance && !p.isHiding()) {
                target = p;
                minDistance = distance;
            }
        }
        return target;
    }
    /**
     * The chase method.
     * Makes the predator chase the prey.
     * @param target the prey to chase.
     */
    private void chase(Prey target) {
        if (target != null) {
            int dx = target.getX() - x; //The difference between the x coordinates of the predator and the target.
            int dy = target.getY() - y; //The difference between the y coordinates of the predator and the target.
            if (dx != 0) {
                x += dx / Math.abs(dx); //Moves the predator in the x direction.
            }
            if (dy != 0) {
                y += dy / Math.abs(dy); //Moves the predator in the y direction.
            }
        } else {
            relax(); //Relaxes.
        }
    }
    /**
     * The attack method.
     * Makes the predator attack the prey.
     * @param target the prey to attack.
     * @param preys the list of preys.
     */
    private void attack(Prey target, LinkedList<Prey> preys) {
        if (target != null) {
            if (Math.abs(target.getX() - x) <= 5 && Math.abs(target.getY() - y) <= 5) { //If the predator is close enough to the target.
                target.getAttacked(strength); //The target gets attacked.
                System.out.println(this.name + " attacked " + target.name + " and caused " + this.strength + " damage");
                if (target.getHealth() <= 0) { //If the target is dead.
                    preys.remove(target); //Removes the target from the list of preys.
                    target.die(); //Calls the die method of the target.
                    System.out.println(this.getName() + " killed " + target.getName());
                }
                relax(); //Relaxes.
            }
        }
    }
    /**
     * The relax method.
     * Makes the predator relax.
     * The predator stops attacking
     */
    private void relax() { //Relaxes.
        this.mode = false; //Sets the mode to false.
        for (int i = 0; i < (int) (Math.random() * 90 + 10); i++) { //Waits for a random amount of time.
            x += (int) (Math.random() * 3)-1; //Moves the predator in the x direction.
            y += (int) (Math.random() * 3)-1; //Moves the predator in the y direction.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mode = true; //Changes the mode of the predator to attack.
    }

    /**
     * The drinkPotion method.
     * Boosts the strength and the speed of the predator.
     * @param object
     */
    public void drinkMagicPotion(Object object) { //Drinks the magic potion. boost stats.
        if (Objects.equals(object.name, "Asterix/Predator")) {  //If the object is Asterix.
            this.strength *= 2;
            this.speed *= 2;
            asterix.play();
        }else if (Objects.equals(object.name, "Obelix/Predator")) {
            System.out.println("Obelix can't drink the magic potion"); //Obelix can't drink the magic potion.
            obelix.play();
        }
    }
    public float getStrength() {
        return strength;
    }

    public void die() {
        this.alive = false;
    }
}