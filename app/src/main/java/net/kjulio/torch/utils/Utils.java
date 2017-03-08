package net.kjulio.torch.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public class Utils {

    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void snackbar(View v, String text) {
        Snackbar.make(v, text, Snackbar.LENGTH_LONG).show();
    }

}
