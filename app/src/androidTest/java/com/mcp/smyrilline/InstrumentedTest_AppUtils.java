package com.mcp.smyrilline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.support.test.runner.AndroidJUnit4;
import com.mcp.smyrilline.util.AppUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest_AppUtils {

    @Test
    public void capitalizedStringValidator() {
        assertThat("Text", is(AppUtils.capitalizeFirstChar("text")));
        assertThat("Text ", is(AppUtils.capitalizeFirstChar("text ")));
        assertThat(null, is(AppUtils.capitalizeFirstChar(null)));
        assertThat("", is(AppUtils.capitalizeFirstChar("")));
        assertNotEquals("text", is(AppUtils.capitalizeFirstChar("TEXT")));
    }

    // given a json string and key, proper value should be returned
    @Test(expected = JSONException.class)
    public void jsonStringValueValidator_correctKey_returnsValue() throws JSONException {
        String jsonString =
                "{  'key1'  :   \"value1\"," +
                        "   'key2'  :   \"value2\"," +
                        "   'key3'  :   null," +
                        "   'key4'  :   \"\"" +
                        "}";
        // equal check
        assertEquals("value1",
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key1"));
        assertEquals(null,
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key3"));
        assertEquals(null,
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key4"));
        assertEquals(null,
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key5"));
        assertEquals(null,
                AppUtils.getStringFromJsonObject(new JSONObject("{}"), "key6"));

        // not equal, key changed
        assertNotEquals("value1",
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key2"));
        // not equal, value changed
        assertNotEquals("value2",
                AppUtils.getStringFromJsonObject(new JSONObject(jsonString), "key1"));
        // not equal, empty string in json, expect JSONException
        assertNotEquals("value1",
                AppUtils.getStringFromJsonObject(new JSONObject(""), "key1"));
    }

    @Test
    public void noCssPropertyExistsInJson_validator() throws JSONException {
        String jsonString_withNoCss =
                "{  'key1'  :   \"value1\"," +
                        "   'key2'  :   \"value2\"," +
                        "   'key3'  :   null," +
                        "   'key4'  :   \"\"," +
                        "   'custom_fields' :   {"
                        + "'no_css': \"\", "
                        + "'Price': \"$5000\", "
                        + "'Brand': \"Mercedes benz\"}"
                        + "}";

        String jsonString_withoutNoCss =
                "{  'key1'  :   \"value1\"," +
                        "   'key2'  :   \"value2\"," +
                        "   'key3'  :   null," +
                        "   'key4'  :   \"\"," +
                        "   'custom_fields' :   {"
                        + "'Price': \"$5000\", "
                        + "'Brand': \"Mercedes benz\"}"
                        + "}";

        assertEquals(true, AppUtils.isNoCss(new JSONObject(jsonString_withNoCss)));
        assertEquals(false, AppUtils.isNoCss(new JSONObject(jsonString_withoutNoCss)));

        assertNotEquals(true, AppUtils.isNoCss(new JSONObject(jsonString_withoutNoCss)));
        assertNotEquals(false, AppUtils.isNoCss(new JSONObject(jsonString_withNoCss)));
    }
}
