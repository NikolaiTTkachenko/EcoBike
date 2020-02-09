package ua.net.tnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EByke extends BaseBike {
    //additional folder comparatively to parent
    Integer speed;
    Integer capacity;

    public EByke(String[] values) {
        super(TYPE.ELECTRIC, values, SpeedElecByke.getValuesDescription());
        Map<String, Integer> valuesDescription = EByke.getValuesDescription();
        this.speed = Integer.parseInt(values[valuesDescription.get("SPEED")]);
        this.capacity = Integer.parseInt(values[valuesDescription.get("CAPACITY")]);
    }

    public EByke(String brand, Boolean light, Integer weight, String color, Integer price,
            Integer speed, Integer capacity){

        super(TYPE.ELECTRIC, brand, light, weight, color, price);
        this.speed = speed;
        this.capacity = capacity;
    }

    protected static Map<String, Integer> getValuesDescription() {
        return new HashMap<String, Integer>(){
            {
                put("BRAND",    0);
                put("SPEED",    1);
                put("WEIGHT",   2);
                put("LIGHT",    3);
                put("CAPACITY", 4);
                put("COLOR",    5);
                put("PRICE",    6);
            }
        };
    }

    @Override
    public String toString(){
        return  "E-BIKE"
                .concat(" ").concat(brand).concat(" with ").concat(capacity.toString()).concat(" mAh")
                .concat(" and ").concat(light ? "" : "no ")
                .concat("head/tail lights").concat("\r\n").concat("Price ")
                .concat(price.toString()).concat(" euros.");
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof EByke) ) return false;
        return type == ((EByke)obj).type && brand.equalsIgnoreCase(((EByke)obj).brand)
                && light.equals(((EByke)obj).light) && weight.equals(((EByke)obj).weight)
                && color.equalsIgnoreCase(((EByke)obj).color) && speed.equals(((EByke)obj).speed)
                && capacity.equals(((EByke)obj).capacity);
    }

    @Override
    public String asCSV() {
        return new StringBuilder("E-BIKE ").append(brand).append(";").append(speed).append(";")
                .append(weight).append(";").append(Boolean.toString(light).toUpperCase()).append(";")
                .append(capacity).append(";").append(color).append(";")
                .append(price).toString();
    }

    public static ArrayList<PropertyDescription> getPropertiesDescription(){
        ArrayList<PropertyDescription> result = BaseBike.getPropertiesDescription();
        result.add(new PropertyDescription(Integer.class, "speed","Maximum speed, km/hour(10-100 range)", 10, 100,
                "Please, enter the number from 10 to 100"));
        result.add(new PropertyDescription(Integer.class, "capacity","Capacity of accumulator", 500, 10000,
                "Please, enter the number from 500 to 10000"));

        return result;
    }

}
