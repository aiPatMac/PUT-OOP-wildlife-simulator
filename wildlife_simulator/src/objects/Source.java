package objects;
/**
 * @author: Patryk Maciejewski
 * @version: 1.0
 * @date: 2022-01-16
 */
/**
 * The Source class.
 * sources are the spots where prey can eat and drink.
 */
public class Source extends Spot { //Source is a subclass of Spot.
    public Source(boolean type, int maxCapacity) {
        super(maxCapacity);
        if (type) { //If the type is true, the name is set to "Water".
            this.name = "Rum Barrel/Water Source";
        } else { //If the type is false, the name is set to "Food".
            this.name = "Orange/Food Source";
        }
    }
}