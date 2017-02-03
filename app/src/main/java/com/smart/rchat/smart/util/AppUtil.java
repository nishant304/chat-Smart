package com.smart.rchat.smart.util;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by nishant on 31.01.17.
 */

public class AppUtil {

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }
}
