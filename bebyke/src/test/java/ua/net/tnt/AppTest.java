package ua.net.tnt;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import org.hamcrest.core.*;

import javax.xml.crypto.Data;

import static org.hamcrest.CoreMatchers.is;

public class AppTest{

    @Test
    public void initShouldReturnTrue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m;
        App app = new App();

        System.out.println("****initShouldReturnTrue");

        //passed test
        String[] args = new String[]{"-f", "C:\\downloads\\ecobike.txt", "-l", "15"};
        m = app.getClass().getDeclaredMethod("init", String[].class);
        m.setAccessible(true);
        Assert.assertEquals (true, m.invoke(app, new Object[]{args}));

        //test on file-path
        String[] args1 = new String[]{"-f", "C:\\downloads\\file_not_exist.txt", "-l", "15"};
        m = app.getClass().getDeclaredMethod("init", String[].class);
        m.setAccessible(true);
        Assert.assertNotEquals (true, m.invoke(app, new Object[]{args1}));

        //test on count row on screen
        String[] args2 = new String[]{"-f", "C:\\downloads\\ecobike.txt", "-l", "1"};
        m = app.getClass().getDeclaredMethod("init", String[].class);
        m.setAccessible(true);
        Assert.assertNotEquals (true, m.invoke(app, new Object[]{args2}));

        //test on read file
        readDataWouldReturn1000(App.dataFileName);

        //test on adding-writing-reading cycle
        lastThreeBikesWouldBeEq();
    }

    public void readDataWouldReturn1000(String filename) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("****readDataWouldReturnGreatThenZero");
        int res = App.readData(filename);
        Assert.assertThat("from file must be written 1001 row", res, is(1000));
    }

    public void lastThreeBikesWouldBeEq(){
        FoldingBike fb = new FoldingBike("Benetti", false, 11400, "rose", 1009, 24, 27);
        SpeedElecByke sb = new SpeedElecByke("E-Scooter", false, 14800, "marine", 309, 60, 15300);
        EByke eb = new EByke("Lankeleisi", false, 10000, "black", 2399, 65, 24200);

        DataSource ds = App.getDataSource();
        //add three bykes
        ds.addBike(fb);
        ds.addBike(sb);
        ds.addBike(eb);

        //invoke search for sorting data
        //long now = new Date().getTime();
        ds.search(ds.getData().get(0));
        while( ds.dataIsLocked() );

        //sorting complete
        //writing
        try {
            ds.write();
            //then read file again
            ds = new FileDataSource(App.dataFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BaseBike b;
        boolean res;
        //thirty bike from end must be Folding bike
        b = ds.getData().get(ds.getSize()-3);
        res = b.equals(fb);
        Assert.assertEquals(true, res);

        //second bike from end must be speedelec bike
        b = ds.getData().get(ds.getSize()-2);
        res = b.equals(sb);
        Assert.assertEquals(true, res);

        //first bike from end must be e-bike bike
        b = ds.getData().get(ds.getSize()-1);
        res = b.equals(eb);
        Assert.assertEquals(true, res);

    }

}
