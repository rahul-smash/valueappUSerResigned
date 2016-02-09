package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.GerOrderHistoryModel;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.order.OrderDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 19/10/15.
 */
public class ActiveOrderList extends Fragment {

    ListView activeOrderList;
    TextView no_record;
    View mView;
    String userId;
    PrefManager prefManager;
    GCMClientManager pushClientManager;
    List<OrderHistoryModel> liseOrderHistoryModel;

    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.active_order_list, container, false);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        typeFaceRobotoRegular = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);
        activeOrderList = (ListView) mView.findViewById(R.id.activeOrderList);
        no_record = (TextView) mView.findViewById(R.id.no_record);
        getOrderHistory();
        return mView;
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<OrderHistoryModel> liseOrderHistoryModel;

        public Adapter(Activity context, List<OrderHistoryModel> liseOrderHistoryModel) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.liseOrderHistoryModel = liseOrderHistoryModel;

        }

        @Override
        public int getCount() {
            return liseOrderHistoryModel.size();
        }

        @Override
        public Object getItem(int position) {
            return liseOrderHistoryModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.active_order_child, null);
                holder = new ViewHolder();
                holder.orderDate = (TextView) convertView.findViewById(R.id.order_date);
                holder.orderDate.setTypeface(typeFaceRobotoRegular);
                holder.orderNumber = (TextView) convertView.findViewById(R.id.order_number);
                holder.orderNumber.setTypeface(typeFaceRobotoRegular);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
                holder.btnViewOrder = (Button) convertView.findViewById(R.id.btnViewOrder);
                holder.statusDot1 = (ImageView) convertView.findViewById(R.id.status_dot1);
                holder.statusDot2 = (ImageView) convertView.findViewById(R.id.status_dot2);
                holder.statusDot3 = (ImageView) convertView.findViewById(R.id.status_dot3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final OrderHistoryModel orderHistoryModel = liseOrderHistoryModel.get(position);

            holder.orderDate.setText(orderHistoryModel.getOrderDate());
            holder.orderNumber.setText(orderHistoryModel.getOrderId());

            if (orderHistoryModel.getStatus().equalsIgnoreCase("1")) {
                holder.txtStatus.setText("Approved");
                holder.statusDot1.setImageResource(R.drawable.active_green);
                holder.statusDot2.setImageResource(R.drawable.active_dull);
                holder.statusDot3.setImageResource(R.drawable.active_dull);
            } else if (orderHistoryModel.getStatus().equalsIgnoreCase("4")) {
                holder.txtStatus.setText("Shipped");
                holder.statusDot1.setImageResource(R.drawable.active_green);
                holder.statusDot2.setImageResource(R.drawable.active_green);
                holder.statusDot3.setImageResource(R.drawable.active_dull);
            } else if (orderHistoryModel.getStatus().equalsIgnoreCase("5")) {
                holder.txtStatus.setText("Delivered");
                holder.statusDot1.setImageResource(R.drawable.active_green);
                holder.statusDot2.setImageResource(R.drawable.active_green);
                holder.statusDot3.setImageResource(R.drawable.active_green);
            }

            holder.btnViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DataAdapter.getInstance().setOrderHistoryModel(orderHistoryModel);

                    Intent intent = new Intent(getActivity(), OrderDetail.class);
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(getActivity());

                }
            });

            return convertView;
        }


        class ViewHolder {
            TextView orderDate;
            TextView orderNumber;
            TextView ship;
            TextView txtStatus;
            Button btnViewOrder;
            ImageView statusDot1, statusDot2, statusDot3;
        }
    }


    public void getOrderHistory() {
        ProgressDialogUtil.showProgressDialog(getActivity());
        String deviceid = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(getActivity());
        Map<String, String> param = new HashMap<String, String>();

        param.put("user_id", userId);

        NetworkAdaper.getInstance().getNetworkServices().getOrderHistoryList(param, new Callback<GerOrderHistoryModel>() {
            @Override
            public void success(GerOrderHistoryModel getOrderHistoryModel, Response response) {
                Log.e("Tab", getOrderHistoryModel.toString());
                if (getOrderHistoryModel.getSuccess()) {
                    List<OrderHistoryModel> liseOrderHistoryModelTemp = getOrderHistoryModel.getData();
                    liseOrderHistoryModel = getActiveOrder(liseOrderHistoryModelTemp);
                    if (liseOrderHistoryModel != null && liseOrderHistoryModel.size() != 0) {
                        adapter = new Adapter(getActivity(), liseOrderHistoryModel);
                        activeOrderList.setAdapter(adapter);
                        activeOrderList.setVisibility(View.VISIBLE);
                        no_record.setVisibility(View.GONE);
                    } else {
                        activeOrderList.setVisibility(View.GONE);
                        no_record.setVisibility(View.VISIBLE);
                    }
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    activeOrderList.setVisibility(View.GONE);
                    ProgressDialogUtil.hideProgressDialog();
                    Toast.makeText(getActivity(), "No Data found.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                Toast.makeText(getActivity(), "No Data found." + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<OrderHistoryModel> getActiveOrder(List<OrderHistoryModel> liseOrderHistoryModel) {

        List<OrderHistoryModel> filterList = new ArrayList<OrderHistoryModel>();

        if (liseOrderHistoryModel != null && liseOrderHistoryModel.size() != 0) {
            for (OrderHistoryModel orderHistoryModel : liseOrderHistoryModel) {
                if (!orderHistoryModel.getStatus().equals("0") &&
                        !orderHistoryModel.getStatus().equals("2") &&
                        !orderHistoryModel.getStatus().equals("3")) {
                    filterList.add(orderHistoryModel);
                }
            }
        }
        return filterList;
    }

}
