package ua.net.tnt;

import org.apache.commons.cli.*;
import org.apache.commons.validator.routines.IntegerValidator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App{
    private static Options options = new Options();
    private static DataSource dataSource;
    static String dataFileName;
    static Integer viewPortLength;
    static BeEvents event = BeEvents.EVENT_SHOW_ENTIRE_CATALOG;

    public static void main( String[] args ){
        if(!init( args )) {
            new HelpFormatter().printHelp("App", options);
            System.exit(1);
        }
        if(readData(dataFileName) < 1)  System.exit(2);
        start();
    }

    /**
     * This function analyses program arguments and checks they on
     * Legal arguments are: -f pathToEcobikeFile -l countlinesPrintedOnScreen
     * @param args String[]
     * @return boolean result of checking arguments
     */
    private static boolean init(String[] args){
        options.addOption("f", true,"path to data-file");
        options.addOption("l", true,"count rows printed on screen, default 10");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse( options, args);
            dataFileName = cmd.getOptionValue("f");
            if(null == dataFileName) return false;

            String val = cmd.getOptionValue("l");
            if(null == val || val.isEmpty()) viewPortLength = 10;
            else{
                viewPortLength = IntegerValidator.getInstance().validate(val);
                if( null == viewPortLength || viewPortLength < 2 ){
                    System.out.println( "Parsing failed.  Reason: \"l\" must be great then 1" );
                    return false;
                }
            }

            System.out.println("BeBike program is starting...");
            System.out.println("Data file is: " + dataFileName);
            File dataFile = new File(dataFileName);
            if( !(dataFile.exists() && dataFile.canRead() && dataFile.canWrite()) ){
                System.out.println("Data file can't be used: " + dataFileName); return false;
            }
        } catch (ParseException e){
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
            return false;
        }

        return true;
    }

    /**
     * This function read file to an array of BaseBike objects
     * @param filename String
     * @return int count reading rows from the data file or -1 if any error was detected
     */
    static int readData(String filename){
        try{
            System.out.println("Reading data file...");
            dataSource = new FileDataSource(filename);
            System.out.println("Reading data file...complete");
        }catch(IOException e){
            e.printStackTrace();
            return -1;
        }

        return dataSource.getSize();
    }

    /**
     * return object who implements DataSource interface
     * @return DataSource
    */
    static DataSource getDataSource(){return dataSource;}

    private static void printHeader(){
        System.out.println(decorLine);
        if(event == BeEvents.EVENT_ADD_FOLDER_BIKE) {
            System.out.println("Creating of new \"Folder bike\"");
        }else if(event == BeEvents.EVENT_ADD_EBIKE){
            System.out.println("Creating of new \"Electric bike\"");
        }else if(event == BeEvents.EVENT_ADD_SPEEDELEC){
            System.out.println("Creating of new \"Speedelec bike\"");
        }
        System.out.println(decorLine);
     }

    /**
     * Print on-screen main options of user choice. Then waiting for valid user input, and switch event variable.
     */
    private static void mainMenuPrint(){
        System.out.println("1-Show the entire EcoBike catalog");
        System.out.println("2–Add a new folding bike");
        System.out.println("3–Add a new speedelec");
        System.out.println("4–Add a new e-bike");
        System.out.println("5–Find the first item of a particular brand");
        System.out.println("6–Write to file");
        System.out.println("7–Stop the program");

        String input = getValidInput("[1234567]", "Enter your choice(1-7):");

        switch(input){
            case "1" : {event = BeEvents.EVENT_SHOW_ENTIRE_CATALOG; break;}
            case "2" : {event = BeEvents.EVENT_ADD_FOLDER_BIKE; break;}
            case "3" : {event = BeEvents.EVENT_ADD_SPEEDELEC; break;}
            case "4" : {event = BeEvents.EVENT_ADD_EBIKE; break;}
            case "5" : {event = BeEvents.EVENT_SEARCH; break;}
            case "6" : {event = BeEvents.EVENT_WRITE; break;}
            case "7" : {event = BeEvents.EVENT_EXIT; break;}
            default  : {event = BeEvents.EVENT_MAIN_MENU; break;}
        }
    }

    /**
     * This function starts loop of asking to enter values which describe bike.
     * It must be called from code when we need searching or adding bike
     * @param type define type of bike for searching or adding
     * @param canBeEmpty true for searching operation and false for adding
     * @return UserEnteredValues set of entered in console parameters for search or adding
     */
    private static UserEnteredValues askBikeProperties(BaseBike.TYPE type, boolean canBeEmpty){
        //new empty set of values
        UserEnteredValues result = new UserEnteredValues();
        printHeader();

        //get set of values who needs to be filled with user input
        ArrayList<BaseBike.PropertyDescription> propertyDescriptions = null;
        if(type == BaseBike.TYPE.FOLDING ) propertyDescriptions = FoldingBike.getPropertiesDescription();
        else if(type == BaseBike.TYPE.ELECTRIC ) propertyDescriptions = EByke.getPropertiesDescription();
        else if(type == BaseBike.TYPE.SPEEDELECS ) propertyDescriptions = SpeedElecByke.getPropertiesDescription();

        Scanner scanner = new Scanner(System.in);

        int iteration = 0;
        boolean breakFlag = false;
        //Loop by each item description and prompt user for input valid value.
        // There is an option if we do not need any value(if we use a searching operation).
        // In this case, the user can skip input.
        while(iteration < propertyDescriptions.size()){
            Boolean bVal = null;
            String sVal = null;
            Integer iVal = null;

            BaseBike.PropertyDescription description = propertyDescriptions.get(iteration);
            System.out.print(description.title + "(\"q\" for break):");
            String input = scanner.nextLine().trim();

            if(input.equalsIgnoreCase("q") ) {breakFlag = true; break;}

            if(!input.isEmpty() || !canBeEmpty){
                //input must be valid
                if (description.propClass == Boolean.class) {
                    if ("y".equals(input.toLowerCase())) bVal = true;
                    else if ("n".equals(input.toLowerCase())) bVal = false;
                    else {
                        System.out.println(description.wrongMessage);
                        continue;
                    }
                } else if (description.propClass == Integer.class) {
                    iVal = IntegerValidator.getInstance().validate(input);
                    if (null == iVal) {
                        System.out.println(description.wrongMessage);
                        continue;
                    } else if (description.integerMinValue != 0 || description.integerMaxValue != 0) {
                        if (iVal < description.integerMinValue || iVal > description.integerMaxValue) {
                            System.out.println(description.wrongMessage);
                            continue;
                        }
                    }
                } else if (description.propClass == String.class) {
                    sVal = input;
                    if (sVal.isEmpty()) {
                        System.out.println(description.wrongMessage);
                        continue;
                    }
                }
            }

            switch (description.name){
                case "brand"   :   {result.brand = sVal; break; }
                case "light"   :   {result.light = bVal; break; }
                case "weight"  :   {result.weight = iVal; break; }
                case "color"   :   {result.color = sVal; break; }
                case "price"   :   {result.price = iVal; break; }
                case "size"    :   {result.size = iVal; break; }
                case "gears"   :   {result.gears = iVal; break; }
                case "capacity":   {result.capacity = iVal; break; }
                case "speed":   {result.speed = iVal; break; }
            }
            iteration++;
        }

        if(!canBeEmpty && breakFlag) event = BeEvents.EVENT_MAIN_MENU;;

        return result;
    }

    /**
     * This function prompts the user for input values that description a new bike.
     * @param type BaseBike.TYPE type of bike to add
     */
    private static void addBikePrint(BaseBike.TYPE type){
        UserEnteredValues values = askBikeProperties(type, false);
        if(event == BeEvents.EVENT_MAIN_MENU) return;

        String input = getValidInput("[yn]", "Want you add new bike to the list?(y|n)");
        if(!input.equals("y")) {event = BeEvents.EVENT_MAIN_MENU; return; }

        BaseBike bike = null;
        if(type == BaseBike.TYPE.FOLDING ){
            bike = new FoldingBike(values.brand, values.light, values.weight, values.color, values.price, values.size, values.gears);
        }else if(type == BaseBike.TYPE.ELECTRIC ){
            bike = new EByke(values.brand, values.light, values.weight, values.color, values.price, values.speed, values.capacity);
        }else if(type == BaseBike.TYPE.SPEEDELECS ){
            bike = new SpeedElecByke(values.brand, values.light, values.weight, values.color, values.price, values.speed, values.capacity);
        }

        if(bike != null) dataSource.addBike(bike);

        event = BeEvents.EVENT_MAIN_MENU;
    }

    /**
     * This is the main cycle of the program live. Each case invokes its
     * subprocess and as result switch event variable.
     */
     private static void start() {
         event = BeEvents.EVENT_SHOW_ENTIRE_CATALOG;
         while (true) {
             if (event == BeEvents.EVENT_MAIN_MENU) {
                 mainMenuPrint();
             }
             else if (event == BeEvents.EVENT_SHOW_ENTIRE_CATALOG) {
                 bikeListPrint();
             }
             else if (event == BeEvents.EVENT_ADD_FOLDER_BIKE) {
                 addBikePrint(BaseBike.TYPE.FOLDING);
             }
             else if (event == BeEvents.EVENT_ADD_EBIKE) {
                 addBikePrint(BaseBike.TYPE.ELECTRIC);
             }
             else if (event == BeEvents.EVENT_ADD_SPEEDELEC) {
                 addBikePrint(BaseBike.TYPE.SPEEDELECS);
             }
             else if (event == BeEvents.EVENT_WRITE) {
                 writeFile();
             }
             else if (event == BeEvents.EVENT_SEARCH){
                 search();
             }
             else if (event == BeEvents.EVENT_EXIT) {
                 close();
             }
             else if(event == BeEvents.EVENT_EXIT_IMMEDIATELY){
                 System.out.println("Program closed"); break;
             }
         }
     }

    /**
     * Print on-screen list of bikes. Each page has number of rows that was set as argument -l
     */
    private static void bikeListPrint(){
        ArrayList<String> viewPort = dataSource.next(viewPortLength);
        for (String line : viewPort) {
            System.out.println(line);
        }
        System.out.println(decorLine);

        String input = getValidInput("[nq]", "Total rows "
                .concat(Integer.toString(dataSource.getSize())).concat(". ").concat(nextList));
        if( input.equals("q") ){
            dataSource.resetCursor();
            event = BeEvents.EVENT_MAIN_MENU;
        }
    }

    /**
     * This function uses Java.Util.Scanner for reads the user's input
     * and match it with legal variants. If the input does not match then asks for input again
     * @param regexp String
     * @param wellcome String prompt for input value
     * @return
     */
     private static String getValidInput(String regexp, String wellcome){
        Scanner scanner = new Scanner(System.in);
        while( true ){
            System.out.print(wellcome);
            String input = scanner.nextLine().trim();
            if(input.matches(regexp)) {
                return input;
            }else{
                System.out.println();
            }
        }
     }

    /**
     * It invokes the function that writes the existing array of BaseBike objects
     * to file on the disk. Before writing, the array will be ordered by the
     * original position in the data file.
     * @return boolean result of operation
     */
     private static boolean writeFile(){
         try {
             dataSource.write();
         } catch (IOException e) {
             e.printStackTrace();
             return false;
         }
         printInForm("Data saved in file:" + dataFileName);
         event = BeEvents.EVENT_MAIN_MENU;
         return true;
     }

    /**
     * Attempt to close the program. If there if modified data then prompt for the user to save the data
     */
    private static void close(){
        if(dataSource.isModify()){
            String input = getValidInput("[yn]", "Data is modify. Save the changes?(y|n)");
            if(input.equals("y")) event = BeEvents.EVENT_WRITE;
            else event = BeEvents.EVENT_EXIT_IMMEDIATELY;
        }else{
            event = BeEvents.EVENT_EXIT_IMMEDIATELY;
        }
     }

     private static void printInForm(String message){
         System.out.println();
         System.out.println(decorLine);
         System.out.println(message);
         System.out.println(decorLine);
         System.out.println();
     }

    /**
     * Prompt user for input values that will be used for searching bike
     */
    private static void search(){
        BaseBike.TYPE type;
        printInForm("Searching bike. Please enter type of searching  bike:");
        String bikeType = getValidInput("[123]", "Which type of bike do you want to seach? 1 - folding, 2 - speeelec, 3 - e-bike:");
        switch(bikeType){
            case "1" : {type = BaseBike.TYPE.FOLDING; break;}
            case "2" : {type = BaseBike.TYPE.SPEEDELECS; break;}
            default  : {type = BaseBike.TYPE.ELECTRIC; break;}
        }

        System.out.println("Please, enter other search catteries. Use empty for no use criteria");
        UserEnteredValues values = askBikeProperties(type, true);
        System.out.println("Staring background searching....");
        startSearch(type, values);
        event = BeEvents.EVENT_MAIN_MENU;
     }

    /**
     * This function invokes the searching process from object implements DataSource interface.
     * The searching process uses thread. User input still doesn't block but data is locked for adding new items.
     */
    private static void startSearch(BaseBike.TYPE type, UserEnteredValues values){
        BaseBike bike = null;
        if(type == BaseBike.TYPE.FOLDING ){
            bike = new FoldingBike(values.brand, values.light, values.weight, values.color, values.price, values.size, values.gears);
        }else if(type == BaseBike.TYPE.ELECTRIC ){
            bike = new EByke(values.brand, values.light, values.weight, values.color, values.price, values.speed, values.capacity);
        }else if(type == BaseBike.TYPE.SPEEDELECS ){
            bike = new SpeedElecByke(values.brand, values.light, values.weight, values.color, values.price, values.speed, values.capacity);
        }

        System.out.println("Searching started in backgound mode...");
        System.out.println("Adding new data is locked!");
        dataSource.search(bike);
    }

    private enum BeEvents{
        EVENT_MAIN_MENU,
        EVENT_SHOW_ENTIRE_CATALOG,
        EVENT_ADD_FOLDER_BIKE,
        EVENT_ADD_EBIKE,
        EVENT_ADD_SPEEDELEC,
        EVENT_SEARCH,
        EVENT_WRITE,
        EVENT_EXIT,
        EVENT_BREAK,
        EVENT_EXIT_IMMEDIATELY;
    }

    private static class UserEnteredValues{
        String brand = null;
        Boolean light = null;
        Integer weight = null;
        String color = null;
        Integer price = null;
        Integer size = null;
        Integer gears = null;
        Integer capacity = null;
        Integer speed = null;
    }

    private static final String decorLine = "-----------------------------------";
    private static final String nextList = "Press \"n\" to next portion of list or \"q\" to break:";
}
