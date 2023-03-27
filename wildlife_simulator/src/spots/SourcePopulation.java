package spots;
import objects.Object;
import objects.Source;
import java.util.LinkedList;
import java.util.List;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The SourcePopulation class.
 * The SourcePopulation class is used to create a population of sources.
 */
public class SourcePopulation extends Population { //The SourcePopulation class.
    private final List<Source> SourcePopulation = new LinkedList<>(); //The list of sources in the population.
    public SourcePopulation(int populationSize, boolean type) { //The constructor for the SourcePopulation class.
        super(populationSize); //Calls the constructor of the Population class.
        for (int i = 0; i < populationSize; i++) { //Loops through the population size.
            Source source = new Source(type, 5); //Creates a new source.
            for (Source s : SourcePopulation) { //Loops through the list of sources.
                if (source.getDistance(s) < 100) { //If the distance between the source and the source in the list is less than 100, the index is decreased and the loop is broken.
                    i--; //Decreases the index.
                    break; //Breaks the loop.
                }
            }
            SourcePopulation.add(source); //Adds the source to the list of sources.
        }
    }
    public Object getObj(int i) {
        return SourcePopulation.get(i);
    } //Returns the object at the specified index.
}