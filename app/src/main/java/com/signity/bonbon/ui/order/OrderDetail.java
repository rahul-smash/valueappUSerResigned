package com.signity.bonbon.ui.order;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.model.FixedTaxDetail;
import com.signity.bonbon.model.OrderHistoryItemModel;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.model.TaxDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity implements View.OnClickListener {

    ListView order_history_list;
    TextView no_record,currencyTxt,rs1,rs2,rs3;

    TextView textAddress, textCheckOut, textDiscount, textShipping, textTOtal, textlblNote, textNote,taxLblText,taxVal;

    ImageView ic_down_up;

    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    Adapter adapter;
    String userId;
    PrefManager prefManager;
    OrderHistoryModel orderHistoryModel;
    List<OrderHistoryItemModel> listOrderHistoryItemModel;
    Button backButton;

    Animation slideUpAnim;
    Animation slideDownAnim;
    RelativeLayout layout_total,taxlayout;
    String isTaxEnable,taxLabel,taxRate;
    LinearLayout linearFixedTaxLayout,linearTaxLayout,linearFixedTaxLayoutDisable;

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
        linearFixedTaxLayout=(LinearLayout)findViewById(R.id.linearFixedTaxLayout);
        linearTaxLayout=(LinearLayout)findViewById(R.id.linearTaxLayout);
        linearFixedTaxLayoutDisable=(LinearLayout)findViewById(R.id.linearFixedTaxLayoutDisable);

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
            } else {
                order_history_list.setVisibility(View.GONE);
                no_record.setVisibility(View.VISIBLE);
            }
        }


        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            currencyTxt.setText(unescapeJavaString(currency));
            rs1.setText(unescapeJavaString(currency));
            rs2.setText(unescapeJavaString(currency));
            rs3.setText("- "+unescapeJavaString(currency));
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
        textTOtal.setText(orderHistoryModel.getTotalOrderVal());
        textCheckOut.setText(orderHistoryModel.getCheckOut());
        textDiscount.setText(orderHistoryModel.getDiscount());
        textShipping.setText(orderHistoryModel.getShipping());
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
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + fixedTaxDetail.get(i).getFixedTaxAmount());
                linearFixedTaxLayout.addView(child);
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
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + fixedTaxDetail.get(i).getFixedTaxAmount());
                linearFixedTaxLayoutDisable.addView(child);
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
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                tax_label.setText("" + detailsList.get(i).getLabel()+"("+detailsList.get(i).getRate()+"%)");
                tax_value.setText("" + detailsList.get(i).getTax());
                linearTaxLayout.addView(child);
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
                holder.order_number_rupya.setText(unescapeJavaString(currency));
            }
            else {
                holder.order_number_rupya.setText(currency);
            }



            if (list.getStatus().equals("0")) {
                holder.status.setText("Pending");
            } else if (list.getStatus().equals("1")) {
                holder.status.setText("Approved");
            } else if (list.getStatus().equals("2")) {
                holder.status.setText("Rejected");
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
        }
    }

    public String unescapeJavaString(String st) {

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

}
