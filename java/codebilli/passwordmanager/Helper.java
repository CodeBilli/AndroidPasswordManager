package codebilli.passwordmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import codebilli.passwordmanager.DetailsActivity;

import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ramakvid on 2/13/2017.
 */

public class Helper {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static String loadFromInternalStorage(Context ctx) {
        String json = null;
        try {
            FileInputStream fis = ctx.openFileInput("PasswordData.json");
            StringBuffer fileContent = new StringBuffer("");
            int n = 0;
            byte[] buffer = new byte[1024];
            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            json = fileContent.toString();
        } catch (Exception ex) {
        }
        return json;
    }

}
