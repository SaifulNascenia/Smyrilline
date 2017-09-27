package com.mcp.smyrilline;

import android.support.test.runner.AndroidJUnit4;
import com.mcp.smyrilline.model.Bulletin;
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
public class InstrumentedTest_Bulletin {

    private static final int EXPECTED_ID = 0;
    private static final String EXPECTED_TITLE = "Test bulletin";
    private static final String EXPECTED_CONTENT = "Test content";
    private static final String EXPECTED_DATE = "Sep 02, 2016 04:15 PM";
    private static final String EXPECTED_IMAGEURL = "http://i.ytimg.com/vi/1Xu_qyeiilU/maxresdefault.jpg";
    private static final boolean EXPECTED_SEEN = false;
    private Bulletin bulletin;

    @Before
    public void setUp() {
        bulletin = new Bulletin(
                0,
                "Text bulletin", "Text content",
                "Sep 02, 2016 04:15 PM",
                "http://i.ytimg.com/vi/1Xu_qyeiilU/maxresdefault.jpg",
                false
        );
    }

    @Test
    public void testBulletinDetails() {
        Assert.assertEquals(EXPECTED_ID, bulletin.getId());
        Assert.assertEquals(EXPECTED_TITLE, bulletin.getTitle());
        Assert.assertEquals(EXPECTED_CONTENT, bulletin.getContent());
        Assert.assertEquals(EXPECTED_DATE, bulletin.getDate());
        Assert.assertEquals(EXPECTED_IMAGEURL, bulletin.getImageUrl());
        Assert.assertEquals(EXPECTED_SEEN, bulletin.isSeen());
    }
}
