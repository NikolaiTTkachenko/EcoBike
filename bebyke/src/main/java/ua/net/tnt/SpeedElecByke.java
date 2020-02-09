package ua.net.tnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpeedElecByke extends BaseBike {
    //additional folder comparatively to parent
    Integer speed;
    Integer capacity;

    public SpeedElecByke(String[] values) {
        super(TYPE.SPEEDELECS, values, SpeedElecByke.getValuesDescription());
        Map<String, Integer> valuesDescription = SpeedElecByke.getValuesDescription();
        this.speed = Integer.parseInt(values[valuesDescription.get("SPEED")]);
        this.capacity = Integer.parseInt(values[valuesDescription.get("CAPACITY")]);
    }

    public SpeedElecByke(String brand, Boolean light, Integer weight, String color, Integer price,
                 Integer speed, Integer capacity){

        super(TYPE.SPEEDELECS, brand, light, weight, color, price);
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

    public static ArrayList<PropertyDescription> getPropertiesDescription(){
        ArrayList<PropertyDescription> result = BaseBike.getPropertiesDescription();
        result.add(new PropertyDescription(Integer.class, "speed","Maximum speed, km/hour(10-100 range)", 10, 100,
                "Please, enter the number from 10 to 100"));
        result.add(new PropertyDescription(Integer.class, "capacity","Capacity of accumulator", 500, 10000,
                "Please, enter the number from 500 to 10000"));

        return result;
    }

    @Override
    public String toString(){
        return "SPEEDELEC"
                .concat(" ").concat(brand).concat(" with ").concat(capacity.toString()).concat(" mAh")
                .concat(" and ").concat(light ? "" : "no ")
                .concat("head/tail lights").concat("\r\n").concat("Price ")
                .concat(price.toString()).concat(" euros.");
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof SpeedElecByke) ) return false;
        return type == ((SpeedElecByke)obj).type && brand.equalsIgnoreCase(((SpeedElecByke)obj).brand)
                && light.equals(((SpeedElecByke)obj).light) && weight.equals(((SpeedElecByke)obj).weight)
                && color.equalsIgnoreCase(((SpeedElecByke)obj).color) && speed.equals(((SpeedElecByke)obj).speed)
                && capacity.equals(((SpeedElecByke)obj).capacity);
    }

    @Override
    public String asCSV() {
        return new StringBuilder("SPEEDELEC ").append(brand).append(";").append(speed).append(";")
                .append(weight).append(";").append(Boolean.toString(light).toUpperCase()).append(";")
                .append(capacity).append(";").append(color).append(";")
                .append(price).toString();
    }

}
