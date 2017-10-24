package com.mcp.smyrilline.util;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by raqib on 10/19/15.
 */
public final class InternalStorage {

    private InternalStorage() {
    }

    public static void writeObject(Context context, String key, Object object) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(key, Context.MODE_PRIVATE);

        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(Context context, String key) {
        Object object = null;
        try {
            FileInputStream fis = context.openFileInput(key);
            ObjectInputStream ois = new ObjectInputStream(fis);
            object = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
