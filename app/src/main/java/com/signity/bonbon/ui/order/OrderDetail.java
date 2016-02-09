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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.model.OrderHistoryItemModel;
import com.signity.bonbon.model.OrderHistoryModel;

import java.util.List;

public class OrderDetail extends AppCompatActivity implements View.OnClickListener {

    ListView order_history_list;
    TextView no_record;

    TextView textAddress, textCheckOut, textDiscount, textShipping, textTOtal, textlblNote, textNote;

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
    RelativeLayout layout_total;

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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            OrderHistoryItemModel list = listOrderHistoryItemModel.get(position);

            holder.order_date.setText(list.getProductName());
            holder.order_number.setText(list.getPrice());
            holder.ship.setText(list.getWeight() + " " + list.getUnitType());
            holder.quantity.setText(list.getQuantity());

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
            TextView quantity;
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
}
