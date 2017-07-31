package com.signity.bonbon.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.model.Gst;
import com.signity.bonbon.model.ProductsWIthTax;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajesh on 8/12/15.
 */
public class Util {

    public static Dialog taxDetailDialog;

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


    public static String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
// Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
// Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }


    public static void showTaxDetail(Context context, List<ProductsWIthTax> productListWithTax){
        taxDetailDialog=null;
        taxDetailDialog = new Dialog(context);
        taxDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        taxDetailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        taxDetailDialog.setContentView(R.layout.layout_tax_detail_layout);

        TableLayout mainTable = (TableLayout) taxDetailDialog.findViewById(R.id.maintable);
        addHeaders(context,mainTable);

        addData(context,mainTable,productListWithTax);

        taxDetailDialog.setCanceledOnTouchOutside(true);
        taxDetailDialog.show();
    }

    private static void addData(Context context, TableLayout mainTable, List<ProductsWIthTax> productListWithTax) {

        if(productListWithTax!=null && productListWithTax.size()!=0){

            for(int i=0; i<productListWithTax.size(); i++){

                String taxLabel1 = null, taxLabel2 = null, tax1 = null, tax2 = null, rate = null, actualPrice = null, totalPrice = null, taxType=null;
                String productName= (!productListWithTax.get(i).getProductName().isEmpty() ? productListWithTax.get(i).getProductName():"");
                List<Gst> gst = productListWithTax.get(i).getGst();
                if(!gst.isEmpty()){
                    taxLabel1=(!gst.get(0).getLable1().isEmpty() ? gst.get(0).getLable1():"");
                    taxLabel2=(!gst.get(0).getLable2().isEmpty() ? gst.get(0).getLable2():"");
                    tax1=(!gst.get(0).getTax1().toString().isEmpty() ? gst.get(0).getTax1().toString():"");
                    tax2=(!gst.get(0).getTax2().toString().isEmpty() ? gst.get(0).getTax2().toString():"");
                    rate=(!gst.get(0).getRate().isEmpty() ? gst.get(0).getRate():"");
                    actualPrice=(!gst.get(0).getActualPrice().toString().isEmpty() ? gst.get(0).getActualPrice().toString():"");
                    totalPrice=(!gst.get(0).getTotalPrice().toString().isEmpty() ? gst.get(0).getTotalPrice().toString():"");
                    taxType=(!gst.get(0).getType().toString().isEmpty() ? gst.get(0).getType().toString():"");
                }

                TableRow tr = new TableRow(context);
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                /** Creating a TextView to add to the row **/
                TextView product = new TextView(context);
                product.setText(productName+"\n("+rate+"% "+taxLabel1+"+"+taxLabel2+")");
                product.setTextColor(Color.BLACK);
                product.setGravity(Gravity.LEFT);
                product.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                product.setPadding(5, 5, 5, 5);
                product.setTextSize(9);
                tr.addView(product, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));  // Adding textView to tablerow.

                TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(0, 20, 0.03f);
                layoutParams.setMargins(0,4,0,4);
                View v1 = new View(context);
                v1.setLayoutParams(layoutParams);
                v1.setBackgroundColor(Color.LTGRAY);
                tr.addView(v1);

                /** Creating another textview **/
                TextView taxableAmount = new TextView(context);
                taxableAmount.setText(actualPrice+"\n("+taxType+")");
                taxableAmount.setTextColor(Color.BLACK);
                taxableAmount.setGravity(Gravity.LEFT);
                taxableAmount.setPadding(5, 5, 5, 5);
                taxableAmount.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                taxableAmount.setTextSize(9);
                tr.addView(taxableAmount, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f)); // Adding textView to tablerow.


                View v2 = new View(context);
                v2.setLayoutParams(layoutParams);
                v2.setBackgroundColor(Color.LTGRAY);
                tr.addView(v2);

                /** Creating another textview **/
                TextView taxRate = new TextView(context);
                taxRate.setText(tax1+" + "+tax2);
                taxRate.setTextColor(Color.DKGRAY);
                taxRate.setGravity(Gravity.LEFT);
                taxRate.setPadding(5, 5, 5, 5);
                taxRate.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                taxRate.setTextSize(9);
                tr.addView(taxRate, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f)); // Adding textView to tablerow.

                View v3 = new View(context);
                v3.setLayoutParams(layoutParams);
                v3.setBackgroundColor(Color.LTGRAY);
                tr.addView(v3);


                /** Creating another textview **/
                /*TextView taxAmout = new TextView(this);
                taxAmout.setText(tax1+" + "+tax2);
                taxAmout.setTextColor(Color.BLACK);
                taxAmout.setGravity(Gravity.CENTER_HORIZONTAL);
                taxAmout.setPadding(5, 5, 5, 0);
                taxAmout.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                taxAmout.setTextSize(9);
                tr.addView(taxAmout, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Adding textView to tablerow.
*/

                /** Creating another textview **/
                TextView total = new TextView(context);
                total.setText(totalPrice);
                total.setTextColor(Color.BLACK);
                total.setGravity(Gravity.LEFT);
                total.setPadding(5, 5, 5, 5);
                total.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                total.setTextSize(9);
                tr.addView(total, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f)); // Adding textView to tablerow.


                // Add the TableRow to the TableLayout
                mainTable.addView(tr, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));



            }
        }


    }

    private static void addHeaders(Context context, TableLayout mainTable) {

        PrefManager prefManager=new PrefManager(context);
        String currency;
        if (prefManager.getSharedValue(AppConstant.CURRENCY).contains("\\")) {
            currency=unescapeJavaString(prefManager.getSharedValue(AppConstant.CURRENCY));
        } else {
            currency=prefManager.getSharedValue(AppConstant.CURRENCY);
        }

        TableRow tr = new TableRow(context);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        /** Creating a TextView to add to the row **/
        TextView product = new TextView(context);
        product.setText(context.getResources().getString(R.string.str_products_name));
        product.setBackgroundResource(R.color.color_gray);
        product.setTextColor(Color.WHITE);
        product.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        product.setPadding(5, 5, 5, 5);
        product.setTextSize(11);
        tr.addView(product, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));  // Adding textView to tablerow.


        /** Creating another textview **/
        TextView taxableAmount = new TextView(context);
        taxableAmount.setText(context.getResources().getString(R.string.str_taxable_amt)+"("+currency+")");
        taxableAmount.setBackgroundResource(R.color.color_gray);
        taxableAmount.setTextColor(Color.WHITE);
        taxableAmount.setPadding(5, 5, 5, 5);
        taxableAmount.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        taxableAmount.setTextSize(11);
        tr.addView(taxableAmount, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f)); // Adding textView to tablerow.


//        Creating another textview
        TextView taxRate = new TextView(context);
        taxRate.setText(context.getResources().getString(R.string.str_tax_rate)+"("+currency+")");
        taxRate.setTextColor(Color.WHITE);
        taxRate.setBackgroundResource(R.color.color_gray);
        taxRate.setPadding(5, 5, 5, 5);
        taxRate.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        taxRate.setTextSize(11);
        tr.addView(taxRate, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f)); // Adding textView to tablerow.


        /** Creating another textview **/
        /*TextView taxAmout = new TextView(this);
        taxAmout.setText(getResources().getString(R.string.str_tax_amount));
        taxAmout.setTextColor(Color.BLACK);
        taxAmout.setGravity(Gravity.CENTER_HORIZONTAL);
        taxAmout.setPadding(5, 5, 5, 0);
        taxAmout.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        taxAmout.setTextSize(11);
        tr.addView(taxAmout, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Adding textView to tablerow.
*/

        /** Creating another textview **/
        TextView total = new TextView(context);
        total.setText(context.getResources().getString(R.string.str_total)+"("+currency+")");
        total.setBackgroundResource(R.color.color_gray);
        total.setTextColor(Color.WHITE);
        total.setPadding(5, 5, 5, 5);
        total.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        total.setTextSize(11);
        tr.addView(total, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f)); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        mainTable.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

       /* View v = new View(this);
        v.setBackgroundColor(R.color.color_ligh_black);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));

        tr.addView(v);*/

    }


}
