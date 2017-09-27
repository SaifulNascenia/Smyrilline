package com.mcp.smyrilline;

import android.support.test.runner.AndroidJUnit4;
import com.mcp.smyrilline.model.Passenger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest_Passenger {

    private static final String EXPECTED_NAME = "Buhl";
    private static final String EXPECTED_SEX = "Male";
    private static final String EXPECTED_DOB = "25/01/1955";  //  dd/MM/yyyy
    private static final String EXPECTED_COUNTRY = "Denmark";
    private Passenger passenger;

    @Before
    public void setUp() throws Exception {
        passenger = new Passenger("Buhl", "Male", "25/01/1955", "Denmark");
    }

    @Test
    public void testPassengerDetails() throws Exception {
        Assert.assertEquals(EXPECTED_NAME, passenger.getName());
        Assert.assertEquals(EXPECTED_SEX, passenger.getSex());
        Assert.assertEquals(EXPECTED_DOB, passenger.getDOB());
        Assert.assertEquals(EXPECTED_COUNTRY, passenger.getCountry());
    }
}
