package ua.net.tnt;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileDataSource implements DataSource {
    private String dataSourceFileName;
    private ArrayList<BaseBike> data;
    //cursor points to position which will be printed next time
    private int cursor = 0;
    //modify flag - set on after add data
    boolean modifyFlag;
    //while search set data lock to avoid adding new data
    boolean dataLocked;


    public FileDataSource(String dataSourceFileName) throws IOException {
        this.dataSourceFileName = dataSourceFileName;
        data = load();
    }

    private BaseBike.TYPE getType(String[] values) {
        int POS_BRAND = 0;
        if (values[POS_BRAND].indexOf("FOLDING BIKE") == 0) {
            return BaseBike.TYPE.FOLDING;
        } else if (values[POS_BRAND].indexOf("SPEEDELEC") == 0) {
            return BaseBike.TYPE.SPEEDELECS;
        } else return BaseBike.TYPE.ELECTRIC;
    }

    public ArrayList<BaseBike> load() throws IOException {
        ArrayList<BaseBike> result = new ArrayList<BaseBike>();
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(dataSourceFileName))
                .withCSVParser(parser).build();

        String[] values;

        while ((values = csvReader.readNext()) != null) {
            for (int i = 0; i < values.length; i++) values[i] = values[i].trim();

            BaseBike.TYPE type = getType(values);

            BaseBike bike = null;
            if (type == BaseBike.TYPE.FOLDING) {
                bike = new FoldingBike(values);
            } else if (type == BaseBike.TYPE.SPEEDELECS) {
                bike = new SpeedElecByke(values);
            } else bike = new EByke(values);
            bike.setPositionInList(result.size());

            result.add(bike);
        }

        return result;
    }

    @Override
    public ArrayList<BaseBike> getData() {
        return data;
    }

    @Override
    public ArrayList<String> next(int count) {
        ArrayList<String> result = new ArrayList<String>();
        //cursor points to position which will appears next iteration
        if (cursor > data.size() - 1) return result;

        int end = Integer.min(data.size() - 1, cursor + count - 1);
        for (int i = cursor; i <= end; i++) {
            result.add(Integer.toString(i + 1) + ")    " + data.get(i).toString());
        }
        cursor = Integer.min(data.size() - 1, end + 1);
        return result;
    }

    @Override
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void resetCursor() {
        cursor = 0;
    }

    @Override
    public void addBike(BaseBike bike) {
        if (dataIsLocked()) {
            System.out.println("WARNING! Data is locked. Operation not completed");
            return;
        }
        data.add(bike);
        setModify(true);
        bike.setPositionInList(data.size() - 1);
    }

    @Override
    public boolean isModify() {
        return modifyFlag;
    }

    @Override
    public void setModify(boolean modifyFlag) {
        this.modifyFlag = modifyFlag;
    }

    @Override
    public void search(BaseBike key) {
        dataLocked = true;

        Runnable process = new Runnable() {
            @Override
            public void run() {
                ComparatorBike comparatorBike = new ComparatorBike(key);
                Collections.sort(data, comparatorBike);
                //int result = Collections.binarySearch(data, key, comparatorBike);
                int result = searchForFirst(key, comparatorBike);
                System.out.println("-----------------------------------");
                if (result > 0) System.out.println("Item found:" + data.get(result));
                else System.out.println("Item not found");
                System.out.println("-----------------------------------");
                dataLocked = false;
            }
        };

        process.run();

    }

    @Override
    public boolean dataIsLocked() {
        return dataLocked;
    }

    @Override
    public void write() throws IOException {
        ComparatorBike comparatorBike = new ComparatorBike(null);
        Collections.sort(data, comparatorBike);

        ArrayList<String> csvData = new ArrayList<String>();
        for (BaseBike bike : data) csvData.add(bike.asCSV());

        File dataFile = new File(dataSourceFileName);
        if (!dataFile.exists() || !dataFile.canWrite()) {
            throw new IOException("\"File is not accesseable: \" + dataSourceFileName");
        }

        FileWriter writer = new FileWriter(dataSourceFileName);
        for (String s : csvData) writer.write(s + "\r\n");
        writer.close();

        setModify(false);
    }

    int searchForFirst(BaseBike key, Comparator<BaseBike> comparator){
        int min = 0;
        int max = data.size() - 1;
        int lastMatch = -1;
        int compareResult = -1;
        while(true){
            compareResult = -1;
            if(min == max && min+1 == max) {
                compareResult = comparator.compare(data.get(min), key);
                if (compareResult == 0){ lastMatch = min; break; }
            }

            if(min+1 == max){
                compareResult = comparator.compare(data.get(max), key);
                if (compareResult == 0){ lastMatch = max; break; }
            }

            int pos = (max + min) / 2;
            BaseBike bike = data.get(pos);
            compareResult = comparator.compare(bike, key);
            if(compareResult == 0){ lastMatch = pos; max = pos; }
            if(compareResult >= 0){ max = pos; }
            else min = pos;
        }

        return compareResult  == 0 ? lastMatch : -lastMatch;
    }

    class ComparatorBike implements Comparator<BaseBike> {
        boolean restoreOriginalPosition;
        boolean compareWithLight;
        boolean compareWithWeight;
        boolean compareWithColor;
        boolean compareWithPrice;
        boolean compareWithBrand;
        boolean compareWithSize;
        boolean compareWithGears;
        boolean compareWithSpeed;
        boolean compareWithCapacity;

        public ComparatorBike(BaseBike key) {
            //use key with null value if we wish only restore original(in data file) position
            if(null == key){
                restoreOriginalPosition = true;
                return;
            }

            compareWithColor = key.color != null;
            compareWithLight = key.light != null;
            compareWithPrice = key.price != null;
            compareWithWeight = key.weight != null;
            compareWithBrand = key.brand != null;

            if(key instanceof FoldingBike){
                compareWithSize = ((FoldingBike) key).size != null;
                compareWithGears = ((FoldingBike) key).gears != null;
            }else if(key instanceof SpeedElecByke){
                compareWithSpeed = ((SpeedElecByke) key).speed != null;
                compareWithCapacity = ((SpeedElecByke) key).capacity != null;
            }else if(key instanceof EByke){
                compareWithSpeed = ((EByke) key).speed != null;
                compareWithCapacity = ((EByke) key).capacity != null;
            }
        }

        @Override
        public int compare(BaseBike o1, BaseBike o2) {
            if(restoreOriginalPosition){
                return o1.getPositionInList().compareTo(o2.getPositionInList());
            }

            //compare with type of bike
            Integer w1 = (o1.type == BaseBike.TYPE.FOLDING ? 3 : (o1.type == BaseBike.TYPE.SPEEDELECS ? 2 : 1));
            Integer w2 = (o2.type == BaseBike.TYPE.FOLDING ? 3 : (o2.type == BaseBike.TYPE.SPEEDELECS ? 2 : 1));
            Integer result = w1.compareTo(w2);
            if (result != 0) return result;

            //in beginning we compere bikes with common attributes:
            //type, brand, price, light, weight, color

            //compare with brand
            if (compareWithBrand) {
                result = o1.brand.toLowerCase().compareTo(o2.brand.toLowerCase());
                if (result != 0) return result;
            }

            //compare with price
            if (compareWithPrice) {
                result = o1.price.compareTo(o2.price);
                if (result != 0) return result;
            }

            //compare with light
            if (compareWithLight) {
                result = o1.light.compareTo(o2.light);
                if (result != 0) return result;
            }

            //compare with weight
            if (compareWithWeight) {
                result = o1.weight.compareTo(o2.weight);
                if (result != 0) return result;
            }

            //compare with color
            if (compareWithColor) {
                result = o1.color.toLowerCase().compareTo(o2.color.toLowerCase());
                if (result != 0) return result;
            }

            //second comparison uses type-defined attributes
            //and this point we sure that o1 and o2 are the same type
            if(o1 instanceof FoldingBike) {
                if (compareWithSize) {
                    result = ((FoldingBike) o1).size.compareTo(((FoldingBike) o2).size);
                    if (result != 0) return result;
                }
                if (compareWithGears) {
                    result = ((FoldingBike) o1).gears.compareTo(((FoldingBike) o2).gears);
                    if (result != 0) return result;
                }
            }else if(o1 instanceof SpeedElecByke){
                if(compareWithSpeed){
                    result = ((SpeedElecByke) o1).speed.compareTo(((SpeedElecByke) o2).speed);
                    if (result != 0) return result;
                }

                if(compareWithCapacity){
                    result = ((SpeedElecByke) o1).capacity.compareTo(((SpeedElecByke) o2).capacity);
                    if (result != 0) return result;
                }
            }else if(o1 instanceof EByke){
                if(compareWithSpeed){
                    result = ((EByke) o1).speed.compareTo(((EByke) o2).speed);
                    if (result != 0) return result;
                }

                if(compareWithCapacity) {
                    result = ((EByke) o1).capacity.compareTo(((EByke) o2).capacity);
                    if (result != 0) return result;
                }
            }

            //also arrange by original position in file
            if( o2.getPositionInList() != null )
                result = o1.getPositionInList().compareTo(o2.getPositionInList());

            return result;
        }
    }

}
