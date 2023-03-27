package spots;
import objects.Object;
import java.util.LinkedList;
import java.util.List;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Population class.
 * The Population class is a parent class for the SourcePopulation and HideoutPopulation classes.
 */
public class Population { //The Population class.
    private final int populationSize; //The size of the population.
    public List<Object> Population = new LinkedList<>(); //The list of objects in the population.
    public Population(int populationSize) {this.populationSize = populationSize;} //The constructor for the Population class.
    public int getPopulationSize() {
        return populationSize;
    } //Returns the size of the population.
    public Object getObj(int i) {
        return Population.get(i);
    } //Returns the object at the specified index.
}