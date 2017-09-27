package com.mcp.smyrilline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)

public class ExampleUnitTest {

    @Mock
    JSONObject jsonObject;

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);     // junit
        assertThat(4, equalTo(2 + 2));   // hamcrest
    }

    @Test
    public void restaurantHasTitleProperty() {
        Integer[] ints = new Integer[]{7, 5, 12, 16};
        assertThat(4, equalTo(2 + 2));
    }
}