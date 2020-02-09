package ua.net.tnt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoldingBike extends BaseBike {
    //additional folder comparatively to parent
    Integer size;
    Integer gears;

    public FoldingBike(String[] values) {
        super(TYPE.FOLDING, values, FoldingBike.getValuesDescription());
        Map<String, Integer> valuesDescription = FoldingBike.getValuesDescription();
        this.gears = Integer.parseInt(values[valuesDescription.get("GEARS")]);
        this.size = Integer.parseInt(values[valuesDescription.get("SIZE")]);
    }

    public FoldingBike(String brand, Boolean light, Integer weight, String color, Integer price,
            Integer size, Integer gears){

        super(TYPE.FOLDING, brand, light, weight, color, price);
        this.size = size;
        this.gears = gears;
    }


    protected static Map<String, Integer> getValuesDescription() {
        HashMap<String, Integer> result = new HashMap<String, Integer>(){
            {
                put("BRAND",    0);
                put("SIZE",     1);
                put("GEARS",    2);
                put("WEIGHT",   3);
                put("LIGHT",    4);
                put("COLOR",    5);
                put("PRICE",    6);
            }
        };

        return result;
    }

    @Override
    public String toString(){
        return "FOLDING BIKE"
                .concat(" ").concat(brand).concat(" with ").concat(gears.toString()).concat(" gear(s)")
                .concat(" and ").concat(light ? "" : "no ")
                .concat("head/tail lights").concat("\r\n").concat("Price ")
                .concat(price.toString()).concat(" euros.");
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof FoldingBike) ) return false;
        return type == ((FoldingBike)obj).type && brand.equalsIgnoreCase(((FoldingBike)obj).brand)
                && light.equals(((FoldingBike)obj).light) && weight.equals(((FoldingBike)obj).weight)
                && color.equalsIgnoreCase(((FoldingBike)obj).color) && gears.equals(((FoldingBike)obj).gears)
                && size.equals(((FoldingBike)obj).size);
    }

    @Override
    public String asCSV() {
        return new StringBuilder("FOLDING BIKE ").append(brand).append(";").append(size).append(";")
                .append(gears).append(";").append(weight).append(";")
                .append(Boolean.toString(light).toUpperCase()).append(";")
                .append(color).append(";").append(price).toString();
    }

    public static ArrayList<PropertyDescription> getPropertiesDescription(){
        ArrayList<PropertyDescription> result = BaseBike.getPropertiesDescription();
        result.add(new PropertyDescription(Integer.class, "size","Size of wheels, inch(10-50 range)", 10, 50,
                "Please, enter the number from 10 to 50"));
        result.add(new PropertyDescription(Integer.class, "gears","Number of gears", 1, 100,
                "Please, enter the number from 1 to 100"));

        return result;
    }

}
