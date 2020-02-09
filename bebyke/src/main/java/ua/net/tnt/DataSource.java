package ua.net.tnt;

import java.io.IOException;
import java.util.ArrayList;

public interface DataSource {
    /**
     * Return array of objects BaseBike.
     * This uses a cursor property of DataSource object to slide over whole data
     * @return
     */
    ArrayList<BaseBike> getData();

    /**
     * return list of strings prepared to print on-screen
     * every next time function return next portion of data
     * @param count int number of needed rows
     * @return ArrayList<String>
     */
    ArrayList<String> next(int count);

    /**
     * length of data
     * @return int
     */
    int getSize();

    /**
     *Return the cursor to start position
     */
    void resetCursor();

    /**
     * Adds BaseBike to data. It also set modify flag to DataSource object
     * @param bike BaseBike
     */
    void addBike(BaseBike bike);

    /**
     * Return Return flag of modifying data
     * @return boolean
     */
    boolean isModify();

    /**
     * Set modifying flag of data
     * @param modifyFlag boolean
     */
    void setModify(boolean modifyFlag);

    /**
     * Start searching for a  bike object in the data.
     * use program thread so program stay available for user input,
     * but data will be locked to adding
     * @param key BaseBike
     */
    void search(BaseBike key);

    /**
     * Return flag of locking of the data
     * @return boolean
     */
    boolean dataIsLocked();

    /**
     * Save data to file
     * @throws IOException
     */
    void write() throws IOException;
}
