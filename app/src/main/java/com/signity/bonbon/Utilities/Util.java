package com.signity.bonbon.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajesh on 8/12/15.
 */
public class Util {


    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    public static String getTime(Date date, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
//        2015-07-28 01:18:42
        DateFormat inFormatone = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat outPutFormatone = new SimpleDateFormat(dateFormat);
//        Date date1 = null;
//        try {
//            date1 = inFormatone.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (date != null)
            return outPutFormatone.format(date);
        else
            return "";
    }

    public static boolean checkValidEmail(String email) {
        boolean isValid = false;
        String PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(email);
        isValid = matcher.matches();
        return isValid;
    }


}
