package spots;
import objects.Hideout;
import objects.Object;
import java.util.LinkedList;
import java.util.List;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The HideoutPopulation class.
 * The HideoutPopulation class is used to create a population of hideouts.
 */
public class HideoutPopulation extends Population { //The HideoutPopulation class.
    private final List<Hideout> HideoutPopulation = new LinkedList<>(); //The list of hideouts in the population.
    public HideoutPopulation(int populationSize) { //The constructor for the HideoutPopulation class.
        super(populationSize); //Calls the constructor of the Population class.
        for (int i = 0; i < populationSize; i++) { //Loops through the population size.
            Hideout hideout = new Hideout(100); //Creates a new hideout.
            for (Hideout h : HideoutPopulation) {   //Loops through the list of hideouts.
                if (hideout.getDistance(h) < 100) { //If the distance between the hideout and the hideout in the list is less than 100, the index is decreased and the loop is broken.
                    i--;
                    break;
                }
            }
            HideoutPopulation.add(hideout); //Adds the hideout to the list of hideouts.
        }
    }
    public Object getObj(int i) {return HideoutPopulation.get(i);} //Returns the object at the specified index.
}