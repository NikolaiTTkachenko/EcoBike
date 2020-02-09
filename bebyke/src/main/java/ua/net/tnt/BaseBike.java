package ua.net.tnt;

import java.util.ArrayList;
import java.util.Map;

/**
 * basik class for three types of bike: folding, speedelec, e-byke
 */
public class BaseBike{
    TYPE type; //type of bike
    /* five of common folder of bike*/
    String brand;
    Boolean light;
    Integer weight;
    String color;
    Integer price;

    //save original position in data-file
    //this will being used for restore list after ordering and for searching
    private Integer positionInList;

    public BaseBike(TYPE type) {
        this.type = type;
    }

    /**
     * Construct a bike with an array of string values and a descriptor of the values.
     * This need because we will construct a bike from CSV data, that has a different order of values for different types of bike
     * @param type BaseBike.TYPE of bike
     * @param values String[] values getting from csv row
     * @param valuesDescription Map<String, String> description of values
     */
    public BaseBike(TYPE type, String[] values, Map<String, Integer> valuesDescription){
        this.type = type;
        this.brand = cutBrand(values[valuesDescription.get("BRAND")]);
        this.light = Boolean.parseBoolean(values[valuesDescription.get("LIGHT")]);
        this.weight = Integer.parseInt (values[valuesDescription.get("WEIGHT")]);
        this.color = values[valuesDescription.get("COLOR")];
        this.price = Integer.parseInt(values[valuesDescription.get("PRICE")]);
    }

    /**
     *
     * @param type BaseBike.TYPE
     * @param brand String
     * @param light Boolean has or not the bike light equipment
     * @param weight Integer
     * @param color String
     * @param price Integer
     */
    public BaseBike(TYPE type, String brand, Boolean light, Integer weight, String color, Integer price){
        this.type = type;
        this.brand = brand;
        this.light = light;
        this.weight = weight;
        this.color = color;
        this.price = price;
    }

    public Integer getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(Integer positionInList) {
        this.positionInList = positionInList;
    }

    /**
     * The function return text presentation of bike
     * @return
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        switch (type){
            case FOLDING: { result.append("FOLDING BIKE"); break; }
            case SPEEDELECS: { result.append("SPEEDELEC"); break; }
            case ELECTRIC: { result.append("E-BIKE"); break; }
        }

        return result.toString();
    }

    /**
     * Cuts the brand from head of data row
     * @param dirtyString String text in format FOLDING BIKE Brompton; 20; 6; 9283; TRUE; black; 1199
     * @return
     */
    private String cutBrand(String dirtyString) {
        switch (type) {
            case FOLDING: {
                return dirtyString.substring("FOLDING BIKE".length() + 1);
            }
            case ELECTRIC: {
                return dirtyString.substring("E-BIKE".length() + 1);
            }
            default: {
                return dirtyString.substring("SPEEDELEC".length() + 1);
            }
        }
    }

    /**
     * The function return presentation of the bike in csv format
     * prepared to write in the data-file
     * @return
     */
    public String asCSV(){
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof BaseBike) ) return false;
        return type == ((BaseBike)obj).type && brand.equalsIgnoreCase(((BaseBike)obj).brand)
                && light.equals(((BaseBike)obj).light) && weight.equals(((BaseBike)obj).weight)
                && color.equalsIgnoreCase(((BaseBike)obj).color)
                && price.equals(((BaseBike)obj).price);

    }

    /**
     * The function return description of the array of string values
     * that will be used for creating a bike object
     * @return
     */
    public static ArrayList<BaseBike.PropertyDescription> getPropertiesDescription(){
        ArrayList<BaseBike.PropertyDescription> result = new ArrayList<BaseBike.PropertyDescription>();
        result.add(new PropertyDescription(String.class, "brand","Brand", 0, 0, "Not empty string is needed"));
        result.add(new PropertyDescription(Boolean.class, "light","Availability of lights(y|n)", 0, 0,
                "Valid value is \"yes\" or \"no\""));
        result.add(new PropertyDescription(Integer.class, "weight","Weight, gram", 1000, 50*1000,
                "Please, enter the number from 1000 to 50 000"));
        result.add(new PropertyDescription(String.class, "color", "Color", 0,0,
                "Not empty string is needed"));
        result.add(new PropertyDescription(Integer.class, "price", "Price, eur", 1, 9999,
                "Please enter the number in range from 1 to 9999"));
        return result;
    }

    public enum TYPE{
        FOLDING, ELECTRIC, SPEEDELECS;
    }

    static class PropertyDescription{
        Class propClass;
        String name;
        String title;
        int integerMinValue;
        int integerMaxValue;
        String wrongMessage;

        public PropertyDescription(Class propClass, String name, String title, int integerMinValue,
            int integerMaxValue, String wrongMessage){

            this.propClass = propClass;
            this.integerMaxValue = integerMaxValue;
            this.integerMinValue = integerMinValue;
            this.name = name;
            this.title = title;
            this.wrongMessage = wrongMessage;
        }

        public boolean validate(Object obj){
            return false;
        }

    }
}
