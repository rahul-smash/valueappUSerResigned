package com.signity.bonbon.ui.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.model.FixedTaxDetail;
import com.signity.bonbon.model.Gst;
import com.signity.bonbon.model.OrderHistoryItemModel;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.model.ProductsWIthTax;
import com.signity.bonbon.model.TaxDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity implements View.OnClickListener {

    ListView order_history_list;
    TextView no_record,currencyTxt,rs1,rs2,rs3;

    TextView textAddress, textCheckOut, textDiscount, textShipping, textTOtal, textlblNote, textNote,taxLblText,taxVal,shipping_charges_text,discountLblText;

    ImageView ic_down_up;

    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    Adapter adapter;
    String userId;
    PrefManager prefManager;
    OrderHistoryModel orderHistoryModel;
    List<OrderHistoryItemModel> listOrderHistoryItemModel;
    Button backButton, bill_detail_btn;

    Animation slideUpAnim;
    Animation slideDownAnim;
    RelativeLayout layout_total,taxlayout,shipping_layout,discount_layout, gst_layout;
    String isTaxEnable,taxLabel,taxRate;
    LinearLayout linearFixedTaxLayout,linearTaxLayout,linearFixedTaxLayoutDisable;
    Dialog taxDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.activity_order_detail);

        textAddress = (TextView) findViewById(R.id.deliveryAddress);
        textCheckOut = (TextView) findViewById(R.id.items_price);
        textShipping = (TextView) findViewById(R.id.shipping_charges);
        textDiscount = (TextView) findViewById(R.id.discountVal);
        textTOtal = (TextView) findViewById(R.id.total);
        textlblNote = (TextView) findViewById(R.id.lblNote);
        textNote = (TextView) findViewById(R.id.note);
        currencyTxt=(TextView)findViewById(R.id.currency);
        shipping_charges_text=(TextView)findViewById(R.id.shipping_charges_text);
        discountLblText=(TextView)findViewById(R.id.discountLblText);
        shipping_layout=(RelativeLayout)findViewById(R.id.shipping_layout);
        discount_layout=(RelativeLayout)findViewById(R.id.discount_layout);
        linearFixedTaxLayout=(LinearLayout)findViewById(R.id.linearFixedTaxLayout);
        linearTaxLayout=(LinearLayout)findViewById(R.id.linearTaxLayout);
        linearFixedTaxLayoutDisable=(LinearLayout)findViewById(R.id.linearFixedTaxLayoutDisable);
        gst_layout=(RelativeLayout)findViewById(R.id.gst_layout);

        rs1=(TextView)findViewById(R.id.rs1);
        rs2=(TextView)findViewById(R.id.rs2);
        rs3=(TextView)findViewById(R.id.rs3);
//        rs4=(TextView)findViewById(R.id.rs4);

        slideUpAnim = AnimationUtils.loadAnimation(OrderDetail.this
                .getApplicationContext(), R.anim.slide_up_activity);
        slideDownAnim = AnimationUtils.loadAnimation(OrderDetail.this
                .getApplicationContext(), R.anim.slide_down_activity);
        layout_total = (RelativeLayout) findViewById(R.id.layout_total);

        ic_down_up = (ImageView) findViewById(R.id.ic_down_up);
        ic_down_up.setOnClickListener(this);

        prefManager = new PrefManager(this);
        order_history_list = (ListView) findViewById(com.signity.bonbon.R.id.order_history_list);
        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        bill_detail_btn=(Button)findViewById(R.id.bill_detail_btn);
        bill_detail_btn.setOnClickListener(this);
        no_record = (TextView) findViewById(com.signity.bonbon.R.id.no_record);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        orderHistoryModel = DataAdapter.getInstance().getOrderHistoryModel();
        if (orderHistoryModel != null) {

            updateUiBlock(orderHistoryModel);
            updateTaxPrice(orderHistoryModel);

            listOrderHistoryItemModel = orderHistoryModel.getOrderItems();
            if (listOrderHistoryItemModel != null && listOrderHistoryItemModel.size() != 0) {
                order_history_list.setVisibility(View.VISIBLE);
                no_record.setVisibility(View.GONE);
                adapter = new Adapter(this, listOrderHistoryItemModel);
                order_history_list.setAdapter(adapter);

                try {
                    if(listOrderHistoryItemModel.get(0).getGst()!=null && listOrderHistoryItemModel.get(0).getGst().size()!=0){
                        gst_layout.setVisibility(View.VISIBLE);
                    }else {
                        gst_layout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gst_layout.setVisibility(View.GONE);
                }

            } else {
                order_history_list.setVisibility(View.GONE);
                no_record.setVisibility(View.VISIBLE);
                gst_layout.setVisibility(View.GONE);
            }
        }


        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            currencyTxt.setText(Util.unescapeJavaString(currency));
            rs1.setText(Util.unescapeJavaString(currency));
            rs2.setText(Util.unescapeJavaString(currency));
            rs3.setText("- "+Util.unescapeJavaString(currency));
//            rs4.setText(unescapeJavaString(currency));
        }
        else {
            currencyTxt.setText(currency);
            rs1.setText(currency);
            rs2.setText(currency);
            rs3.setText("- "+currency);
//            rs4.setText(currency);
        }

    }

    private void updateTaxPrice(OrderHistoryModel orderHistoryModel) {

        List<FixedTaxDetail> listWithTaxEnable=new ArrayList<>();
        List<FixedTaxDetail> listWithTaxdisable=new ArrayList<>();

        if(orderHistoryModel.getStoreFixedTaxDetail()!=null && orderHistoryModel.getStoreFixedTaxDetail().size()!=0){

            for(FixedTaxDetail fixedTaxDetail:orderHistoryModel.getStoreFixedTaxDetail()){
                if(fixedTaxDetail.getIsTaxEnable().equalsIgnoreCase("1")){
                    listWithTaxEnable.add(fixedTaxDetail);
                }
                else {
                    listWithTaxdisable.add(fixedTaxDetail);
                }
            }

        }

        addTaxRow(orderHistoryModel.getCalculatedTaxDetail());
        addFixedTaxRowEnable(listWithTaxEnable);
        addFixedTaxRowDisable(listWithTaxdisable);

    }

    private void updateUiBlock(OrderHistoryModel orderHistoryModel) {

        double totalPrice = Double.parseDouble(orderHistoryModel.getTotalOrderVal());
        double checkOut = Double.parseDouble(orderHistoryModel.getCheckOut());
        double discount = Double.parseDouble(orderHistoryModel.getDiscount());
        double shippingCharge = Double.parseDouble(orderHistoryModel.getShipping());

        textTOtal.setText("" + String.format("%.2f", totalPrice));
        textCheckOut.setText("" + String.format("%.2f", checkOut));
        textDiscount.setText("" + String.format("%.2f", discount));
        textShipping.setText("" + String.format("%.2f", shippingCharge));

        if (shippingCharge == 0.0) {
            shipping_charges_text.setVisibility(View.GONE);
            shipping_layout.setVisibility(View.GONE);
        } else {
            shipping_charges_text.setVisibility(View.VISIBLE);
            shipping_layout.setVisibility(View.VISIBLE);
        }

        if (discount == 0.0) {
            discountLblText.setVisibility(View.GONE);
            discount_layout.setVisibility(View.GONE);
        } else {
            discountLblText.setVisibility(View.VISIBLE);
            discount_layout.setVisibility(View.VISIBLE);
        }

        textAddress.setText(orderHistoryModel.getAddress());
        if (orderHistoryModel.getNote() != null && !orderHistoryModel.getNote().isEmpty()) {
            textNote.setText(orderHistoryModel.getNote());
        } else {
            textNote.setVisibility(View.GONE);
            textlblNote.setVisibility(View.GONE);
        }
    }


    private void addFixedTaxRowEnable(List<FixedTaxDetail> fixedTaxDetail) {
        if(!fixedTaxDetail.isEmpty()){

            for (int i = 0; i < fixedTaxDetail.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_detail_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(Util.unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(fixedTaxDetail.get(i).getFixedTaxAmount());
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + String.format("%.2f", tax));
                if(tax!=0.0){
                    linearFixedTaxLayout.addView(child);
                }
            }
        }

    }

    private void addFixedTaxRowDisable(List<FixedTaxDetail> fixedTaxDetail) {

        if(!fixedTaxDetail.isEmpty()){

            for (int i = 0; i < fixedTaxDetail.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_detail_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(Util.unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(fixedTaxDetail.get(i).getFixedTaxAmount());
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + String.format("%.2f", tax));
                if(tax!=0.0){
                    linearFixedTaxLayoutDisable.addView(child);
                }


            }
        }

    }


    private void addTaxRow(List<TaxDetails> detailsList) {

        if(!detailsList.isEmpty()){

            for (int i = 0; i < detailsList.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_detail_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(Util.unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(detailsList.get(i).getTax());


                if(detailsList.get(i).getRate().isEmpty() || detailsList.get(i).getRate().equalsIgnoreCase("0")){
                    tax_label.setText("" + detailsList.get(i).getLabel());
                }else {
                    tax_label.setText("" + detailsList.get(i).getLabel() + "(" + detailsList.get(i).getRate() + "%)");
                }

                tax_value.setText("" + String.format("%.2f", tax));
                if(tax!=0.0){
                    linearTaxLayout.addView(child);
                }
            }
        }

    }


    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<OrderHistoryItemModel> listOrderHistoryItemModel;

        public Adapter(Activity context, List<OrderHistoryItemModel> liseOrderHistoryModel) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listOrderHistoryItemModel = liseOrderHistoryModel;

        }

        @Override
        public int getCount() {
            return listOrderHistoryItemModel.size();
        }

        @Override
        public Object getItem(int position) {
            return listOrderHistoryItemModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(com.signity.bonbon.R.layout.order_detail_history_child, null);
                holder = new ViewHolder();
                holder.order_date = (TextView) convertView.findViewById(com.signity.bonbon.R.id.order_date);
                holder.order_date.setTypeface(typeFaceRobotoRegular);
                holder.order_number = (TextView) convertView.findViewById(com.signity.bonbon.R.id.order_number);
                holder.order_number.setTypeface(typeFaceRobotoRegular);
                holder.ship = (TextView) convertView.findViewById(com.signity.bonbon.R.id.ship);
                holder.ship.setTypeface(typeFaceRobotoRegular);
                holder.quantity = (TextView) convertView.findViewById(com.signity.bonbon.R.id.quantity);
                holder.quantity.setTypeface(typeFaceRobotoRegular);
                holder.status = (TextView) convertView.findViewById(com.signity.bonbon.R.id.status);
                holder.status.setTypeface(typeFaceRobotoRegular);
                holder.order_number_rupya=(TextView)convertView.findViewById(R.id.order_number_rupya);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            OrderHistoryItemModel list = listOrderHistoryItemModel.get(position);

            holder.order_date.setText(list.getProductName());
            holder.order_number.setText(list.getPrice());
            holder.ship.setText(list.getWeight() + " " + list.getUnitType());
            holder.quantity.setText(list.getQuantity());


            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.order_number_rupya.setText(Util.unescapeJavaString(currency));
            }
            else {
                holder.order_number_rupya.setText(currency);
            }



            if (list.getStatus().equals("0")) {
                holder.status.setText(getString(R.string.str_pending));
            } else if (list.getStatus().equals("1")) {
                holder.status.setText(getString(R.string.str_approved));
            } else if (list.getStatus().equals("2")) {
                holder.status.setText(getString(R.string.str_rejected));
            }  else {
                holder.status.setVisibility(View.GONE);
            }

            return convertView;
        }


        class ViewHolder {
            TextView order_date;
            TextView order_number;
            TextView ship;
            TextView status;
            TextView quantity,order_number_rupya;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(OrderDetail.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_down_up:
                if (!ic_down_up.isSelected()) {
                    ic_down_up.setSelected(true);
                    layout_total.startAnimation(slideUpAnim);
                    layout_total.setVisibility(View.VISIBLE);
                } else {
                    ic_down_up.setSelected(false);
                    layout_total.startAnimation(slideDownAnim);
                    layout_total.setVisibility(View.GONE);
                }
                break;
            case R.id.bill_detail_btn:
                showTaxDetail(this, listOrderHistoryItemModel);
                break;
        }
    }

    public void showTaxDetail(Context context, List<OrderHistoryItemModel> productListWithTax){
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

    private void addData(Context context, TableLayout mainTable, List<OrderHistoryItemModel> productListWithTax) {

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

    private void addHeaders(Context context, TableLayout mainTable) {

        PrefManager prefManager=new PrefManager(context);
        String currency;
        if (prefManager.getSharedValue(AppConstant.CURRENCY).contains("\\")) {
            currency=Util.unescapeJavaString(prefManager.getSharedValue(AppConstant.CURRENCY));
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
