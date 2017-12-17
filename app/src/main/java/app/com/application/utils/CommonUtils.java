package app.com.application.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.support.annotation.NonNull;

/**
 * Created by admin on 17/12/17.
 */

public class CommonUtils {

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }



    @NonNull
    public static String getPathName() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/fetch";
    }

}
